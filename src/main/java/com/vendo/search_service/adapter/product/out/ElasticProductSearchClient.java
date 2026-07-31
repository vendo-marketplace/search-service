package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.search_service.adapter.product.out.constants.ProductSearchFields;
import com.vendo.search_service.adapter.product.out.persistence.ElasticProductSearchItem;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.domain.product.exception.InternalSearchException;
import com.vendo.search_service.domain.product.sort.ProductSortField;
import com.vendo.search_service.domain.product.sort.SortBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.ResourceFailureException;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.DESCRIPTION;
import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.TITLE;

@Slf4j
@Component
@RequiredArgsConstructor
class ElasticProductSearchClient implements SearchRepository<ElasticProductSearchItem, ProductSearchItem> {

    private static final String FUZZINESS_MODE = "AUTO";
    private final ElasticsearchOperations operations;

    private final List<QueryContributor> queryContributors;

    @Value("${product.search.size}")
    private int DEFAULT_SIZE;
    @Value("${product.search.page}")
    private int DEFAULT_PAGE;

    @Override
    public List<ElasticProductSearchItem> search(String q, ProductSearchItem searchItem) {
        List<Query> must = new ArrayList<>(), filters = new ArrayList<>();
        NativeQueryBuilder queryBuilder = NativeQuery.builder();

        textQuery(q).ifPresent(must::add);
        withFilters(searchItem, filters);

        withSort(queryBuilder, searchItem);
        withPage(queryBuilder, searchItem);
        withDefaults(queryBuilder, must, filters);

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
                                        .fields(ProductSearchFields.withPriority(TITLE, 3), DESCRIPTION)
                                        .fuzziness(FUZZINESS_MODE)))));

        return Optional.of(query);
    }

    private void withFilters(ProductSearchItem searchItem, List<Query> filters) {
        queryContributors.forEach(qc -> qc.contribute(searchItem, filters));
    }

    private void withSort(NativeQueryBuilder queryBuilder, ProductSearchItem searchItem) {
        SortOptions sortOptions = sort(searchItem);
        queryBuilder.withSort(s -> s.field(f -> f.field(sortOptions.sortField()).order(sortOptions.order())));
    }

    private void withPage(NativeQueryBuilder queryBuilder, ProductSearchItem searchItem) {
        PageRequest pageable = PageRequest.of(getPage(searchItem), getSize(searchItem));
        queryBuilder.withPageable(pageable);
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
            log.warn("Elasticsearch internal exception, returning empty list. Reason: ", e);
            return List.of();
        } catch (UncategorizedElasticsearchException | ResourceNotFoundException | ResourceFailureException e) {
            throw new InternalSearchException(e);
        }
    }

    private void withDefaults(NativeQueryBuilder queryBuilder, List<Query> must, List<Query> filters) {
        if (must.isEmpty() && filters.isEmpty()) {
            queryBuilder.withQuery(qb -> qb.matchAll(ma -> ma));
        } else {
            queryBuilder.withQuery(qb -> qb.bool(b -> {
                if (!must.isEmpty()) b.must(must);
                if (!filters.isEmpty()) b.filter(filters);
                return b;
            }));
        }
    }

    private int getPage(ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.page() != null)
                ? searchItem.page()
                : DEFAULT_PAGE;
    }

    private int getSize(ProductSearchItem searchItem) {
        return (searchItem != null && searchItem.size() != null)
                ? searchItem.size()
                : DEFAULT_SIZE;
    }
}
