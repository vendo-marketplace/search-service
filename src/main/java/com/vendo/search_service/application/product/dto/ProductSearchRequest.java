package com.vendo.search_service.application.product.dto;

import com.vendo.search_service.domain.search.AttributeFilter;

import java.math.BigDecimal;

public record ProductSearchRequest(

        String categoryId,
        Boolean active,

        AttributeFilter attributeFilter,

        BigDecimal minPrice,
        BigDecimal maxPrice,

        Integer size,
        Integer page

) {

}
