package com.vendo.search_service.application.product;

import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchPort;
import com.vendo.search_service.port.ProductSearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService implements ProductSearchUseCase {

    private final ProductSearchPort productSearchPort;

    @Override
    public List<Product> search(String q, ProductSearchItem searchItem) {
        return productSearchPort.search(q, searchItem);
    }
}
