package com.vendo.search_service.adapter.product.in.dto;

import com.vendo.search_service.domain.product.sort.SortBody;
import jakarta.validation.Valid;

public record ProductSearchRequest(
        String categoryId,
        Boolean active,

        SortBody sort,

        @Valid AttributeFilterRequest attributeFilter,
        @Valid PriceRangeFilterRequest priceRangeFilter,

        Integer size,
        Integer page
) {
}
