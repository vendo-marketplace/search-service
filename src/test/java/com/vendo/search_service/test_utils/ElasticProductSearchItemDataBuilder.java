package com.vendo.search_service.test_utils;

import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem;
import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem.ElasticSearchAttribute;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class ElasticProductSearchItemDataBuilder {

    private String id = "product-1";
    private String title = "Gaming Laptop";
    private String description = "Powerful gaming laptop";
    private Integer quantity = 10;
    private BigDecimal price = BigDecimal.valueOf(1500);
    private String ownerId = "owner-1";
    private String categoryId = "category-1";
    private List<ElasticSearchAttribute> attributes = List.of(new ElasticSearchAttribute("color", List.of("red", "blue")));
    private Boolean active = true;
    private Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

    public static ElasticProductSearchItemDataBuilder withAllFields() {
        return new ElasticProductSearchItemDataBuilder();
    }

    public ElasticProductSearchItemDataBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ElasticProductSearchItemDataBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ElasticProductSearchItemDataBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ElasticProductSearchItemDataBuilder quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public ElasticProductSearchItemDataBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ElasticProductSearchItemDataBuilder ownerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public ElasticProductSearchItemDataBuilder categoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public ElasticProductSearchItemDataBuilder attributes(List<ElasticSearchAttribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public ElasticProductSearchItemDataBuilder active(Boolean active) {
        this.active = active;
        return this;
    }

    public ElasticProductSearchItemDataBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ElasticProductSearchItem build() {
        return new ElasticProductSearchItem(
                id, title, description, quantity, price, ownerId, categoryId, attributes, active, createdAt);
    }
}