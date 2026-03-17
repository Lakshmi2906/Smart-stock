package com.smartstock.service;

import com.smartstock.dto.AdminProductRequest;
import com.smartstock.dto.AdminRefillRequest;
import com.smartstock.dto.DashboardResponse;
import com.smartstock.model.InventoryAudit;
import com.smartstock.model.Product;
import com.smartstock.repository.CartLockRepository;
import com.smartstock.repository.InventoryAuditRepository;
import com.smartstock.repository.OrderRepository;
import com.smartstock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final ProductRepository productRepository;
    private final CartLockRepository cartLockRepository;
    private final OrderRepository orderRepository;
    private final InventoryAuditRepository inventoryAuditRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        long activeLocks = cartLockRepository.countActiveLocks();
        List<Product> lowStock = productRepository.findLowStockProducts(10);
        List<Product> outOfStock = productRepository.findOutOfStockProducts();
        long totalOrders = orderRepository.countPaidOrders();
        var revenueToday = orderRepository.sumRevenueForPeriod(LocalDateTime.now().minusDays(1));
        var revenueTotal = orderRepository.sumRevenueForPeriod(LocalDateTime.now().minusYears(10));

        return DashboardResponse.builder()
                .activeLocks(activeLocks)
                .lowStockCount((long) lowStock.size())
                .outOfStockCount((long) outOfStock.size())
                .totalOrders(totalOrders)
                .revenueToday(revenueToday)
                .revenueTotal(revenueTotal)
                .lowStockProducts(lowStock)
                .recentAuditLogs(inventoryAuditRepository.findAllByOrderByTimestampDesc(
                        PageRequest.of(0, 20)).getContent())
                .build();
    }

    @Transactional
    public Product addProduct(String adminPhone, AdminProductRequest req) {
        Product product = Product.builder()
                .name(req.getName())
                .weight(req.getWeight())
                .price(req.getPrice())
                .originalPrice(req.getOriginalPrice() != null ? req.getOriginalPrice() : req.getPrice())
                .stock(req.getStock())
                .category(req.getCategory())
                .imageUrl(req.getImageUrl())
                .isFlashSale(false)
                .build();

        Product saved = productRepository.save(product);

        inventoryAuditRepository.save(InventoryAudit.builder()
                .adminPhone(adminPhone)
                .productId(saved.getId())
                .qtyChange(req.getStock())
                .action("ADD_PRODUCT")
                .notes("New product: " + req.getName())
                .build());

        log.info("ADMIN {} added product: {} (id={})", adminPhone, req.getName(), saved.getId());
        return saved;
    }

    @Transactional
    public Product refillStock(String adminPhone, AdminRefillRequest req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        int oldStock = product.getStock();
        product.setStock(oldStock + req.getQty());
        Product saved = productRepository.save(product);

        inventoryAuditRepository.save(InventoryAudit.builder()
                .adminPhone(adminPhone)
                .productId(req.getProductId())
                .qtyChange(req.getQty())
                .action("REFILL")
                .notes("Refill by " + adminPhone + ": " + oldStock + " → " + product.getStock())
                .build());

        log.info("ADMIN {} refilled product {} by {} units (now: {})",
                adminPhone, req.getProductId(), req.getQty(), product.getStock());
        return saved;
    }

    @Transactional
    public Product toggleFlashSale(String adminPhone, Integer productId, boolean enabled) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.setFlashSale(enabled);
        Product saved = productRepository.save(product);

        inventoryAuditRepository.save(InventoryAudit.builder()
                .adminPhone(adminPhone)
                .productId(productId)
                .qtyChange(0)
                .action(enabled ? "FLASH_SALE_ON" : "FLASH_SALE_OFF")
                .notes("Flash sale " + (enabled ? "enabled" : "disabled") + " by " + adminPhone)
                .build());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<InventoryAudit> getAuditLog(Integer productId) {
        if (productId != null) {
            return inventoryAuditRepository.findByProductIdOrderByTimestampDesc(productId);
        }
        return inventoryAuditRepository.findAllByOrderByTimestampDesc(
                PageRequest.of(0, 100)).getContent();
    }
}
