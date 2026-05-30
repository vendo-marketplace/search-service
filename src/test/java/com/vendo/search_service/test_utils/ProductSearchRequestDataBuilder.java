package com.vendo.search_service.test_utils;

import com.vendo.search_service.adapter.product.in.dto.AttributeFilterRequest;
import com.vendo.search_service.adapter.product.in.dto.AttributeFilterRequest.AttributeRequest;
import com.vendo.search_service.adapter.product.in.dto.PriceRangeFilterRequest;
import com.vendo.search_service.adapter.product.in.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.sort.ProductSortField;
import com.vendo.search_service.domain.product.sort.SortBody;
import com.vendo.search_service.domain.product.sort.SortDirection;

import java.math.BigDecimal;
import java.util.List;

public class ProductSearchRequestDataBuilder {

    private String categoryId;
    private Boolean active;
    private SortBody sort;
    private AttributeFilterRequest attributeFilter;
    private PriceRangeFilterRequest priceRangeFilter;
    private Integer size;
    private Integer page;

    public static ProductSearchRequestDataBuilder empty() {
        return new ProductSearchRequestDataBuilder();
    }

    public static ProductSearchRequestDataBuilder withAllFields() {
        return new ProductSearchRequestDataBuilder()
                .categoryId("category-1")
                .active(true)
                .sort(new SortBody(ProductSortField.PRICE, SortDirection.ASC))
                .attributeFilter(new AttributeFilterRequest(List.of(
                        new AttributeRequest("color", List.of("red", "blue"))
                )))
                .priceRangeFilter(new PriceRangeFilterRequest(BigDecimal.TEN, BigDecimal.valueOf(100)))
                .size(20)
                .page(0);
    }

    public ProductSearchRequestDataBuilder categoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public ProductSearchRequestDataBuilder active(Boolean active) {
        this.active = active;
        return this;
    }

    public ProductSearchRequestDataBuilder sort(SortBody sort) {
        this.sort = sort;
        return this;
    }

    public ProductSearchRequestDataBuilder attributeFilter(AttributeFilterRequest attributeFilter) {
        this.attributeFilter = attributeFilter;
        return this;
    }

    public ProductSearchRequestDataBuilder priceRangeFilter(PriceRangeFilterRequest priceRangeFilter) {
        this.priceRangeFilter = priceRangeFilter;
        return this;
    }

    public ProductSearchRequestDataBuilder size(Integer size) {
        this.size = size;
        return this;
    }

    public ProductSearchRequestDataBuilder page(Integer page) {
        this.page = page;
        return this;
    }

    public ProductSearchRequest build() {
        return new ProductSearchRequest(categoryId, active, sort, attributeFilter, priceRangeFilter, size, page);
    }
}