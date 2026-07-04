package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.product.out.nested.ElasticSearchAttribute;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(indexName = "products")
public record ElasticProductSearchItem(

        @Id
        String id,

        @Field(type = FieldType.Text)
        String title,

        @Field(type = FieldType.Text)
        String description,

        @Field(type = FieldType.Integer)
        Integer quantity,

        @Field(type = FieldType.Double)
        BigDecimal price,

        @Field(type = FieldType.Keyword)
        String ownerId,

        @Field(type = FieldType.Keyword)
        String categoryId,

        @Field(type = FieldType.Nested)
        List<ElasticSearchAttribute> attributes,

        @Field(type = FieldType.Boolean)
        Boolean active,

        @Field(type = FieldType.Date, format = DateFormat.date_time)
        Instant createdAt
) {

}
