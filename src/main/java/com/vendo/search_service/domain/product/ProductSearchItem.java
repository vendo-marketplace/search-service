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
        List<ElasticAttribute> attributes,
        Boolean active) {

    record ElasticAttribute(
            String title,
            String type,
            List<String> values) {
    }

}
