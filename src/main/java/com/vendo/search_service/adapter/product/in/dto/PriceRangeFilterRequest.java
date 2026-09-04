package com.vendo.search_service.adapter.product.in.dto;

import com.vendo.core_lib.annotations.price.ValidPriceRange;
import com.vendo.core_lib.dto.request.PriceRange;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@ValidPriceRange
public record PriceRangeFilterRequest(
        @DecimalMin(value = "0", message = "Minimal price must not be less than 0.")
        BigDecimal minPrice,

        @DecimalMin(value = "0", message = "Maximum price must not be less than 0.")
        BigDecimal maxPrice
) implements PriceRange {

        @Override
        public BigDecimal getMinPrice() {
                return minPrice;
        }

        @Override
        public BigDecimal getMaxPrice() {
                return maxPrice;
        }

        public static PriceRangeFilterRequest from(BigDecimal minPrice, BigDecimal maxPrice) {
                return new PriceRangeFilterRequest(minPrice, maxPrice);
        }
}

