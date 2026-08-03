package com.vendo.search_service.domain.search;

public record SearchMetadata(
        int page,
        int size,
        long totalPages,
        long totalElements,
        boolean hasPrevious,
        boolean hasNext
) {

    public static SearchMetadata fromDefault(int page, int size) {
        return new SearchMetadata(
                page,
                size,
                0,
                0,
                false,
                false
        );
    }
}
