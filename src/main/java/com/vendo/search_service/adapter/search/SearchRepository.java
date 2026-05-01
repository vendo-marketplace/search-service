package com.vendo.search_service.adapter.search;

import java.util.List;

public interface SearchRepository<T> {

    List<T> search(String query);

}
