package com.vendo.search_service.adapter.product.out.persistence;

import com.vendo.search_service.adapter.product.out.mapper.ElasticProductMapper;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.adapter.search.dto.SearchResponse;
import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.search.ProductSearchData;
import com.vendo.search_service.port.ProductSearchPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class ElasticProductSearchAdapter implements ProductSearchPort {

    private final SearchRepository<ElasticProductSearchItem, ProductSearchItem> repository;
    private final ElasticProductMapper mapper;

    @Override
    public ProductSearchData search(String q, ProductSearchItem searchItem) {
        SearchResponse<ElasticProductSearchItem> searchResponse = repository.search(q, searchItem);
        List<Product> products = mapper.toProducts(searchResponse.data());
        return new ProductSearchData(products, searchResponse.metadata());
    }
}
