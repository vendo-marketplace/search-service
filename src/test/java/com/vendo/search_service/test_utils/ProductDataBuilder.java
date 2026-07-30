package com.vendo.search_service.test_utils;

import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.Product.Attribute;
import com.vendo.search_service.domain.product.nested.Address;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Accessors(fluent = true, chain = true)
public class ProductDataBuilder {

    private String id;

    private String title;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private List<Attribute> attributes;
    private List<String> images;
    private Address address;

    private String ownerId;
    private String categoryId;

    private Boolean isNew;
    private Boolean active;

    private LocalDateTime createdAt;

    public static ProductDataBuilder withAllFields() {
        return new ProductDataBuilder()
                .id("product-1")
                .title("Gaming Laptop")
                .description("Powerful gaming laptop")
                .quantity(10)
                .isNew(true)
                .price(BigDecimal.valueOf(1500))
                .ownerId("owner-1")
                .categoryId("category-1")
                .address(new Address("Lviv region", "Lviv", new Address.Location(49.8397, 24.0297)))
                .attributes(List.of(new Attribute("color", List.of("red", "blue"))))
                .images(List.of("url1", "url2"))
                .active(true)
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0));
    }

    public Product build() {
        return new Product(
                id, title, description, quantity, price, attributes, images, address, isNew, active, ownerId, categoryId, createdAt);
    }
}