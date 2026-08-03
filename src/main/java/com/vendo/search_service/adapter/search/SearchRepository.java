package com.vendo.search_service.adapter.search;

import com.vendo.search_service.adapter.search.dto.SearchResponse;

public interface SearchRepository<T, R> {

    SearchResponse<T> search(String q, R request);

}
