package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.FieldValue;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import com.vendo.search_service.domain.search.AttributeFilter;
import com.vendo.utils_lib.StringUtils;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class ElasticProductSearchClient implements SearchRepository<ElasticProductSearchItem> {

    private final ElasticsearchOperations operations;

    @Value("${product.search.size}")
    private int DEFAULT_SIZE;

    @Value("${product.search.page}")
    private int DEFAULT_PAGE;

    private static final String FUZZINESS_MODE = "AUTO";
    private static final String FIELD_PRIORITY = "^3";

    // TODO write utility that returns list(set preferred) of fields by passed class
    private static final String TITLE_FIELD = "title";
    private static final String ACTIVE_FIELD = "active";
    private static final String CATEGORY_ID_FIELD = "categoryId";
    private static final String PRICE_FIELD = "price";
    private static final String DESCRIPTION_FIELD = "title";
    private static final String ATTRIBUTES_FIELD = "attributes";
    private static final String ATTRIBUTES_TITLE_FIELD = ATTRIBUTES_FIELD + ".title";
    private static final String ATTRIBUTES_VALUES_FIELD = ATTRIBUTES_FIELD + ".values";

    @Override
    public List<ElasticProductSearchItem> search(String q, ProductSearchRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        NativeQueryBuilder queryBuilder = NativeQuery.builder();

        withPageable(PageRequest.of(page, size), queryBuilder);
        withQuery(q, queryBuilder);
        withActive(request.active(), queryBuilder);
        withCategoryId(request.categoryId(), queryBuilder);
        withPriceRange(request.minPrice(), request.maxPrice(), queryBuilder);

        return operations.search(queryBuilder.build(), ElasticProductSearchItem.class).stream().map(this::toSearchItem).toList();
    }

    private void withPageable(PageRequest page, NativeQueryBuilder builder) {
        builder.withPageable(page);
    }

    private void withQuery(String q, NativeQueryBuilder builder) {
        if (Optional.ofNullable(q).isPresent()) {
            builder.withQuery(query -> query
                    .multiMatch(mm -> mm
                            .query(q)
                            .fields(
                                    TITLE_FIELD + FIELD_PRIORITY,
                                    DESCRIPTION_FIELD,
                                    ATTRIBUTES_TITLE_FIELD,
                                    ATTRIBUTES_VALUES_FIELD
                            ).fuzziness(FUZZINESS_MODE)
                    )
            );
        }
    }

    private void withActive(Boolean active, NativeQueryBuilder builder) {
        if (Optional.ofNullable(active).isPresent()) {
            builder.withFilter(f -> f.term(t -> t.field(ACTIVE_FIELD).value(active)));
        }
    }

    private void withCategoryId(String categoryId, NativeQueryBuilder builder) {
        if (!StringUtils.isEmpty(categoryId)) {
            builder.withQuery(query -> query.term(t -> t.field(CATEGORY_ID_FIELD).value(categoryId)));
        }
    }

    private void withPriceRange(BigDecimal minPrice, BigDecimal maxPrice, NativeQueryBuilder builder) {
        Optional<BigDecimal> minOpt = Optional.ofNullable(minPrice);
        Optional<BigDecimal> maxOpt = Optional.ofNullable(maxPrice);

        if (minOpt.isEmpty() || maxOpt.isPresent()) {
            builder.withQuery(query -> query.bool(b -> b.filter(f -> f.range(r -> r.number(n -> {
                n.field(PRICE_FIELD);

                if (minOpt.isPresent()) {
                   n.gte(minPrice.doubleValue());
                }

                if (maxOpt.isPresent()) {
                    n.lte(maxPrice.doubleValue());
                }

                return n;
            })))));
        }
    }

    private void withAttributeFilter(AttributeFilter filter, NativeQueryBuilder builder) {
        if (Optional.ofNullable(filter).isPresent() && !filter.attributes().isEmpty()) {
            filter.attributes().forEach(attribute -> builder
                    .withFilter(f -> f
                            .terms(t -> t.field(attribute.title())
                                    .terms(s -> s
                                            .value(attribute.values().stream()
                                                    .map(FieldValue::of)
                                                    .toList()))))
            );
        }
    }

    private ElasticProductSearchItem toSearchItem(SearchHit<ElasticProductSearchItem> hit) {
        return new ElasticProductSearchItem(
                hit.getContent().id(),
                hit.getContent().title(),
                hit.getContent().description(),
                hit.getContent().quantity(),
                hit.getContent().price(),
                hit.getContent().ownerId(),
                hit.getContent().categoryId(),
                hit.getContent().attributes(),
                hit.getContent().active());
    }
}
