package com.vendo.search_service.adapter.product.out;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
        @Field(type = FieldType.Date)
        Instant createdAt
) {

    public record ElasticSearchAttribute(
            String id,
            List<String> values) {
    }
}
