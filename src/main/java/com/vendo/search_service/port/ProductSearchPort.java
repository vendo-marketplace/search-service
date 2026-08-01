package com.vendo.search_service.port;

import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.search.ProductSearchData;

public interface ProductSearchPort {

    ProductSearchData search(String q, ProductSearchItem searchItem);

}
