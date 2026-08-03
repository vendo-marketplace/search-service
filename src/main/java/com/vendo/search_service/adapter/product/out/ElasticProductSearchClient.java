package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.search_service.adapter.product.out.constants.ProductSearchFields;
import com.vendo.search_service.adapter.product.out.persistence.ElasticProductSearchItem;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.adapter.search.dto.SearchResponse;
import com.vendo.search_service.domain.product.exception.InternalSearchException;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.search.exception.PageNotFoundException;
import com.vendo.search_service.domain.product.search.sort.ProductSortField;
import com.vendo.search_service.domain.product.search.sort.SortBody;
import com.vendo.search_service.domain.search.SearchMetadata;
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
import org.springframework.data.elasticsearch.core.SearchHits;
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
    public SearchResponse<ElasticProductSearchItem> search(String q, ProductSearchItem searchItem) {
        List<Query> must = new ArrayList<>(), filters = new ArrayList<>();
        NativeQueryBuilder queryBuilder = NativeQuery.builder();

        textQuery(q).ifPresent(must::add);
        withFilters(searchItem, filters);

        withSort(queryBuilder, searchItem);
        withPage(queryBuilder, searchItem);
        withDefaults(queryBuilder, must, filters);

        return getResults(queryBuilder, searchItem);
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
        SortOptions sortOptions = getSortOptions(searchItem);
        queryBuilder.withSort(s -> s.field(f -> f.field(sortOptions.sortField()).order(sortOptions.order())));
    }

    private SortOptions getSortOptions(ProductSearchItem searchItem) {
        SortBody sort = searchItem != null
                ? searchItem.getSort()
                : null;

        ProductSortField sortField = sort != null && sort.sortBy() != null ?
                sort.sortBy() :
                ProductSortField.CREATED_AT;

        SortOrder order = sort != null && sort.direction() != null ?
                SortOrder.valueOf(sort.direction().getDirection()) :
                SortOrder.Desc;

        return new SortOptions(sortField.getField(), order);
    }

    private void withPage(NativeQueryBuilder queryBuilder, ProductSearchItem searchItem) {
        PageRequest pageable = PageRequest.of(ProductSearchItem.getPage(DEFAULT_PAGE, searchItem), ProductSearchItem.getSize(DEFAULT_SIZE, searchItem));
        queryBuilder.withPageable(pageable);
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

    private SearchResponse<ElasticProductSearchItem> getResults(NativeQueryBuilder queryBuilder, ProductSearchItem searchItem) {
        try {
            SearchHits<ElasticProductSearchItem> hits = operations.search(queryBuilder.build(), ElasticProductSearchItem.class);

            List<ElasticProductSearchItem> items = hits.stream().map(SearchHit::getContent).toList();
            SearchMetadata metadata = buildMetadata(hits.getTotalHits(), searchItem);

            throwIfPageNotFound(metadata.page(), metadata.totalPages());
            return new SearchResponse<>(items, metadata);
        } catch (NoSuchIndexException e) {
            log.warn("Elasticsearch internal exception, returning empty list. Reason: ", e);
            return new SearchResponse<>(List.of(), SearchMetadata.fromDefault(DEFAULT_PAGE, DEFAULT_SIZE));
        } catch (UncategorizedElasticsearchException | ResourceNotFoundException | ResourceFailureException e) {
            throw new InternalSearchException(e);
        }
    }

    private SearchMetadata buildMetadata(long totalItems, ProductSearchItem searchItem) {
        int page = ProductSearchItem.getPage(DEFAULT_PAGE, searchItem);
        int size = ProductSearchItem.getSize(DEFAULT_SIZE, searchItem);

        return new SearchMetadata(
                page,
                size,
                ProductSearchItem.getTotalPages(totalItems, size),
                totalItems,
                ProductSearchItem.getHasPrevious(page),
                ProductSearchItem.getHasNext(page, size, totalItems)
        );
    }

    private void throwIfPageNotFound(int page, long totalPages) {
        if (page >= totalPages && page > ProductSearchItem.EMPTY) {
            throw new PageNotFoundException("Page %d not found.".formatted(page));
        }
    }
}
