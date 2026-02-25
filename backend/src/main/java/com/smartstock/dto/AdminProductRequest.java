package com.smartstock.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdminProductRequest {
    @NotBlank private String name;
    private String weight;
    @NotNull @DecimalMin("0.01") private BigDecimal price;
    private BigDecimal originalPrice;
    @NotNull @Min(0) private Integer stock;
    @NotBlank private String category;
    private String imageUrl;
}
