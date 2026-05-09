package com.vendo.search_service.port;

import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.ProductSearchItem;

import java.util.List;

public interface ProductSearchPort {

    List<ProductSearchItem> search(String q, ProductSearchRequest request);

}
