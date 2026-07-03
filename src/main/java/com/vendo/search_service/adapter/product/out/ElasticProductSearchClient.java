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
import java.util.ArrayList;
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

        List<Query> must = new ArrayList<>(), filters = new ArrayList<>();

        textQuery(q).ifPresent(must::add);
        categoryQuery(searchItem).ifPresent(filters::add);
        activeQuery(searchItem).ifPresent(filters::add);
        priceQuery(searchItem).ifPresent(filters::add);
        attributesQuery(searchItem).ifPresent(filters::add);

        SortOptions sortOptions = sort(searchItem);
        queryBuilder.withSort(s -> s.field(f -> f.field(sortOptions.sortField()).order(sortOptions.order)));
        queryBuilder.withPageable(pageable(searchItem));

        queryBuilder.withQuery(qb -> qb.bool(b -> {
            if (!must.isEmpty()) b.must(must);
            if (!filters.isEmpty()) b.filter(filters);
            return b;
        }));

        return search(queryBuilder);
    }

    private Optional<Query> textQuery(String q) {
        if (StringUtils.isEmpty(q)) {
            return Optional.empty();
        }

        Query query = Query.of(builder -> builder
                .bool(b -> b
                        .should(s -> s
                                .matchPhrase(mp -> mp
                                        .field(TITLE)
                                        .query(q)
                                        .boost(10f)))
                        .should(s -> s
                                .multiMatch(mm -> mm
                                        .query(q)
                                        .fields(TITLE + FIELD_PRIORITY, DESCRIPTION)
                                        .fuzziness(FUZZINESS_MODE)))));

        return Optional.of(query);
    }

    private Optional<Query> categoryQuery(ProductSearchItem searchItem) {
        if (searchItem == null || StringUtils.isEmpty(searchItem.categoryId())) {
            return Optional.empty();
        }

        Query query = Query.of(builder -> builder
                .bool(b -> b
                        .filter(f -> f
                                .term(t -> t
                                        .field(CATEGORY_ID).value(searchItem.categoryId())))));

        return Optional.of(query);
    }

    private Optional<Query> activeQuery(ProductSearchItem searchItem) {
        if (searchItem == null || searchItem.active() == null) {
            return Optional.empty();
        }

        Query query = Query.of(builder -> builder
                .bool(b -> b
                        .filter(f -> f
                                .term(t -> t
                                        .field(ACTIVE).value(searchItem.active())))));

        return Optional.of(query);
    }

    private Optional<Query> priceQuery(ProductSearchItem searchItem) {
        if (searchItem == null || searchItem.priceRangeFilter() == null) {
            return Optional.empty();
        }

        BigDecimal min = searchItem.priceRangeFilter().minPrice();
        BigDecimal max = searchItem.priceRangeFilter().maxPrice();

        if (min == null && max == null) {
            return Optional.empty();
        }

        Query query = Query.of(builder -> builder
                .bool(b -> b
                        .filter(f -> f
                                .range(r -> r
                                        .number(n -> {
                                            n.field(PRICE);

                                            if (min != null) {
                                                n.gte(min.doubleValue());
                                            }

                                            if (max != null) {
                                                n.lte(max.doubleValue());
                                            }
                                             return n;
                                        })))));

        return Optional.of(query);
    }

    private Optional<Query> attributesQuery(ProductSearchItem searchItem) {
        if (searchItem == null || searchItem.attributeFilter() == null) {
            return Optional.empty();
        }

        AttributeFilter filter = searchItem.attributeFilter();

        if (filter.attributes().isEmpty()) {
            return Optional.empty();
        }

        List<Query> nestedQueries = filter.attributes().stream()
                .map(attribute -> Query.of(q -> q
                        .nested(n -> n
                                .path(ATTRIBUTES)
                                .query(nq -> nq
                                        .bool(b -> b
                                                .must(m -> m.term(t -> t
                                                        .field(ATTRIBUTES_ID)
                                                        .value(attribute.id())
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
                                        )
                                )
                        )
                ))
                .toList();

        return Optional.of(
                Query.of(q -> q
                        .bool(b -> b.filter(nestedQueries))
                )
        );
    }

    private PageRequest pageable(ProductSearchItem searchItem) {
        return PageRequest.of(getPage(searchItem), getSize(searchItem));
    }

    private SortOptions sort(ProductSearchItem searchItem) {
        SortBody sort = searchItem != null
                ? searchItem.sort()
                : null;

        ProductSortField sortField = sort != null && sort.sortBy() != null ?
                sort.sortBy() :
                ProductSortField.CREATED_AT;

        SortOrder order = sort != null && sort.direction() != null ?
                SortOrder.valueOf(sort.direction().getDirection()) :
                SortOrder.Desc;

        return new SortOptions(sortField.getField(), order);
    }

    private List<ElasticProductSearchItem> search(NativeQueryBuilder queryBuilder) {
        try {
            return operations.search(queryBuilder.build(), ElasticProductSearchItem.class).stream().map(SearchHit::getContent).toList();
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

    private record SortOptions(String sortField, SortOrder order) {}
}
