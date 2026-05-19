package com.vendo.search_service.domain.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Product(
        String id,
        String title,
        String description,
        Integer quantity,
        BigDecimal price,
        String ownerId,
        String categoryId,
        List<Attribute> attributes,
        Boolean active,
        LocalDateTime createdAt
        ) {

    public record Attribute(
            String id,
            List<String> values) {
    }

}
