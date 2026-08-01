package com.vendo.search_service.domain.product.search;

import com.vendo.search_service.domain.product.search.filter.AddressFilter;
import com.vendo.search_service.domain.product.search.filter.AttributeFilter;
import com.vendo.search_service.domain.product.search.filter.PriceRangeFilter;
import com.vendo.search_service.domain.product.search.sort.SortBody;

public record ProductSearchItem(

        String categoryId,
        Boolean active,
        Boolean isNew,

        SortBody sort,

        AddressFilter addressFilter,
        AttributeFilter attributeFilter,
        PriceRangeFilter priceRangeFilter,

        Integer size,
        Integer page

) {
}
