package com.vendo.search_service.test_utils;

import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem;
import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem.ElasticSearchAttribute;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Setter
@Accessors(fluent = true, chain = true)
public class ElasticProductSearchItemDataBuilder {

    private String id;
    private String title;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private String ownerId;
    private String categoryId;
    private List<ElasticSearchAttribute> attributes;
    private Boolean active;
    private Instant createdAt;

    public static ElasticProductSearchItemDataBuilder withAllFields() {
        return new ElasticProductSearchItemDataBuilder()
                .id("product-1")
                .title("Gaming Laptop")
                .description("Powerful gaming laptop")
                .quantity(10)
                .price(BigDecimal.valueOf(1500))
                .ownerId("owner-1")
                .categoryId("category-1")
                .attributes(List.of(new ElasticSearchAttribute("color", List.of("red", "blue"))))
                .active(true)
                .createdAt(Instant.parse("2024-01-01T00:00:00Z"));
    }

    public ElasticProductSearchItem build() {
        return new ElasticProductSearchItem(
                id, title, description, quantity, price, ownerId, categoryId, attributes, active, createdAt);
    }
}