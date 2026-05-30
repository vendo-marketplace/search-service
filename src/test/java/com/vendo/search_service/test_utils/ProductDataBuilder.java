package com.vendo.search_service.test_utils;

import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.Product.Attribute;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDataBuilder {

    private String id = "product-1";
    private String title = "Gaming Laptop";
    private String description = "Powerful gaming laptop";
    private Integer quantity = 10;
    private BigDecimal price = BigDecimal.valueOf(1500);
    private String ownerId = "owner-1";
    private String categoryId = "category-1";
    private List<Attribute> attributes = List.of(new Attribute("color", List.of("red", "blue")));
    private Boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 0, 0);

    public static ProductDataBuilder withAllFields() {
        return new ProductDataBuilder();
    }

    public ProductDataBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ProductDataBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ProductDataBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProductDataBuilder quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductDataBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductDataBuilder ownerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public ProductDataBuilder categoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public ProductDataBuilder attributes(List<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public ProductDataBuilder active(Boolean active) {
        this.active = active;
        return this;
    }

    public ProductDataBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Product build() {
        return new Product(id, title, description, quantity, price, ownerId, categoryId, attributes, active, createdAt);
    }
}