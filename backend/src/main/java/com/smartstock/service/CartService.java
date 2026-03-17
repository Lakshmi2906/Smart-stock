package com.smartstock.service;

import com.smartstock.dto.CartAddRequest;
import com.smartstock.dto.CartResponse;
import com.smartstock.model.CartLock;
import com.smartstock.model.InventoryAudit;
import com.smartstock.model.Product;
import com.smartstock.repository.CartLockRepository;
import com.smartstock.repository.InventoryAuditRepository;
import com.smartstock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int LOCK_DURATION_MINUTES = 7;

    private final ProductRepository productRepository;
    private final CartLockRepository cartLockRepository;
    private final InventoryAuditRepository inventoryAuditRepository;

    /**
     * Core atomic cart-add operation.
     * Uses SERIALIZABLE isolation + pessimistic write lock to prevent
     * race conditions when multiple users add the same low-stock item.
     *
     * Stock >= 10  → normal add (decrement stock, no lock)
     * Stock < 10   → atomic lock (decrement stock, create 7-min lock row)
     * Stock == 0   → throws SoldOutException
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CartResponse.CartItem addToCart(String phone, CartAddRequest request) {
        int productId = request.getProductId();
        int qty = request.getQty() > 0 ? request.getQty() : 1;

        // Pessimistic write lock — blocks concurrent transactions on this product row
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (product.getStock() <= 0) {
            throw new RuntimeException("SOLD_OUT: " + product.getName() + " is out of stock.");
        }
        if (product.getStock() < qty) {
            throw new RuntimeException("INSUFFICIENT_STOCK: Only " + product.getStock()
                    + " units of " + product.getName() + " are available.");
        }

        boolean isLowStock = product.getStock() < LOW_STOCK_THRESHOLD;

        // Deduct stock atomically
        product.setStock(product.getStock() - qty);
        productRepository.save(product);

        InventoryAudit audit = InventoryAudit.builder()
                .productId(productId)
                .qtyChange(-qty)
                .action(isLowStock ? "LOCK_DEDUCT" : "CART_ADD")
                .notes("User: " + phone)
                .build();
        inventoryAuditRepository.save(audit);

        if (isLowStock) {
            // Check if user already has an active lock for this product — extend it instead
            boolean hasExistingLock = cartLockRepository
                    .existsByPhoneAndProductIdAndStatus(phone, productId, "active");

            if (hasExistingLock) {
                // Extend existing lock — return stock and update
                product.setStock(product.getStock() + qty);
                productRepository.save(product);
                throw new RuntimeException("ALREADY_LOCKED: You already have a lock on " + product.getName()
                        + ". Remove from cart first to change quantity.");
            }

            // Create atomic lock entry
            CartLock lock = CartLock.builder()
                    .phone(phone)
                    .productId(productId)
                    .qty(qty)
                    .lockExpires(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES))
                    .status("active")
                    .build();
            CartLock saved = cartLockRepository.save(lock);

            log.info("LOCK CREATED: user={} product={} qty={} expires={}",
                    phone, productId, qty, lock.getLockExpires());

            return CartResponse.CartItem.builder()
                    .lockId(saved.getId())
                    .productId(productId)
                    .name(product.getName())
                    .imageUrl(product.getImageUrl())
                    .qty(qty)
                    .price(product.getPrice())
                    .locked(true)
                    .lockExpires(lock.getLockExpires())
                    .remainingSeconds(lock.getRemainingSeconds())
                    .build();
        } else {
            // Normal cart add — no lock row needed
            return CartResponse.CartItem.builder()
                    .lockId(null)
                    .productId(productId)
                    .name(product.getName())
                    .imageUrl(product.getImageUrl())
                    .qty(qty)
                    .price(product.getPrice())
                    .locked(false)
                    .lockExpires(null)
                    .remainingSeconds(0)
                    .build();
        }
    }

    /**
     * Get all active cart items for a user.
     * Locked items include remaining timer data.
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(String phone) {
        List<CartLock> activeLocks = cartLockRepository.findActiveByPhone(phone);

        List<CartResponse.CartItem> items = activeLocks.stream().map(lock -> {
            Product product = productRepository.findById(lock.getProductId()).orElse(null);
            if (product == null) return null;

            boolean expired = lock.isExpired();
            return CartResponse.CartItem.builder()
                    .lockId(lock.getId())
                    .productId(lock.getProductId())
                    .name(product.getName())
                    .imageUrl(product.getImageUrl())
                    .qty(lock.getQty())
                    .price(product.getPrice())
                    .locked(true)
                    .lockExpires(lock.getLockExpires())
                    .remainingSeconds(expired ? 0 : lock.getRemainingSeconds())
                    .expired(expired)
                    .build();
        }).filter(item -> item != null).collect(Collectors.toList());

        java.math.BigDecimal subtotal = items.stream()
                .map(i -> i.getPrice().multiply(java.math.BigDecimal.valueOf(i.getQty())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .itemCount(items.stream().mapToInt(CartResponse.CartItem::getQty).sum())
                .subtotal(subtotal)
                .build();
    }

    /**
     * Remove an item from cart — returns stock.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeFromCart(String phone, Integer lockId) {
        CartLock lock = cartLockRepository.findByIdAndPhone(lockId, phone)
                .orElseThrow(() -> new RuntimeException("Cart item not found or unauthorized."));

        if (!"active".equals(lock.getStatus())) {
            throw new RuntimeException("This cart item is no longer active.");
        }

        lock.setStatus("expired");
        cartLockRepository.save(lock);

        // Return stock
        productRepository.returnStock(lock.getProductId(), lock.getQty());

        inventoryAuditRepository.save(InventoryAudit.builder()
                .productId(lock.getProductId())
                .qtyChange(lock.getQty())
                .action("LOCK_RETURN")
                .notes("User removed from cart: " + phone)
                .build());
    }

    /**
     * Extend an active lock by 7 more minutes.
     */
    @Transactional
    public long extendLock(String phone, Integer lockId) {
        CartLock lock = cartLockRepository.findByIdAndPhone(lockId, phone)
                .orElseThrow(() -> new RuntimeException("Lock not found or unauthorized."));

        if (!"active".equals(lock.getStatus()) || lock.isExpired()) {
            throw new RuntimeException("Lock has already expired. Please add item to cart again.");
        }

        LocalDateTime newExpiry = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
        cartLockRepository.extendLock(lockId, newExpiry);

        log.info("LOCK EXTENDED: user={} lockId={} newExpiry={}", phone, lockId, newExpiry);
        return java.time.Duration.between(LocalDateTime.now(), newExpiry).getSeconds();
    }
}
