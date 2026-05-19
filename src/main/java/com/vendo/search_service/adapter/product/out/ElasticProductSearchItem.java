package com.vendo.search_service.adapter.product.out;

import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(indexName = "products")
public record ElasticProductSearchItem(
        String id,
        String title,
        String description,
        Integer quantity,
        BigDecimal price,
        String ownerId,
        String categoryId,
        List<ElasticSearchAttribute> attributes,
        Boolean active,
        Instant createdAt
) {

    public record ElasticSearchAttribute(
            String id,
            List<String> values) {
    }
}
