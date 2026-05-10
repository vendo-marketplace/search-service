package com.vendo.search_service.domain.product;

import java.math.BigDecimal;

public record ProductSearchItem(

        String categoryId,
        Boolean active,

        AttributeFilter attributeFilter,

        BigDecimal minPrice,
        BigDecimal maxPrice,

        Integer size,
        Integer page

) {
}
