package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.domain.product.filter.AttributeFilter;
import com.vendo.search_service.domain.product.sort.ProductSortField;
import com.vendo.search_service.domain.product.sort.SortBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.NoSuchIndexException;
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
        withSort(searchItem, queryBuilder);

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

    private void withSort(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {
        SortBody sort =
                searchItem != null
                        ? searchItem.sort()
                        : null;

        ProductSortField sortField =
                sort != null && sort.sortBy() != null
                        ? sort.sortBy()
                        : ProductSortField.CREATED_AT;

        SortOrder order =
                sort != null && sort.direction() != null
                        ? SortOrder.valueOf(sort.direction().getDirection())
                        : SortOrder.Desc;

        queryBuilder.withSort(s -> s
                .field(f -> f
                        .field(sortField.getField())
                        .order(order)
                )
        );
    }

    private void withAttributes(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {
        if (searchItem == null || searchItem.attributeFilter() == null) return;

        AttributeFilter filter = searchItem.attributeFilter();
        if (filter.attributes().isEmpty()) {
            return;
        }

        List<Query> attributeQueries = filter.attributes().stream().map(attribute -> new Query.Builder().nested(n -> n
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
                ))).build()).toList();

        queryBuilder.withQuery(q -> q.bool(b -> b
                .must(attributeQueries)
        ));
    }

    private void withCategoryId(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {
        if (searchItem != null && !StringUtils.isEmpty(searchItem.categoryId())) {
            queryBuilder.withFilter(query -> query.term(t -> t.field(CATEGORY_ID).value(v -> v.stringValue(searchItem.categoryId()))));
        }
    }

    private void withActive(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {
        if (searchItem != null && Optional.ofNullable(searchItem.active()).isPresent()) {
            queryBuilder.withFilter(f -> f.term(t -> t.field(ACTIVE).value(searchItem.active())));
        }
    }

    private void withPrice(ProductSearchItem searchItem, NativeQueryBuilder queryBuilder) {
        if (searchItem == null || searchItem.priceRangeFilter() == null) return;

        Optional<BigDecimal> minOpt = Optional.ofNullable(searchItem.priceRangeFilter().minPrice());
        Optional<BigDecimal> maxOpt = Optional.ofNullable(searchItem.priceRangeFilter().maxPrice());

        if (minOpt.isPresent() || maxOpt.isPresent()) {
            queryBuilder.withQuery(query -> query.bool(b -> b.filter(f -> f.range(r -> r.number(n -> {
                n.field(PRICE);

                if (minOpt.isPresent()) {
                    n.gte(searchItem.priceRangeFilter().minPrice().doubleValue());
                }

                if (maxOpt.isPresent()) {
                    n.lte(searchItem.priceRangeFilter().maxPrice().doubleValue());
                }

                return n;
            })))));
        }
    }

    private List<ElasticProductSearchItem> search(NativeQueryBuilder queryBuilder) {
        try {
            return operations.search(queryBuilder.build(), ElasticProductSearchItem.class).stream()
                    .map(SearchHit::getContent)
                    .toList();
        } catch (NoSuchIndexException e) {
            log.warn("Search index not found (Elastic restarted/unavailable), returning empty list. Reason: {}", e.getMessage());
            return List.of();
        }
    }

    private int getPage(ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.page() != null) ? searchItem.page() : DEFAULT_PAGE;
    }

    private int getSize(ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.size() != null) ? searchItem.size() : DEFAULT_SIZE;
    }
}
