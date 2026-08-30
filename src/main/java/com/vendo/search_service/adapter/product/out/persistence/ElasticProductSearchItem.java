package com.vendo.search_service.adapter.product.out.persistence;

import com.vendo.search_service.adapter.product.out.persistence.nested.ElasticAddress;
import com.vendo.search_service.adapter.product.out.persistence.nested.ElasticSearchAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Document(indexName = "products")
public class ElasticProductSearchItem {

        @Id
        private String id;

        @Field(type = FieldType.Text)
        private String title;
        @Field(type = FieldType.Text)
        private String description;
        @Field(type = FieldType.Integer)
        private Integer quantity;
        @Field(type = FieldType.Double)
        private BigDecimal price;
        @Field(type = FieldType.Object)
        private ElasticAddress address;

        @Field(type = FieldType.Nested)
        private List<ElasticSearchAttribute> attributes;
        @Field(type = FieldType.Keyword)
        private List<String> images;

        @Field(type = FieldType.Keyword)
        private String ownerId;
        @Field(type = FieldType.Keyword)
        private String categoryId;

        @Field(type = FieldType.Boolean)
        private Boolean isNew;
        @Field(type = FieldType.Boolean)
        private Boolean active;

        @Field(type = FieldType.Date)
        private Instant createdAt;

}
