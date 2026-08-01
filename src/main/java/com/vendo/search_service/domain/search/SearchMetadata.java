package com.vendo.search_service.domain.search;

public record SearchMetadata(
        int page,
        int size,
        long totalPages,
        long totalElements,
        boolean hasPrevious,
        boolean hasNext
) {
}
