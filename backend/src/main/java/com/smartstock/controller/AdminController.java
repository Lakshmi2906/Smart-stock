package com.smartstock.controller;

import com.smartstock.dto.AdminProductRequest;
import com.smartstock.dto.AdminRefillRequest;
import com.smartstock.service.AdminService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(Map.of("success", true, "data", adminService.getDashboard()));
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@Valid @RequestBody AdminProductRequest req, Principal principal) {
        try {
            var product = adminService.addProduct(principal.getName(), req);
            return ResponseEntity.ok(Map.of("success", true, "data", product, "message",
                    "Product '" + product.getName() + "' added with ID " + product.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/refill")
    public ResponseEntity<?> refillStock(@Valid @RequestBody AdminRefillRequest req, Principal principal) {
        try {
            var product = adminService.refillStock(principal.getName(), req);
            return ResponseEntity.ok(Map.of("success", true, "data", product,
                    "message", "Stock refilled. New stock: " + product.getStock()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/products/{id}/flash-sale")
    public ResponseEntity<?> toggleFlashSale(@PathVariable Integer id,
                                              @RequestBody FlashSaleRequest req,
                                              Principal principal) {
        try {
            var product = adminService.toggleFlashSale(principal.getName(), id, req.isEnabled());
            return ResponseEntity.ok(Map.of("success", true, "data", product,
                    "message", "Flash sale " + (req.isEnabled() ? "enabled" : "disabled")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/audit")
    public ResponseEntity<?> getAuditLog(@RequestParam(required = false) Integer productId) {
        return ResponseEntity.ok(Map.of("success", true, "data", adminService.getAuditLog(productId)));
    }

    @Data static class FlashSaleRequest { private boolean enabled; }
}
