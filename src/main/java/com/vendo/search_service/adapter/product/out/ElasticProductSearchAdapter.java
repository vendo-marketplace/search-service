package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.product.out.mapper.ElasticProductMapper;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticProductSearchAdapter implements ProductSearchPort {

    private final SearchRepository<ElasticProductSearchItem> repository;
    private final ElasticProductMapper mapper;

    @Override
    public List<ProductSearchItem> search(String text) {
        List<ElasticProductSearchItem> result = repository.search(text);
        return mapper.toProducts(result);
    }
}
