package com.smartstock.controller;

import com.smartstock.dto.CartAddRequest;
import com.smartstock.dto.CartResponse;
import com.smartstock.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(Principal principal) {
        try {
            CartResponse cart = cartService.getCart(principal.getName());
            return ResponseEntity.ok(Map.of("success", true, "data", cart));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartAddRequest req, Principal principal) {
        try {
            CartResponse.CartItem item = cartService.addToCart(principal.getName(), req);
            String msg = item.isLocked()
                    ? "Item locked for 7 minutes! Complete checkout before timer expires."
                    : "Item added to cart successfully.";
            return ResponseEntity.ok(Map.of("success", true, "data", item, "message", msg));
        } catch (Exception e) {
            String error = e.getMessage();
            String code = error.startsWith("SOLD_OUT") ? "SOLD_OUT"
                    : error.startsWith("INSUFFICIENT_STOCK") ? "INSUFFICIENT_STOCK"
                    : "ERROR";
            return ResponseEntity.badRequest().body(Map.of("success", false, "code", code, "error", error));
        }
    }

    @DeleteMapping("/{lockId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Integer lockId, Principal principal) {
        try {
            cartService.removeFromCart(principal.getName(), lockId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Item removed and stock returned."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/extend/{lockId}")
    public ResponseEntity<?> extendLock(@PathVariable Integer lockId, Principal principal) {
        try {
            long remainingSecs = cartService.extendLock(principal.getName(), lockId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lock extended by 7 minutes.",
                    "remainingSeconds", remainingSecs
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
