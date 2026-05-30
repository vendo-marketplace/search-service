package com.vendo.search_service.adapter.product.out.mapper;

import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem;
import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem.ElasticSearchAttribute;
import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.test_utils.ElasticProductSearchItemDataBuilder;
import com.vendo.search_service.test_utils.ProductDataBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ElasticProductMapperTest {

    private final ElasticProductMapper mapper = Mappers.getMapper(ElasticProductMapper.class);

    private Product map(ElasticProductSearchItem source) {
        List<Product> products = mapper.toProducts(List.of(source));
        assertThat(products).hasSize(1);
        return products.get(0);
    }

    @Test
    void mapsId() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().id("custom-id").build();
        Product expected = ProductDataBuilder.withAllFields().id("custom-id").build();

        assertThat(map(source).id()).isEqualTo(expected.id());
    }

    @Test
    void mapsTitle() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().title("Custom Title").build();
        Product expected = ProductDataBuilder.withAllFields().title("Custom Title").build();

        assertThat(map(source).title()).isEqualTo(expected.title());
    }

    @Test
    void mapsDescription() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().description("Custom desc").build();
        Product expected = ProductDataBuilder.withAllFields().description("Custom desc").build();

        assertThat(map(source).description()).isEqualTo(expected.description());
    }

    @Test
    void mapsQuantity() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().quantity(42).build();
        Product expected = ProductDataBuilder.withAllFields().quantity(42).build();

        assertThat(map(source).quantity()).isEqualTo(expected.quantity());
    }

    @Test
    void mapsPrice() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().price(BigDecimal.valueOf(99.99)).build();
        Product expected = ProductDataBuilder.withAllFields().price(BigDecimal.valueOf(99.99)).build();

        assertThat(map(source).price()).isEqualByComparingTo(expected.price());
    }

    @Test
    void mapsOwnerId() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().ownerId("owner-99").build();
        Product expected = ProductDataBuilder.withAllFields().ownerId("owner-99").build();

        assertThat(map(source).ownerId()).isEqualTo(expected.ownerId());
    }

    @Test
    void mapsCategoryId() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().categoryId("cat-99").build();
        Product expected = ProductDataBuilder.withAllFields().categoryId("cat-99").build();

        assertThat(map(source).categoryId()).isEqualTo(expected.categoryId());
    }

    @Test
    void mapsActive() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().active(false).build();
        Product expected = ProductDataBuilder.withAllFields().active(false).build();

        assertThat(map(source).active()).isEqualTo(expected.active());
    }

    @Test
    void mapsAttributes() {
        List<ElasticSearchAttribute> sourceAttributes = List.of(new ElasticSearchAttribute("size", List.of("M", "L")));
        List<Product.Attribute> expectedAttributes = List.of(new Product.Attribute("size", List.of("M", "L")));

        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().attributes(sourceAttributes).build();
        Product expected = ProductDataBuilder.withAllFields().attributes(expectedAttributes).build();

        assertThat(map(source).attributes()).isEqualTo(expected.attributes());
    }

    @Test
    void mapsCreatedAt_convertingInstantToUtcLocalDateTime() {
        Instant instant = Instant.parse("2025-06-15T10:30:00Z");
        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 6, 15, 10, 30, 0);

        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().createdAt(instant).build();
        Product expected = ProductDataBuilder.withAllFields().createdAt(expectedDateTime).build();

        assertThat(map(source).createdAt()).isEqualTo(expected.createdAt());
    }

    @Test
    void mapsNullCreatedAt() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().createdAt(null).build();

        assertThat(map(source).createdAt()).isNull();
    }

    @Test
    void mapsAllFieldsTogether() {
        ElasticProductSearchItem source = ElasticProductSearchItemDataBuilder.withAllFields().build();
        Product expected = ProductDataBuilder.withAllFields().build();

        assertThat(map(source)).isEqualTo(expected);
    }
}