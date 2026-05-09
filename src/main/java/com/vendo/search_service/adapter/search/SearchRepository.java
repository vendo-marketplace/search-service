package com.vendo.search_service.adapter.search;

import java.util.List;

public interface SearchRepository<T, R> {

    List<T> search(String q, R request);

}
