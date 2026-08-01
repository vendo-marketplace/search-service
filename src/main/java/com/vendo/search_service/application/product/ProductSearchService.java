package com.vendo.search_service.application.product;

import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.search.ProductSearchData;
import com.vendo.search_service.port.ProductSearchPort;
import com.vendo.search_service.port.ProductSearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchService implements ProductSearchUseCase {

    private final ProductSearchPort productSearchPort;

    @Override
    public ProductSearchData search(String q, ProductSearchItem searchItem) {
        return productSearchPort.search(q, searchItem);
    }
}
