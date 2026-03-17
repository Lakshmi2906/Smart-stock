package com.smartstock.controller;

import com.smartstock.model.Product;
import com.smartstock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        List<Product> products;

        if (search != null && !search.isBlank()) {
            products = productRepository.search(search.trim());
        } else if (category != null && !category.isBlank() && !"All Products".equalsIgnoreCase(category)) {
            products = productRepository.findByCategoryIgnoreCase(category);
        } else {
            products = productRepository.findAll();
        }

        return ResponseEntity.ok(Map.of("success", true, "data", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {
        return productRepository.findById(id)
                .map(p -> ResponseEntity.ok(Map.of("success", true, "data", p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flash-sale")
    public ResponseEntity<?> getFlashSaleProducts() {
        return ResponseEntity.ok(Map.of("success", true, "data", productRepository.findByIsFlashSaleTrue()));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(Map.of("success", true, "data", productRepository.findLowStockProducts(threshold)));
    }
}
