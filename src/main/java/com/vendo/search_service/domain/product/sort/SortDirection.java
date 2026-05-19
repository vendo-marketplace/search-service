package com.vendo.search_service.domain.product.sort;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortDirection {

    ASC("asc"),
    DESC("desc");

    private final String direction;

}
