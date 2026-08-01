package com.vendo.search_service.domain.product.search.sort;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortDirection {

    ASC("Asc"),
    DESC("Desc");

    private final String direction;

}
