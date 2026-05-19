package com.vendo.search_service.adapter.product.in.dto;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record PriceRangeFilterRequest(
        @Min(value = 0, message = "Min price range is minimum 0.")
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
