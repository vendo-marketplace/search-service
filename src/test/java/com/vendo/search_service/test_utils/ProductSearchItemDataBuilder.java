package com.vendo.search_service.test_utils;

import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.search.filter.AddressFilter;
import com.vendo.search_service.domain.product.search.filter.AttributeFilter;
import com.vendo.search_service.domain.product.search.filter.PriceRangeFilter;
import com.vendo.search_service.domain.product.search.sort.ProductSortField;
import com.vendo.search_service.domain.product.search.sort.SortBody;
import com.vendo.search_service.domain.product.search.sort.SortDirection;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Accessors(fluent = true, chain = true)
public class ProductSearchItemDataBuilder {

    private String categoryId;
    private Boolean active;
    private Boolean isNew;
    private SortBody sort;
    private List<String> ids;
    private AddressFilter addressFilter;
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
                .isNew(true)
                .sort(new SortBody(ProductSortField.PRICE, SortDirection.ASC))
                .addressFilter(new AddressFilter("Lviv", "Lviv region"))
                .attributeFilter(new AttributeFilter(List.of(
                        new AttributeFilter.Attribute("color", List.of("red", "blue"))
                )))
                .ids(List.of("1", "2", "3"))
                .priceRangeFilter(new PriceRangeFilter(BigDecimal.TEN, BigDecimal.valueOf(100)))
                .size(25)
                .page(1);
    }

    public ProductSearchItem build() {
        return new ProductSearchItem(categoryId, active, isNew, sort, ids, addressFilter, attributeFilter, priceRangeFilter, size, page);
    }
}