package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.product.out.mapper.ElasticProductMapper;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProductSearchAdapter implements ProductSearchPort {

    private final SearchRepository<ElasticProductSearchItem, ProductSearchItem> repository;
    private final ElasticProductMapper mapper;

    @Override
    public List<Product> search(String q, ProductSearchItem searchItem) {
        List<ElasticProductSearchItem> result = repository.search(q, searchItem);
        return mapper.toProducts(result);
    }
}
