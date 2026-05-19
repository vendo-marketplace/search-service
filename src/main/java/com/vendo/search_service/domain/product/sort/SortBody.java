package com.vendo.search_service.domain.product.sort;

public record SortBody(

        ProductSortField sortBy,
        SortDirection direction

) {
}
