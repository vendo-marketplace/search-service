package com.vendo.search_service.adapter.product.in.dto;

import com.vendo.search_service.domain.product.sort.SortBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

public record ProductSearchRequest(
        String categoryId,
        Boolean active,

        SortBody sort,

        @Valid
        AttributeFilterRequest attributeFilter,

        @Valid
        PriceRangeFilterRequest priceRangeFilter,

        @Min(value = 1, message = "Page size must not be less than one.")
        Integer size,
        @Min(value = 0, message = "Page must not be less than zero.")
        Integer page
) {
}
