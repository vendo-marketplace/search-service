package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.search_service.domain.product.filter.AttributeFilter;
import com.vendo.search_service.domain.product.filter.PriceRangeFilter;
import com.vendo.search_service.domain.product.sort.ProductSortField;
import com.vendo.search_service.domain.product.sort.SortBody;
import com.vendo.search_service.domain.product.sort.SortDirection;
import com.vendo.search_service.test_utils.ElasticProductSearchItemDataBuilder;
import com.vendo.search_service.test_utils.ProductSearchItemDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ElasticProductSearchClientTest {

    private static final int DEFAULT_SIZE = 20;
    private static final int DEFAULT_PAGE = 0;

    @Mock
    private ElasticsearchOperations operations;

    @InjectMocks
    private ElasticProductSearchClient client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "DEFAULT_SIZE", DEFAULT_SIZE);
        ReflectionTestUtils.setField(client, "DEFAULT_PAGE", DEFAULT_PAGE);
    }

    @Test
    void search_shouldMapSearchHitsToContent() {
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
    void buildsBoolWithTwoShoulds_whenQueryProvided() {
        givenSearchReturns();

        client.search("laptop", null);

        Query query = captureQuery().getQuery();
        assertThat(query).isNotNull();
        assertThat(query.isBool()).isTrue();
        assertThat(query.bool().should()).hasSize(2);
    }

    @Test
    void usesMatchAllFilter_whenQueryIsBlank() {
        givenSearchReturns();

        client.search("", null);

        NativeQuery nativeQuery = captureQuery();
        assertThat(nativeQuery.getQuery()).isNull();
        assertThat(nativeQuery.getFilter()).isNotNull();
        assertThat(nativeQuery.getFilter().isMatchAll()).isTrue();
    }

    @Test
    void usesMatchAllFilter_whenQueryIsNull() {
        givenSearchReturns();

        client.search(null, null);

        assertThat(Objects.requireNonNull(captureQuery().getFilter()).isMatchAll()).isTrue();
    }


    @Test
    void addsTermFilter_whenCategoryProvided() {
        givenSearchReturns();

        client.search(null, ProductSearchItemDataBuilder.empty().categoryId("cat-7").build());

        Query filter = captureQuery().getFilter();
        Assertions.assertNotNull(filter);
        assertThat(filter.isTerm()).isTrue();
        assertThat(filter.term().field()).isEqualTo("categoryId");
        assertThat(filter.term().value().stringValue()).isEqualTo("cat-7");
    }

    @Test
    void noCategoryTerm_whenCategoryIsNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().build());

        assertThat(captureQuery().getFilter()).isNull();
    }


    @Test
    void addsTermFilter_whenActiveIsTrue() {
        givenSearchReturns();

        client.search(null, ProductSearchItemDataBuilder.empty().active(true).build());

        Query filter = captureQuery().getFilter();
        Assertions.assertNotNull(filter);
        assertThat(filter.isTerm()).isTrue();
        assertThat(filter.term().field()).isEqualTo("active");
        assertThat(filter.term().value().booleanValue()).isTrue();
    }

    @Test
    void addsTermFilter_whenActiveIsFalse() {
        givenSearchReturns();

        client.search(null, ProductSearchItemDataBuilder.empty().active(false).build());

        Query filter = captureQuery().getFilter();
        Assertions.assertNotNull(filter);
        assertThat(filter.isTerm()).isTrue();
        assertThat(filter.term().field()).isEqualTo("active");
        assertThat(filter.term().value().booleanValue()).isFalse();
    }


    @Test
    void addsRangeWithMinAndMax_whenBothProvided() {
        givenSearchReturns();

        client.search(null, ProductSearchItemDataBuilder.empty().priceRangeFilter(new PriceRangeFilter(BigDecimal.valueOf(10), BigDecimal.valueOf(100))).build());

        Query query = captureQuery().getQuery();
        Assertions.assertNotNull(query);
        assertThat(query.isBool()).isTrue();
        assertThat(query.bool().filter()).hasSize(1);

        Query range = query.bool().filter().get(0);
        assertThat(range.isRange()).isTrue();
        assertThat(range.range().number().field()).isEqualTo("price");
        assertThat(range.range().number().gte()).isEqualTo(10.0);
        assertThat(range.range().number().lte()).isEqualTo(100.0);
    }

    @Test
    void addsRangeWithOnlyMin_whenMaxIsNull() {
        givenSearchReturns();

        client.search(null, ProductSearchItemDataBuilder.empty().priceRangeFilter(new PriceRangeFilter(BigDecimal.valueOf(10), null)).build());

        Query range = Objects.requireNonNull(captureQuery().getQuery()).bool().filter().get(0);
        assertThat(range.range().number().gte()).isEqualTo(10.0);
        assertThat(range.range().number().lte()).isNull();
    }

    @Test
    void addsRangeWithOnlyMax_whenMinIsNull() {
        givenSearchReturns();

        client.search(null, ProductSearchItemDataBuilder.empty().priceRangeFilter(new PriceRangeFilter(null, BigDecimal.valueOf(100))).build());

        Query range = Objects.requireNonNull(captureQuery().getQuery()).bool().filter().get(0);
        assertThat(range.range().number().gte()).isNull();
        assertThat(range.range().number().lte()).isEqualTo(100.0);
    }

    @Test
    void noRange_whenBothBoundsNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().priceRangeFilter(new PriceRangeFilter(null, null)).build());

        assertThat(Objects.requireNonNull(captureQuery().getQuery()).bool().filter()).isEmpty();
    }


    @Test
    void addsOneNestedMustQueryPerAttribute() {
        givenSearchReturns();
        AttributeFilter filter = new AttributeFilter(List.of(new AttributeFilter.Attribute("color", List.of("red")), new AttributeFilter.Attribute("size", List.of("M", "L"))));

        client.search(null, ProductSearchItemDataBuilder.empty().attributeFilter(filter).build());

        Query query = captureQuery().getQuery();
        Assertions.assertNotNull(query);
        assertThat(query.isBool()).isTrue();
        assertThat(query.bool().must()).hasSize(2);
        assertThat(query.bool().must().get(0).isNested()).isTrue();
        assertThat(query.bool().must().get(0).nested().path()).isEqualTo("attributes");
    }

    @Test
    void skipsAttributes_whenListIsEmpty() {
        givenSearchReturns();

        client.search(null, ProductSearchItemDataBuilder.empty().attributeFilter(new AttributeFilter(List.of())).build());

        NativeQuery nativeQuery = captureQuery();
        assertThat(nativeQuery.getQuery()).isNull();
        Assertions.assertNotNull(nativeQuery.getFilter());
        assertThat(nativeQuery.getFilter().isMatchAll()).isTrue();
    }

    @Test
    void skipsAttributes_whenFilterIsNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().build());

        assertThat(Objects.requireNonNull(captureQuery().getQuery()).bool().must()).isEmpty();
    }


    @Test
    void defaultsToCreatedAtDesc_whenSearchItemIsNull() {
        givenSearchReturns();

        client.search("laptop", null);

        SortOptions sort = captureQuery().getSortOptions().get(0);
        assertThat(sort.field().field()).isEqualTo("createdAt");
        assertThat(sort.field().order()).isEqualTo(SortOrder.Desc);
    }

    @Test
    void usesProvidedFieldAndDirection() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().sort(new SortBody(ProductSortField.PRICE, SortDirection.ASC)).build());

        SortOptions sort = captureQuery().getSortOptions().get(0);
        assertThat(sort.field().field()).isEqualTo("price");
        assertThat(sort.field().order()).isEqualTo(SortOrder.Asc);
    }

    @Test
    void defaultsDirectionToDesc_whenDirectionIsNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().sort(new SortBody(ProductSortField.PRICE, null)).build());

        SortOptions sort = captureQuery().getSortOptions().get(0);
        assertThat(sort.field().field()).isEqualTo("price");
        assertThat(sort.field().order()).isEqualTo(SortOrder.Desc);
    }

    @Test
    void defaultsFieldToCreatedAt_whenSortByIsNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().sort(new SortBody(null, SortDirection.ASC)).build());

        SortOptions sort = captureQuery().getSortOptions().get(0);
        assertThat(sort.field().field()).isEqualTo("createdAt");
        assertThat(sort.field().order()).isEqualTo(SortOrder.Asc);
    }


    @Test
    void usesDefaults_whenSearchItemIsNull() {
        givenSearchReturns();

        client.search("laptop", null);

        Pageable pageable = captureQuery().getPageable();
        assertThat(pageable.getPageNumber()).isEqualTo(DEFAULT_PAGE);
        assertThat(pageable.getPageSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    void usesDefaults_whenPageAndSizeAreNull() {
        givenSearchReturns();

        client.search("laptop", ProductSearchItemDataBuilder.empty().build());

        Pageable pageable = captureQuery().getPageable();
        assertThat(pageable.getPageNumber()).isEqualTo(DEFAULT_PAGE);
        assertThat(pageable.getPageSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    void usesProvidedPageAndSize() {
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