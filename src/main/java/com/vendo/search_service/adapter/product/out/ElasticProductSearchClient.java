package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ElasticProductSearchClient implements SearchRepository<ElasticProductSearchItem> {

    private final ElasticsearchOperations operations;

    @Value("${product.search.size}")
    private int DEFAULT_SIZE;

    @Value("${product.search.page}")
    private int DEFAULT_PAGE;

    @Override
    public List<ElasticProductSearchItem> search(String q, ProductSearchRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        NativeQueryBuilder queryBuilder = NativeQuery.builder();

        withPageable(PageRequest.of(page, size), queryBuilder);
        withQuery(q, queryBuilder);

        return operations.search(queryBuilder.build(), ElasticProductSearchItem.class).stream().map(this::toSearchItem).toList();
    }

    private void withPageable(PageRequest page, NativeQueryBuilder builder) {
        builder.withPageable(page);
    }

    private void withQuery(String q, NativeQueryBuilder builder) {
        if (Optional.ofNullable(q).isPresent()) {
            builder.withQuery(query -> query
                    .multiMatch(mm -> mm
                            .query(q)
                            .fields(
                                    "title^3",
                                    "description",
                                    "attributes.title",
                                    "attributes.values"
                            ).fuzziness("AUTO")
                    )
            );
        }
    }

    private void withPriceRange(BigDecimal minPrice, BigDecimal maxPrice, NativeQueryBuilder builder) {

    }

    private ElasticProductSearchItem toSearchItem(SearchHit<ElasticProductSearchItem> hit) {
        return new ElasticProductSearchItem(
                hit.getContent().id(),
                hit.getContent().title(),
                hit.getContent().description(),
                hit.getContent().quantity(),
                hit.getContent().price(),
                hit.getContent().ownerId(),
                hit.getContent().categoryId(),
                hit.getContent().attributes(),
                hit.getContent().active());
    }

}
