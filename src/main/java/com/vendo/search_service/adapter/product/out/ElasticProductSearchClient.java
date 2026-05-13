package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.FieldValue;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.AttributeFilter;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.utils_lib.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProductSearchClient implements SearchRepository<ElasticProductSearchItem, ProductSearchItem> {

    private static final String FUZZINESS_MODE = "AUTO";
    private static final String FIELD_PRIORITY = "^3";

    private final ElasticsearchOperations operations;


    @Value("${product.search.size}")
    private int DEFAULT_SIZE;
    @Value("${product.search.page}")
    private int DEFAULT_PAGE;

    @Override
    public List<ElasticProductSearchItem> search(String q, ProductSearchItem searchItem) {
        NativeQueryBuilder queryBuilder = NativeQuery.builder();

        withQuery(q, queryBuilder);
        withPageable(PageRequest.of(getPage(searchItem), getSize(searchItem)), queryBuilder);

        if (searchItem == null) return search(queryBuilder);

        withCategoryId(searchItem, queryBuilder);
        withActive(searchItem, queryBuilder);
        withPrice(searchItem, queryBuilder);
        withAttributes(searchItem, queryBuilder);

        return search(queryBuilder);
    }

    private void withPageable(PageRequest page, NativeQueryBuilder queryBuilder) {
        queryBuilder.withPageable(page);
    }

    private void withQuery(String q, NativeQueryBuilder queryBuilder) {
        if (!StringUtils.isEmpty(q)) {
            queryBuilder.withQuery(query -> query
                    .bool(b -> b
                            .should(s -> s
                                    .matchPhrase(ph -> ph
                                            .field(TITLE)
                                            .query(q)
                                            .boost(10f)
                                    )
                            )
                            .should(s -> s.multiMatch(m -> m
                                            .query(q)
                                            .fields(TITLE + FIELD_PRIORITY, DESCRIPTION)
                                            .fuzziness(FUZZINESS_MODE)
                                    )
                            )
                    )
            );
        } else {
            queryBuilder.withFilter(query -> query.matchAll(m -> m));
        }
    }

    private void withAttributes(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {

        if (searchItem.attributeFilter() == null) {
            return;
        }

        AttributeFilter filter = searchItem.attributeFilter();

        if (filter.attributes().isEmpty()) {
            return;
        }

        filter.attributes().forEach(attribute -> {

            queryBuilder.withFilter(f -> f.nested(n -> n
                    .path(ATTRIBUTES)
                    .query(q -> q.bool(b -> b
                            .must(m -> m.term(t -> t
                                    .field(ATTRIBUTES_ID)
                                    .value(FieldValue.of(attribute.id()))
                            ))
                            .must(m -> m.terms(t -> t
                                    .field(ATTRIBUTES_VALUES)
                                    .terms(ts -> ts.value(
                                            attribute.values()
                                                    .stream()
                                                    .map(FieldValue::of)
                                                    .toList()
                                    ))
                            ))
                    ))
            ));
        });
    }

    private void withCategoryId(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {
        if (!StringUtils.isEmpty(searchItem.categoryId())) {
            queryBuilder.withFilter(query -> query.term(t -> t.field(CATEGORY_ID).value(v -> v.stringValue(searchItem.categoryId()))));
        }
    }

    private void withActive(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {
        if (Optional.ofNullable(searchItem.active()).isPresent()) {
            queryBuilder.withFilter(f -> f.term(t -> t.field(ACTIVE).value(searchItem.active())));
        }
    }

    private void withPrice(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {
        Optional<BigDecimal> minOpt = Optional.ofNullable(searchItem.minPrice());
        Optional<BigDecimal> maxOpt = Optional.ofNullable(searchItem.maxPrice());

        if (minOpt.isPresent() || maxOpt.isPresent()) {
            queryBuilder.withQuery(query -> query.bool(b -> b.filter(f -> f.range(r -> r.number(n -> {
                n.field(PRICE);

                if (minOpt.isPresent()) {
                    n.gte(searchItem.minPrice().doubleValue());
                }

                if (maxOpt.isPresent()) {
                    n.lte(searchItem.maxPrice().doubleValue());
                }

                return n;
            })))));
        }
    }

    private List<ElasticProductSearchItem> search(NativeQueryBuilder queryBuilder) {
        return operations.search(queryBuilder.build(), ElasticProductSearchItem.class).stream()
                .map(SearchHit::getContent)
                .toList();
    }

    private int getPage(ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.page() != null) ? searchItem.page() : DEFAULT_PAGE;
    }

    private int getSize(ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.size() != null) ? searchItem.size() : DEFAULT_SIZE;
    }
}
