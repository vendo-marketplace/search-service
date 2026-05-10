package com.vendo.search_service.adapter.product.in.dto;

import java.math.BigDecimal;

public record ProductSearchRequest(

        String categoryId,
        Boolean active,

        AttributeFilterRequest attributeFilter,

        BigDecimal minPrice,
        BigDecimal maxPrice,

        Integer size,
        Integer page

) {

}
