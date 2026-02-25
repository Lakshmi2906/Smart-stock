package com.smartstock.controller;

import com.smartstock.dto.OrderRequest;
import com.smartstock.dto.PaymentVerifyRequest;
import com.smartstock.model.Order;
import com.smartstock.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /** Create order + Razorpay payment intent */
    @PostMapping("/api/orders")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest req, Principal principal) {
        try {
            var result = orderService.createOrderAndPaymentIntent(principal.getName(), req);
            return ResponseEntity.ok(Map.of("success", true, "data", result.toMap()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /** Get user's order history */
    @GetMapping("/api/orders")
    public ResponseEntity<?> getMyOrders(Principal principal) {
        List<Order> orders = orderService.getOrdersForUser(principal.getName());
        return ResponseEntity.ok(Map.of("success", true, "data", orders));
    }

    /** Verify Razorpay payment and finalize order */
    @PostMapping("/api/payments/razorpay")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody PaymentVerifyRequest req, Principal principal) {
        try {
            Order order = orderService.confirmPayment(principal.getName(), req);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment confirmed! Order #" + order.getId() + " placed successfully.",
                    "orderId", order.getId(),
                    "total", order.getTotal()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
