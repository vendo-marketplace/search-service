package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.search.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticProductSearchClient implements SearchRepository<ElasticProductSearchItem> {

    private final ElasticsearchOperations operations;

    @Override
    public List<ElasticProductSearchItem> search(String text) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(mm -> mm
                                .query(text)
                                .fields(
                                        "title^3",
                                        "description",
                                        "attributes.title",
                                        "attributes.values"
                                ).fuzziness("AUTO")
                        )
                )
                .build();

        return operations.search(query, ElasticProductSearchItem.class)
                .stream()
                .map(hit -> new ElasticProductSearchItem(
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
