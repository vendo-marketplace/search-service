package com.vendo.search_service.adapter.product.in.dto;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record PriceRangeFilterRequest(
        @Min(value = 0, message = "Minimal price range is 0.")
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
