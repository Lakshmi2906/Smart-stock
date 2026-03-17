package com.smartstock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_locks", indexes = {
    @Index(name = "idx_cart_locks_phone", columnList = "phone"),
    @Index(name = "idx_cart_locks_product", columnList = "product_id"),
    @Index(name = "idx_cart_locks_status", columnList = "status"),
    @Index(name = "idx_cart_locks_expires", columnList = "lock_expires")
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String phone;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private Integer qty;

    @Column(name = "lock_expires")
    private LocalDateTime lockExpires;

    @Column(nullable = false)
    private String status; // active, expired, checked_out

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "active";
        if (this.lockExpires == null) {
            this.lockExpires = LocalDateTime.now().plusMinutes(7);
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.lockExpires);
    }

    public long getRemainingSeconds() {
        if (isExpired()) return 0;
        return java.time.Duration.between(LocalDateTime.now(), lockExpires).getSeconds();
    }
}
