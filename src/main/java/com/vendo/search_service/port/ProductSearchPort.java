package com.vendo.search_service.port;

import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.ProductSearchItem;

import java.util.List;

public interface ProductSearchPort {

    List<Product> search(String q, ProductSearchItem searchItem);

}
