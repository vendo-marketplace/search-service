package com.vendo.search_service.domain.product.search.sort;

public record SortBody(

        ProductSortField sortBy,
        SortDirection direction

) {
}
