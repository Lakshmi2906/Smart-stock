package com.smartstock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank
    private String deliveryAddress;
    private String deliverySlot;
    private String paymentMethod; // razorpay | upi | cod
}
