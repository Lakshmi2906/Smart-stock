package com.smartstock.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class CartResponse {
    private List<CartItem> items;
    private int itemCount;
    private BigDecimal subtotal;

    @Data @Builder
    public static class CartItem {
        private Integer lockId;
        private Integer productId;
        private String name;
        private String imageUrl;
        private int qty;
        private BigDecimal price;
        private boolean locked;
        private LocalDateTime lockExpires;
        private long remainingSeconds;
        private boolean expired;
    }
}
