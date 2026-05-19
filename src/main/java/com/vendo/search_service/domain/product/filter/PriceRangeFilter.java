package com.vendo.search_service.domain.product.filter;

import java.math.BigDecimal;

public record PriceRangeFilter(
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
