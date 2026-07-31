package com.vendo.search_service.domain.product;

import com.vendo.search_service.domain.product.filter.AddressFilter;
import com.vendo.search_service.domain.product.filter.AttributeFilter;
import com.vendo.search_service.domain.product.filter.PriceRangeFilter;
import com.vendo.search_service.domain.product.sort.SortBody;

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
