package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.product.out.query.ProductQueryFactory;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.shared.ClassFieldExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProductSearchClient implements SearchRepository<ElasticProductSearchItem, ProductSearchRequest> {

    @Value("${product.search.size}")
    private int DEFAULT_SIZE;
    @Value("${product.search.page}")
    private int DEFAULT_PAGE;

    private static final String FUZZINESS_MODE = "AUTO";
    private static final String FIELD_PRIORITY = "^3";

    private static final String TITLE_FIELD = "title";
    private static final String DESCRIPTION_FIELD = "title";
    private static final String ATTRIBUTES_FIELD = "attributes";
    private static final String ATTRIBUTES_TITLE_FIELD = ATTRIBUTES_FIELD + ".title";
    private static final String ATTRIBUTES_VALUES_FIELD = ATTRIBUTES_FIELD + ".values";

    private final ElasticsearchOperations operations;
    private final ProductQueryFactory productQueryFactory;
    private final NativeQueryBuilder queryBuilder = NativeQuery.builder();

    @Override
    public List<ElasticProductSearchItem> search(String q, ProductSearchRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        withPageable(PageRequest.of(page, size), queryBuilder);
        withQuery(q, queryBuilder);

        Set<String> fields = ClassFieldExtractor.extract(ProductSearchItem.class);
        fields.stream()
                .map(productQueryFactory::getBuilder)
                .filter(Objects::nonNull)
                .forEach(qb -> qb.build(request, queryBuilder));

        return operations.search(queryBuilder.build(), ElasticProductSearchItem.class).stream()
                .map(SearchHit::getContent)
                .toList();
    }

    private void withPageable(PageRequest page, NativeQueryBuilder builder) {
        builder.withPageable(page);
    }

    private void withQuery(String q, NativeQueryBuilder builder) {
        if (Optional.ofNullable(q).isPresent()) {
            builder.withQuery(query -> query.multiMatch(mm -> mm
                    .query(q)
                    .fields(TITLE_FIELD + FIELD_PRIORITY, DESCRIPTION_FIELD, ATTRIBUTES_TITLE_FIELD, ATTRIBUTES_VALUES_FIELD)
                    .fuzziness(FUZZINESS_MODE)
            ));
        }
    }
}
