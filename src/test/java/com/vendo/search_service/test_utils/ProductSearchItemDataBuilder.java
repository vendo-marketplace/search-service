package com.vendo.search_service.test_utils;

import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.domain.product.filter.AttributeFilter;
import com.vendo.search_service.domain.product.filter.PriceRangeFilter;
import com.vendo.search_service.domain.product.sort.ProductSortField;
import com.vendo.search_service.domain.product.sort.SortBody;
import com.vendo.search_service.domain.product.sort.SortDirection;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Accessors(fluent = true, chain = true)
public class ProductSearchItemDataBuilder {

    private String categoryId;
    private Boolean active;
    private SortBody sort;
    private AttributeFilter attributeFilter;
    private PriceRangeFilter priceRangeFilter;
    private Integer size;
    private Integer page;

    public static ProductSearchItemDataBuilder empty() {
        return new ProductSearchItemDataBuilder();
    }

    public static ProductSearchItemDataBuilder withAllFields() {
        return new ProductSearchItemDataBuilder()
                .categoryId("category-1")
                .active(true)
                .sort(new SortBody(ProductSortField.PRICE, SortDirection.ASC))
                .attributeFilter(new AttributeFilter(List.of(
                        new AttributeFilter.Attribute("color", List.of("red", "blue"))
                )))
                .priceRangeFilter(new PriceRangeFilter(BigDecimal.TEN, BigDecimal.valueOf(100)))
                .size(20)
                .page(0);
    }

    public ProductSearchItem build() {
        return new ProductSearchItem(categoryId, active, sort, attributeFilter, priceRangeFilter, size, page);
    }
}