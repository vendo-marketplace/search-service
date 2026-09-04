package com.vendo.search_service.adapter.product.out.strategy;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.search_service.adapter.product.out.QueryContributor;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.search.filter.AddressFilter;
import com.vendo.search_service.domain.product.search.filter.AttributeFilter;
import com.vendo.search_service.domain.product.search.filter.PriceRangeFilter;
import com.vendo.search_service.test_utils.ProductSearchItemDataBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.*;
import static org.assertj.core.api.Assertions.assertThat;

public class FiltersQueryContributorTest {

    private final CategoryQueryContributor categoryQueryContributor = new CategoryQueryContributor();
    private final ActiveQueryContributor activeQueryContributor = new ActiveQueryContributor();
    private final PriceQueryContributor priceQueryContributor = new PriceQueryContributor();
    private final AttributesQueryContributor attributesQueryContributor = new AttributesQueryContributor();
    private final IsNewQueryContributor isNewQueryContributor = new IsNewQueryContributor();
    private final AddressQueryContributor addressQueryContributor = new AddressQueryContributor();
    private final ProductIdsQueryContributor productIdsQueryContributor = new ProductIdsQueryContributor();

    @Test
    void search_shouldAddTermFilter_whenCategoryProvided() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().categoryId("id").build();

        Query query = getSingleResult(categoryQueryContributor, searchItem);

        assertThat(query).isNotNull();
        assertThat(query.isTerm()).isTrue();
        assertThat(query.term().field()).isEqualTo(CATEGORY_ID);
        assertThat(query.term().value().stringValue()).isEqualTo(searchItem.getCategoryId());
    }

    @Test
    void search_shouldAddTermsFilter_whenIds() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().ids(List.of("1", "2", "3")).build();

        Query query = getSingleResult(productIdsQueryContributor, searchItem);

        assertThat(query).isNotNull();
        assertThat(query.isTerms()).isTrue();
        assertThat(query.terms().field()).isEqualTo(ID);
        assertThat(query.terms().terms().value())
                .extracting(FieldValue::stringValue)
                .containsExactlyElementsOf(searchItem.getIds());
    }

    @Test
    void search_shouldAddTermFilter_whenActiveIsTrue() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().active(true).build();

        Query query = getSingleResult(activeQueryContributor, searchItem);

        assertThat(query).isNotNull();
        assertThat(query.isTerm()).isTrue();
        assertThat(query.term().field()).isEqualTo(ACTIVE);
        assertThat(query.term().value().booleanValue()).isTrue();
    }

    @Test
    void search_shouldAddTermFilter_whenIsNewIsTrue() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().isNew(true).build();

        Query query = getSingleResult(isNewQueryContributor, searchItem);

        assertThat(query).isNotNull();
        assertThat(query.isTerm()).isTrue();
        assertThat(query.term().field()).isEqualTo(IS_NEW);
        assertThat(query.term().value().booleanValue()).isTrue();
    }

    @Test
    void search_shouldAddTermFilter_whenActiveIsFalse() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().active(false).build();

        Query query = getSingleResult(activeQueryContributor, searchItem);

        assertThat(query).isNotNull();
        assertThat(query.isTerm()).isTrue();
        assertThat(query.term().field()).isEqualTo(ACTIVE);
        assertThat(query.term().value().booleanValue()).isFalse();
    }

    @Test
    void search_shouldAddTermFilters_withCityAndRegion_whenAddressProvided() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().addressFilter(new AddressFilter("Lviv", "Lviv region")).build();

        List<Query> queries = getMultipleResults(addressQueryContributor, searchItem);

        assertThat(queries).isNotNull();
        assertThat(queries).hasSize(2);

        Query cityTermQuery = queries.get(0);
        assertThat(cityTermQuery.isTerm()).isTrue();
        assertThat(cityTermQuery.term().field()).isEqualTo(ADDRESS_CITY);
        assertThat(cityTermQuery.term().value().stringValue()).isEqualTo(searchItem.getAddressFilter().city());

        Query regionTermQuery = queries.get(1);
        assertThat(regionTermQuery.isTerm()).isTrue();
        assertThat(regionTermQuery.term().field()).isEqualTo(ADDRESS_REGION);
        assertThat(regionTermQuery.term().value().stringValue()).isEqualTo(searchItem.getAddressFilter().region());
    }

    @Test
    void search_shouldAddTermFilter_withCity_butNoRegion() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().addressFilter(new AddressFilter("Lviv", null)).build();

        Query query = getSingleResult(addressQueryContributor, searchItem);

        assertThat(query).isNotNull();

        assertThat(query.isTerm()).isTrue();
        assertThat(query.term().field()).isEqualTo(ADDRESS_CITY);
        assertThat(query.term().value().stringValue()).isEqualTo(searchItem.getAddressFilter().city());
    }

    @Test
    void search_shouldNotAddTermFilter_withRegion_butNoCity() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().addressFilter(new AddressFilter(null, "Lviv region")).build();
        assertThat(isEmpty(addressQueryContributor, searchItem));
    }

    @Test
    void search_shouldAddRangeWithMinAndMax_whenBothProvided() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().priceRangeFilter(new PriceRangeFilter(BigDecimal.valueOf(10), BigDecimal.valueOf(100))).build();

        Query query = getSingleResult(priceQueryContributor, searchItem);

        assertThat(query.isRange()).isTrue();
        assertThat(query.range().number().field()).isEqualTo(PRICE);
        assertThat(query.range().number().gte()).isEqualTo(10.0);
        assertThat(query.range().number().lte()).isEqualTo(100.0);
    }

    @Test
    void search_shouldNotAddRange_whenBothAreNotProvided() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().priceRangeFilter(new PriceRangeFilter(null, null)).build();
        assertThat(isEmpty(priceQueryContributor, searchItem)).isTrue();
    }

    @Test
    void search_shouldAddRangeWithOnlyMin_whenMaxIsNull() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().priceRangeFilter(new PriceRangeFilter(BigDecimal.valueOf(10), null)).build();

        Query query = getSingleResult(priceQueryContributor, searchItem);

        assertThat(query.range().number().gte()).isEqualTo(10.0);
        assertThat(query.range().number().lte()).isNull();
    }

    @Test
    void search_addsRangeWithOnlyMax_whenMinIsNull() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().priceRangeFilter(new PriceRangeFilter(null, BigDecimal.valueOf(100))).build();

        Query query = getSingleResult(priceQueryContributor, searchItem);

        assertThat(query.range().number().gte()).isNull();
        assertThat(query.range().number().lte()).isEqualTo(100.0);
    }

    @Test
    void search_shouldAddOneNestedMustQueryPerAttribute() {
        AttributeFilter.Attribute collorAttribute = new AttributeFilter.Attribute("id1", List.of("red"));
        AttributeFilter.Attribute sizeAttribute = new AttributeFilter.Attribute("id2", List.of("M", "L"));
        AttributeFilter filter = new AttributeFilter(List.of(collorAttribute, sizeAttribute));
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().attributeFilter(filter).build();

        List<Query> queries = getMultipleResults(attributesQueryContributor, searchItem);

        assertThat(queries).hasSize(2);

        Query colorNestedQueryAttribute = queries.get(0);
        assertThat(colorNestedQueryAttribute).isNotNull();
        assertThat(colorNestedQueryAttribute.isNested()).isTrue();

        NestedQuery colorQueryAttribute = colorNestedQueryAttribute.nested();
        assertThat(colorQueryAttribute.path()).isEqualTo(ATTRIBUTES);
        assertThat(colorQueryAttribute.query().isBool()).isTrue();
        assertThat(colorQueryAttribute.query().bool().must()).hasSize(2);

        assertThat(colorQueryAttribute.path()).isEqualTo(ATTRIBUTES);
        assertThat(colorQueryAttribute.query().isBool()).isTrue();
        assertThat(colorQueryAttribute.query().bool().must()).hasSize(2);

        Query colorAttributeIdQuery = colorQueryAttribute.query().bool().must().get(0);
        assertThat(colorAttributeIdQuery.isTerm()).isTrue();
        assertThat(colorAttributeIdQuery.term().field()).isEqualTo(ATTRIBUTES_ID);
        assertThat(colorAttributeIdQuery.term().value().stringValue()).isEqualTo(collorAttribute.id());

        Query colorAttributeValuesQuery = colorQueryAttribute.query().bool().must().get(1);
        assertThat(colorAttributeValuesQuery.isTerms()).isTrue();
        assertThat(colorAttributeValuesQuery.terms().field()).isEqualTo(ATTRIBUTES_VALUES);
        assertThat(colorAttributeValuesQuery.terms().terms().value().stream().map(FieldValue::stringValue).toList().equals(collorAttribute.values())).isTrue();

        Query sizeNestedQueryAttribute = queries.get(1);
        assertThat(sizeNestedQueryAttribute.isNested());
        NestedQuery sizeQueryAttribute = sizeNestedQueryAttribute.nested();
        assertThat(sizeQueryAttribute.path()).isEqualTo(ATTRIBUTES);

        Query sizeAttributeIdQuery = sizeQueryAttribute.query().bool().must().get(0);
        assertThat(sizeAttributeIdQuery.isTerm()).isTrue();
        assertThat(sizeAttributeIdQuery.term().field()).isEqualTo(ATTRIBUTES_ID);
        assertThat(sizeAttributeIdQuery.term().value().stringValue()).isEqualTo(sizeAttribute.id());

        Query sizeAttributeValuesQuery = sizeQueryAttribute.query().bool().must().get(1);
        assertThat(sizeAttributeValuesQuery.isTerms()).isTrue();
        assertThat(sizeAttributeValuesQuery.terms().field()).isEqualTo(ATTRIBUTES_VALUES);
        assertThat(sizeAttributeValuesQuery.terms().terms().value().stream().map(FieldValue::stringValue).toList().equals(sizeAttribute.values())).isTrue();
    }

    @Test
    void search_shouldSkipAttributes_whenFilterIsNull() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().build();
        assertThat(isEmpty(attributesQueryContributor, searchItem));
    }

    @Test
    void search_shouldSkipAttributes_whenListIsEmpty() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().attributeFilter(new AttributeFilter(List.of())).build();
        assertThat(isEmpty(attributesQueryContributor, searchItem));
    }

    private Query getSingleResult(QueryContributor queryContributor, ProductSearchItem searchItem) {
        List<Query> queries = new ArrayList<>();

        queryContributor.contribute(searchItem, queries);

        assertThat(queries).isNotEmpty();
        assertThat(queries.size()).isEqualTo(1);
        return queries.get(0);
    }

    private List<Query> getMultipleResults(QueryContributor queryContributor, ProductSearchItem searchItem) {
        List<Query> queries = new ArrayList<>();

        queryContributor.contribute(searchItem, queries);

        assertThat(queries).isNotEmpty();
        assertThat(queries.size()).isGreaterThan(1);
        return queries;
    }

    private boolean isEmpty(QueryContributor queryContributor, ProductSearchItem searchItem) {
        List<Query> queries = new ArrayList<>();

        queryContributor.contribute(searchItem, queries);

        return queries.isEmpty();
    }
}
