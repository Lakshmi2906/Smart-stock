package com.smartstock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_audit")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "admin_phone")
    private String adminPhone;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "qty_change", nullable = false)
    private Integer qtyChange;

    @Column(nullable = false)
    private String action; // REFILL, ADD_PRODUCT, FLASH_SALE_ON, FLASH_SALE_OFF, LOCK_DEDUCT, LOCK_RETURN

    private String notes;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }
}
