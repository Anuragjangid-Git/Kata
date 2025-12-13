package com.backend.Kata.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchSweetRequest {
    private String name;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}

