package com.smartstock.service;

import com.smartstock.dto.OrderRequest;
import com.smartstock.dto.PaymentVerifyRequest;
import com.smartstock.model.CartLock;
import com.smartstock.model.InventoryAudit;
import com.smartstock.model.Order;
import com.smartstock.model.Product;
import com.smartstock.repository.CartLockRepository;
import com.smartstock.repository.InventoryAuditRepository;
import com.smartstock.repository.OrderRepository;
import com.smartstock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final CartLockRepository cartLockRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryAuditRepository inventoryAuditRepository;
    private final PaymentService paymentService;

    /**
     * Step 1: Create order & Razorpay payment intent.
     * Validates all locks are still active before proceeding.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JSONObject createOrderAndPaymentIntent(String phone, OrderRequest request) {
        List<CartLock> activeLocks = cartLockRepository.findActiveByPhone(phone);

        if (activeLocks.isEmpty()) {
            throw new RuntimeException("Cart is empty. Add items before checkout.");
        }

        // Validate no locks have expired
        List<CartLock> expiredLocks = activeLocks.stream()
                .filter(CartLock::isExpired).collect(Collectors.toList());
        if (!expiredLocks.isEmpty()) {
            throw new RuntimeException("Some cart items have expired. Please re-add them.");
        }

        // Calculate total
        BigDecimal subtotal = activeLocks.stream().map(lock -> {
            Product p = productRepository.findById(lock.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + lock.getProductId()));
            return p.getPrice().multiply(BigDecimal.valueOf(lock.getQty()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal delivery = subtotal.compareTo(new BigDecimal("499")) > 0
                ? BigDecimal.ZERO : new BigDecimal("40");
        BigDecimal total = subtotal.add(delivery);

        // Create pending order
        List<Order.OrderItem> items = activeLocks.stream().map(lock -> {
            Product p = productRepository.findById(lock.getProductId()).orElseThrow();
            return Order.OrderItem.builder()
                    .productId(lock.getProductId())
                    .name(p.getName())
                    .imageUrl(p.getImageUrl())
                    .qty(lock.getQty())
                    .price(p.getPrice())
                    .build();
        }).collect(Collectors.toList());

        Order order = Order.builder()
                .phone(phone)
                .items(items)
                .total(total)
                .status("pending")
                .deliveryAddress(request.getDeliveryAddress())
                .deliverySlot(request.getDeliverySlot())
                .build();
        Order savedOrder = orderRepository.save(order);

        // Create Razorpay order
        JSONObject rzpOrder = paymentService.createRazorpayOrder(
                total, "SSE-" + savedOrder.getId());

        // Update order with Razorpay order ID
        savedOrder.setRazorpayOrderId(rzpOrder.getString("razorpay_order_id"));
        orderRepository.save(savedOrder);

        rzpOrder.put("order_id", savedOrder.getId());
        rzpOrder.put("total", total);
        rzpOrder.put("item_count", items.size());

        return rzpOrder;
    }

    /**
     * Step 2: Verify Razorpay payment signature and finalize order.
     * - Validates HMAC-SHA256 signature
     * - Marks all locks as checked_out (stock already deducted during locking)
     * - Updates order status to paid
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order confirmPayment(String phone, PaymentVerifyRequest req) {
        // Verify payment signature
        if (!paymentService.verifyPaymentSignature(req)) {
            throw new RuntimeException("PAYMENT_INVALID: Payment signature verification failed.");
        }

        Order order = orderRepository.findByRazorpayOrderId(req.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found for Razorpay ID: "
                        + req.getRazorpayOrderId()));

        if (!order.getPhone().equals(phone)) {
            throw new RuntimeException("Unauthorized: Order does not belong to this user.");
        }

        if ("paid".equals(order.getStatus())) {
            return order; // Idempotent — safe to call twice
        }

        // Mark all active locks as checked_out
        List<CartLock> activeLocks = cartLockRepository.findActiveByPhone(phone);
        activeLocks.forEach(lock -> {
            lock.setStatus("checked_out");
            cartLockRepository.save(lock);

            // Log permanent stock deduction
            inventoryAuditRepository.save(InventoryAudit.builder()
                    .productId(lock.getProductId())
                    .qtyChange(-lock.getQty())
                    .action("CHECKOUT_DEDUCT")
                    .notes("Order #" + order.getId() + " | Phone: " + phone)
                    .build());
        });

        order.setStatus("paid");
        order.setRazorpayPaymentId(req.getRazorpayPaymentId());
        orderRepository.save(order);

        log.info("ORDER CONFIRMED: id={} phone={} total={} payment={}",
                order.getId(), phone, order.getTotal(), req.getRazorpayPaymentId());

        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersForUser(String phone) {
        return orderRepository.findByPhoneOrderByCreatedAtDesc(phone);
    }
}
