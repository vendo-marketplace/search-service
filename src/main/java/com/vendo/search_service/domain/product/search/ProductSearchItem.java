package com.vendo.search_service.domain.product.search;

import com.vendo.search_service.domain.product.search.filter.AddressFilter;
import com.vendo.search_service.domain.product.search.filter.AttributeFilter;
import com.vendo.search_service.domain.product.search.filter.PriceRangeFilter;
import com.vendo.search_service.domain.product.search.sort.SortBody;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductSearchItem {

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

    public static final int EMPTY = 0, FIRST_ELEMENT = 1;

    public static long getTotalPages(long totalItems, int size) {
        if (totalItems == EMPTY) return EMPTY;
        return (totalItems + size - FIRST_ELEMENT) / size;
    }

    public static boolean getHasPrevious(int page, int size, long totalItems) {
        return page > FIRST_ELEMENT && page <= (getTotalPages(totalItems, size) + FIRST_ELEMENT);
    }

    public static boolean getHasNext(int page, int size, long totalItems) {
        return page < getTotalPages(totalItems, size);
    }

    public static int getPage(int defaultPage, ProductSearchItem searchItem, boolean oneBased) {
        int page = (searchItem != null && searchItem.getPage() != null)
                ? searchItem.getPage()
                : defaultPage;

        return oneBased ? page : page - FIRST_ELEMENT;
    }

    public static int getSize(int defaultSize, ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.getSize() != null)
                ? searchItem.getSize()
                : defaultSize;
    }
}
