package com.vendo.search_service.test_utils;

import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem;
import com.vendo.search_service.adapter.product.out.nested.ElasticAddress;
import com.vendo.search_service.adapter.product.out.nested.ElasticSearchAttribute;
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
    private ElasticAddress address;

    private List<ElasticSearchAttribute> attributes;
    private List<String> images;

    private String ownerId;
    private String categoryId;

    private Boolean isNew;
    private Boolean active;

    private Instant createdAt;

    public static ElasticProductSearchItemDataBuilder withAllFields() {
        return new ElasticProductSearchItemDataBuilder()
                .id("product-1")
                .title("Gaming Laptop")
                .description("Powerful gaming laptop")
                .quantity(10)
                .isNew(true)
                .price(BigDecimal.valueOf(1500))
                .ownerId("owner-1")
                .address(new ElasticAddress("Lviv region", "Lviv", new ElasticAddress.ElasticLocation(49.8397, 24.0297)))
                .categoryId("category-1")
                .attributes(List.of(new ElasticSearchAttribute("id", List.of("red", "blue"))))
                .images(List.of("url1", "url2"))
                .active(true)
                .createdAt(Instant.parse("2024-01-01T00:00:00Z"));
    }

    public ElasticProductSearchItem build() {
        return new ElasticProductSearchItem(
                id, title, description, quantity, price, address, attributes, images, ownerId, categoryId, isNew, active, createdAt);
    }
}