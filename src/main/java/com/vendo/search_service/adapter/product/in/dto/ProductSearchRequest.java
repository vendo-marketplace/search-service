package com.vendo.search_service.adapter.product.in.dto;

public record ProductSearchRequest(

        String query,
//        ProductFilters filters,
//        SortRequest sort,
        int page,
        int size

) {
}
