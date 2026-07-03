package com.vendo.search_service.adapter.product.out.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class ProductSearchFields {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String ACTIVE = "active";
    public static final String CATEGORY_ID = "categoryId";
    public static final String ATTRIBUTES = "attributes";
    public static final String ATTRIBUTES_ID = ATTRIBUTES + ".id";
    public static final String ATTRIBUTES_VALUES = ATTRIBUTES + ".values";

    private static final String PRIORITY_PREFIX = "^";

    public static String withPriority(String field, int priority) {
        return field + PRIORITY_PREFIX + priority;
    }

}
