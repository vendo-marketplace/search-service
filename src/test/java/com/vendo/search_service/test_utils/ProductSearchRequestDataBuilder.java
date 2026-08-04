package com.vendo.search_service.test_utils;

import com.vendo.search_service.adapter.product.in.dto.AddressFilterRequest;
import com.vendo.search_service.adapter.product.in.dto.AttributeFilterRequest;
import com.vendo.search_service.adapter.product.in.dto.AttributeFilterRequest.AttributeRequest;
import com.vendo.search_service.adapter.product.in.dto.PriceRangeFilterRequest;
import com.vendo.search_service.adapter.product.in.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.search.sort.ProductSortField;
import com.vendo.search_service.domain.product.search.sort.SortBody;
import com.vendo.search_service.domain.product.search.sort.SortDirection;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Accessors(fluent = true, chain = true)
public class ProductSearchRequestDataBuilder {

    private String categoryId;
    private Boolean active;
    private Boolean isNew;
    private SortBody sort;
    private AddressFilterRequest addressFilter;
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
                .isNew(true)
                .addressFilter(new AddressFilterRequest("Lviv", "Lviv region"))
                .sort(new SortBody(ProductSortField.PRICE, SortDirection.ASC))
                .attributeFilter(new AttributeFilterRequest(List.of(
                        new AttributeRequest("color", List.of("red", "blue"))
                )))
                .priceRangeFilter(new PriceRangeFilterRequest(BigDecimal.TEN, BigDecimal.valueOf(100)))
                .size(25)
                .page(1);
    }

    public ProductSearchRequest build() {
        return new ProductSearchRequest(categoryId, active, isNew, sort, addressFilter, attributeFilter, priceRangeFilter, size, page);
    }
}