package com.vendo.search_service.domain.product.search;

import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.search.SearchMetadata;

import java.util.List;

public record ProductSearchData(
        List<Product> data,
        SearchMetadata metadata
) {
}
