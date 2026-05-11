package com.vendo.search_service.adapter.product.out.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class  ProductSearchFields {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String ACTIVE = "active";
    public static final String CATEGORY_ID = "categoryId";
    public static final String ATTRIBUTES = "attributes";
    public static final String ATTRIBUTES_ID = "attributes.id";
    public static final String ATTRIBUTES_VALUES = "attributes.values";

}
