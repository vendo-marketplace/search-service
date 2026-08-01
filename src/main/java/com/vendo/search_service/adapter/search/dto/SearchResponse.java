package com.vendo.search_service.adapter.search.dto;

import com.vendo.search_service.domain.search.SearchMetadata;

import java.util.List;

public record SearchResponse<T>(
        List<T> data,
        SearchMetadata metadata
) {
}
