package com.vendo.search_service.domain.product;

import com.vendo.search_service.domain.product.filter.AttributeFilter;
import com.vendo.search_service.domain.product.filter.PriceRangeFilter;
import com.vendo.search_service.domain.product.sort.SortBody;

public record ProductSearchItem(

        String categoryId,
        Boolean active,

        SortBody sort,

        AttributeFilter attributeFilter,
        PriceRangeFilter priceRangeFilter,

        Integer size,
        Integer page

) {
}
