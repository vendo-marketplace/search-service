package com.vendo.search_service.port;

import com.vendo.search_service.domain.product.ProductSearchItem;

import java.util.List;

public interface ProductSearchUseCase {

    List<ProductSearchItem> search(String query);

}
