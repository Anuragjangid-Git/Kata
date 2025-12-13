package com.backend.Kata.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateSweetRequest {

    private String name;
    private String category;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}

