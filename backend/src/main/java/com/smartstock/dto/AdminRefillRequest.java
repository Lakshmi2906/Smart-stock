package com.smartstock.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AdminRefillRequest {
    @NotNull private Integer productId;
    @Min(1) private int qty;
}
