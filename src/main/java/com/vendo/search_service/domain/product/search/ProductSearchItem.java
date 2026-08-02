package com.vendo.search_service.domain.product.search;

import com.vendo.search_service.domain.product.search.filter.AddressFilter;
import com.vendo.search_service.domain.product.search.filter.AttributeFilter;
import com.vendo.search_service.domain.product.search.filter.PriceRangeFilter;
import com.vendo.search_service.domain.product.search.sort.SortBody;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSearchItem {

    private String categoryId;
    private Boolean active;
    private Boolean isNew;

    private SortBody sort;

    private AddressFilter addressFilter;
    private AttributeFilter attributeFilter;
    private PriceRangeFilter priceRangeFilter;

    private Integer size;
    private Integer page;

    public static final int FIRST_ELEMENT = 0;
    private static final int EMPTY = 0, ONE_ELEMENT = 1;

    public static long getTotalPages(long totalItems, int size) {
        if (totalItems == EMPTY || size == EMPTY) return EMPTY;
        return totalItems < size ? ONE_ELEMENT : totalItems / size;
    }

    public static boolean getHasPrevious(int page) {
        return page > FIRST_ELEMENT;
    }

    public static boolean getHasNext(int page, int size, long totalItems) {
        return (page + ONE_ELEMENT) < getTotalPages(totalItems, size);
    }

    public static int getPage(int defaultPage, ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.getPage() != null)
                ? searchItem.getPage()
                : defaultPage;
    }

    public static int getSize(int defaultSize, ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.getSize() != null)
                ? searchItem.getSize()
                : defaultSize;
    }
}
