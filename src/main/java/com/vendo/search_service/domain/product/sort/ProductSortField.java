package com.vendo.search_service.domain.product.sort;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductSortField {

    CREATED_AT("createdAt"),
    PRICE("price");

    private final String field;

}
