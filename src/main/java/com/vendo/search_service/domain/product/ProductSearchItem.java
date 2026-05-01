package com.vendo.search_service.domain.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductSearchItem(
        String id,
        String title,
        String description,
        Integer quantity,
        BigDecimal price,
        String ownerId,
        String categoryId,
        List<SearchAttribute> attributes,
        Boolean active) {

    public record SearchAttribute(
            String title,
            String type,
            List<String> values) {
    }

}
