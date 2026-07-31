package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.search_service.adapter.product.out.constants.ProductSearchFields;
import com.vendo.search_service.adapter.product.out.persistence.ElasticProductSearchItem;
import com.vendo.search_service.domain.product.exception.InternalSearchException;
import com.vendo.search_service.domain.product.sort.ProductSortField;
import com.vendo.search_service.domain.product.sort.SortBody;
import com.vendo.search_service.domain.product.sort.SortDirection;
import com.vendo.search_service.test_utils.ElasticProductSearchItemDataBuilder;
import com.vendo.search_service.test_utils.ProductSearchItemDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.DESCRIPTION;
import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElasticProductSearchClientTest {

    private static final int DEFAULT_SIZE = 20;
    private static final int DEFAULT_PAGE = 0;

    @Mock
    private ElasticsearchOperations operations;

    @Mock
    private List<QueryContributor> queryContributors;

    @InjectMocks
    private ElasticProductSearchClient client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "DEFAULT_SIZE", DEFAULT_SIZE);
        ReflectionTestUtils.setField(client, "DEFAULT_PAGE", DEFAULT_PAGE);
    }

    @Test
    void search_shouldReturnProducts() {
        ElasticProductSearchItem item1 = ElasticProductSearchItemDataBuilder.withAllFields().id("p-1").build();
        ElasticProductSearchItem item2 = ElasticProductSearchItemDataBuilder.withAllFields().id("p-2").build();
        givenSearchReturns(item1, item2);

        List<ElasticProductSearchItem> result = client.search("laptop", null);

        assertThat(result).containsExactly(item1, item2);
        verify(operations).search(any(org.springframework.data.elasticsearch.core.query.Query.class), eq(ElasticProductSearchItem.class));
    }

    @Test
    void search_shouldReturnEmptyList_whenNothingFound() {
        givenSearchReturns();

        List<ElasticProductSearchItem> result = client.search("laptop", null);

        assertThat(result).isEmpty();
    }

    @Test
    void search_shouldReturnEmptyList_whenIndexDoesNotExist() {
        when(operations.search(any(org.springframework.data.elasticsearch.core.query.Query.class), eq(ElasticProductSearchItem.class))).thenThrow(new NoSuchIndexException("products"));

        List<ElasticProductSearchItem> result = client.search("laptop", null);

        assertThat(result).isEmpty();
    }

    @Test
    void search_shouldThrowInternalSearchException_whenUncategorizedElasticsearchExceptionThrown() {
        when(operations.search(any(org.springframework.data.elasticsearch.core.query.Query.class), eq(ElasticProductSearchItem.class))).thenThrow(new UncategorizedElasticsearchException("uncategorized"));

        assertThatThrownBy(() -> client.search("laptop", null))
                .isInstanceOf(InternalSearchException.class);
    }


    /**
     * Duplicate nested bool is not a bug here. It is necessary because Elastic
     * will ignore SHOULD when there is at least one of these queries in the root: filter, must.
     * In this case search by text is always prioritized so we can't allow it to be ignored.
     */
    @Test
    void search_shouldBuildBoolWithTwoShould_whenOnlyQueryProvided() {
        givenSearchReturns();

        client.search("laptop", null);

        Query query = captureQuery().getQuery();

        assertThat(query).isNotNull();
        assertThat(query.isBool()).isTrue();

        assertThat(query.bool().must()).isNotEmpty();
        assertThat(query.bool().must()).hasSize(1);

        assertThat(query.bool().must().get(0).isBool()).isTrue();

        assertThat(query.bool().must().get(0).bool().should()).isNotNull();
        assertThat(query.bool().must().get(0).bool().should()).isNotEmpty();
        assertThat(query.bool().must().get(0).bool().should()).hasSize(2);

        Query matchPhraseQuery = query.bool().must().get(0).bool().should().get(0);
        assertThat(matchPhraseQuery.isMatchPhrase()).isTrue();
        assertThat(matchPhraseQuery.matchPhrase().field()).isEqualTo(TITLE);

        Query multiMatchQuery = query.bool().must().get(0).bool().should().get(1);
        assertThat(multiMatchQuery.isMultiMatch()).isTrue();
        assertThat(multiMatchQuery.multiMatch().fields()).isNotEmpty();
        assertThat(multiMatchQuery.multiMatch().fields()).hasSize(2);
        assertThat(multiMatchQuery.multiMatch().fields().get(0)).isEqualTo(ProductSearchFields.withPriority(TITLE, 3));
        assertThat(multiMatchQuery.multiMatch().fields().get(1)).isEqualTo(DESCRIPTION);
    }

    @Test
    void search_shouldMatchAllFilter_whenQueryIsBlank_andNoFilters() {
        givenSearchReturns();

        client.search("", null);

        Query query = captureQuery().getQuery();

        assertThat(query).isNotNull();
        assertThat(query.isMatchAll()).isTrue();
    }

    @Test
    void search_shouldMatchAllFilter_whenQueryIsNull() {
        givenSearchReturns();

        client.search(null, null);

        Query query = captureQuery().getQuery();

        assertThat(query).isNotNull();
        assertThat(query.isMatchAll()).isTrue();
    }

    @Test
    void search_shouldNotIncludeFilter_whenFilterIsEmpty() {
        givenSearchReturns();

        client.search(null, ProductSearchItemDataBuilder.empty().build());

        Query query = captureQuery().getQuery();

        assertThat(query).isNotNull();
        assertThat(query.isBool()).isFalse();
    }

    @Test
    void search_shouldDefaultToCreatedAtDesc_whenSearchItemIsNull() {
        givenSearchReturns();

        client.search("laptop", null);

        SortOptions sort = captureQuery().getSortOptions().get(0);
        assertThat(sort.field().field()).isEqualTo("createdAt");
        assertThat(sort.field().order()).isEqualTo(SortOrder.Desc);
    }

    @Test
    void search_shouldUseProvidedFieldAndDirection() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().sort(new SortBody(ProductSortField.PRICE, SortDirection.ASC)).build());

        SortOptions sort = captureQuery().getSortOptions().get(0);
        assertThat(sort.field().field()).isEqualTo("price");
        assertThat(sort.field().order()).isEqualTo(SortOrder.Asc);
    }

    @Test
    void search_shouldDefaultDirectionToDesc_whenDirectionIsNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().sort(new SortBody(ProductSortField.PRICE, null)).build());

        SortOptions sort = captureQuery().getSortOptions().get(0);
        assertThat(sort.field().field()).isEqualTo("price");
        assertThat(sort.field().order()).isEqualTo(SortOrder.Desc);
    }

    @Test
    void search_shouldDefaultFieldToCreatedAt_whenSortByIsNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().sort(new SortBody(null, SortDirection.ASC)).build());

        SortOptions sort = captureQuery().getSortOptions().get(0);
        assertThat(sort.field().field()).isEqualTo("createdAt");
        assertThat(sort.field().order()).isEqualTo(SortOrder.Asc);
    }


    @Test
    void search_shouldUseDefaults_whenSearchItemIsNull() {
        givenSearchReturns();

        client.search("laptop", null);

        Pageable pageable = captureQuery().getPageable();
        assertThat(pageable.getPageNumber()).isEqualTo(DEFAULT_PAGE);
        assertThat(pageable.getPageSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    void search_shouldUseDefaults_whenPageAndSizeAreNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().build());

        Pageable pageable = captureQuery().getPageable();
        assertThat(pageable.getPageNumber()).isEqualTo(DEFAULT_PAGE);
        assertThat(pageable.getPageSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    void search_shouldUseProvidedPageAndSize() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().page(2).size(50).build());

        Pageable pageable = captureQuery().getPageable();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(50);
    }


    @SuppressWarnings("unchecked")
    private void givenSearchReturns(ElasticProductSearchItem... items) {
        SearchHits<ElasticProductSearchItem> hits = mock(SearchHits.class);
        List<SearchHit<ElasticProductSearchItem>> hitList = Arrays.stream(items).map(item -> {
            SearchHit<ElasticProductSearchItem> hit = mock(SearchHit.class);
            when(hit.getContent()).thenReturn(item);
            return hit;
        }).toList();

        when(hits.stream()).thenReturn(hitList.stream());
        when(operations.search(any(org.springframework.data.elasticsearch.core.query.Query.class), eq(ElasticProductSearchItem.class))).thenReturn(hits);
    }

    private NativeQuery captureQuery() {
        ArgumentCaptor<org.springframework.data.elasticsearch.core.query.Query> captor = ArgumentCaptor.forClass(org.springframework.data.elasticsearch.core.query.Query.class);
        verify(operations).search(captor.capture(), eq(ElasticProductSearchItem.class));
        return (NativeQuery) captor.getValue();
    }
}