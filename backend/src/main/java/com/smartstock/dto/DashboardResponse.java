package com.smartstock.dto;

import com.smartstock.model.Product;
import com.smartstock.model.InventoryAudit;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class DashboardResponse {
    private long activeLocks;
    private long lowStockCount;
    private long outOfStockCount;
    private long totalOrders;
    private BigDecimal revenueToday;
    private BigDecimal revenueTotal;
    private List<Product> lowStockProducts;
    private List<InventoryAudit> recentAuditLogs;
}
