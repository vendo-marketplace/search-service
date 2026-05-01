package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticProductSearchAdapter implements ProductSearchPort {

    private final SearchRepository<ProductSearchItem> repository;

    @Override
    public List<ProductSearchItem> search(String text) {
        return repository.search(text);
    }
}
