package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.product.out.persistence.ElasticProductSearchItem;
import com.vendo.search_service.adapter.search.dto.SearchResponse;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.exception.InternalSearchException;
import com.vendo.search_service.domain.search.SearchMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.ResourceFailureException;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class ElasticProductResultsClient {

    private final ElasticsearchOperations operations;

    SearchResponse<ElasticProductSearchItem> getResults(NativeQueryBuilder queryBuilder, ProductSearchItem searchItem) {
        try {
            SearchHits<ElasticProductSearchItem> hits = operations.search(queryBuilder.build(), ElasticProductSearchItem.class);

            List<ElasticProductSearchItem> items = hits.stream().map(SearchHit::getContent).toList();
            SearchMetadata metadata = buildMetadata(hits.getTotalHits(), searchItem);

            return new SearchResponse<>(items, metadata);
        } catch (NoSuchIndexException e) {
            log.warn("Elasticsearch internal exception, returning empty list. Reason: ", e);
            return new SearchResponse<>(List.of(), null);
        } catch (UncategorizedElasticsearchException | ResourceNotFoundException | ResourceFailureException e) {
            throw new InternalSearchException(e);
        }
    }

    private static SearchMetadata buildMetadata(long totalItems, ProductSearchItem searchItem) {
        int page = searchItem == null ? 0 : searchItem.page();
        int size = searchItem == null ? 0 : searchItem.size();

        return new SearchMetadata(
                page,
                size,
                getTotalPages(totalItems, size),
                totalItems,
                getHasPrevious(page),
                getHasNext(page, size, totalItems)
        );
    }

    private static long getTotalPages(long totalItems, int size) {
        if (totalItems == 0 || size == 0) return 0;
        return totalItems / size;
    }

    private static boolean getHasPrevious(int page) {
        return page > 1;
    }

    private static boolean getHasNext(int page, int size, long totalItems) {
        return page < getTotalPages(totalItems, size);
    }

}
