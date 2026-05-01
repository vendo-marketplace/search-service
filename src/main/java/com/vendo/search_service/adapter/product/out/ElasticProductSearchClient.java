package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.ProductSearchItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticProductSearchClient implements SearchRepository<ProductSearchItem> {

    private final ElasticsearchOperations operations;

    @Override
    public List<ProductSearchItem> search(String text) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("title")
                                .query(text)
                        )
                )
                .build();

        return operations.search(query, ProductSearchItem.class)
                .stream()
                .map(hit -> new ProductSearchItem(
                        hit.getContent().id(),
                        hit.getContent().title(),
                        hit.getContent().description(),
                        hit.getContent().quantity(),
                        hit.getContent().price(),
                        hit.getContent().ownerId(),
                        hit.getContent().categoryId(),
                        hit.getContent().attributes(),
                        hit.getContent().active())
                )
                .toList();
    }

}
