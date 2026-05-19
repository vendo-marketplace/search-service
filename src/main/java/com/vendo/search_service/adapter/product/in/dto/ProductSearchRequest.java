package com.vendo.search_service.adapter.product.in.dto;

import com.vendo.search_service.domain.product.sort.SortBody;

public record ProductSearchRequest(
        String categoryId,
        Boolean active,

        SortBody sort,

        AttributeFilterRequest attributeFilter,
        PriceRangeFilterRequest priceRangeFilter,

        Integer size,
        Integer page
) {
}
