package com.vendo.search_service.adapter.search;

import com.vendo.search_service.application.product.dto.ProductSearchRequest;

import java.util.List;

public interface SearchRepository<T> {

    List<T> search(String q, ProductSearchRequest request);

}
