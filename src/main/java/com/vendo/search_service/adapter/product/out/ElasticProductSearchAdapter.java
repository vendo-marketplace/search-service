package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.product.out.mapper.ElasticProductMapper;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticProductSearchAdapter implements ProductSearchPort {

    private final SearchRepository<ElasticProductSearchItem, ProductSearchRequest> repository;
    private final ElasticProductMapper mapper;

    @Override
    public List<ProductSearchItem> search(String q, ProductSearchRequest request) {
        List<ElasticProductSearchItem> result = repository.search(q, request);
        return mapper.toProducts(result);
    }
}
