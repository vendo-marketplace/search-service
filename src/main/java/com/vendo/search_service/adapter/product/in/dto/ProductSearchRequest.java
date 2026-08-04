package com.vendo.search_service.adapter.product.in.dto;

import com.vendo.search_service.domain.product.search.sort.SortBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

public record ProductSearchRequest(
        String categoryId,

        Boolean active,
        Boolean isNew,

        SortBody sort,

        @Valid
        AddressFilterRequest addressFilter,
        @Valid
        AttributeFilterRequest attributeFilter,
        @Valid
        PriceRangeFilterRequest priceRangeFilter,

        @Min(value = 1, message = "Page size must not be less than one.")
        Integer size,
        @Min(value = 1, message = "Page must not be less than one.")
        Integer page
) {
}
