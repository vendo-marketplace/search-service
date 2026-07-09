package com.vendo.search_service.adapter.product.in.dto;

import com.vendo.search_service.adapter.product.in.annotation.ValidPriceRange;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

@ValidPriceRange
public record PriceRangeFilterRequest(
        @Min(value = 0, message = "Minimal price must not be less than zero.")
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
