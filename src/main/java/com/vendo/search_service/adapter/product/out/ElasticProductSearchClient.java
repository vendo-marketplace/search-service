package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.product.out.query.ProductQueryFactory;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.shared.ClassFieldExtractor;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProductSearchClient implements SearchRepository<ElasticProductSearchItem, ProductSearchItem> {

    @Value("${product.search.size}")
    private int DEFAULT_SIZE;
    @Value("${product.search.page}")
    private int DEFAULT_PAGE;

    private static final String FUZZINESS_MODE = "AUTO";
    private static final String FIELD_PRIORITY = "^3";

    private static final String TITLE_FIELD = "title";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String ATTRIBUTES_FIELD = "attributes";
    private static final String ATTRIBUTES_VALUES_FIELD = ATTRIBUTES_FIELD + ".values";

    private final ElasticsearchOperations operations;
    private final ProductQueryFactory productQueryFactory;
    private final NativeQueryBuilder queryBuilder = NativeQuery.builder();

    @Override
    public List<ElasticProductSearchItem> search(String q, ProductSearchItem searchItem) {
        withPageable(PageRequest.of(getPage(searchItem), getSize(searchItem)), queryBuilder);

        if (StringUtils.isEmpty(q) && Objects.isNull(searchItem)) return search();

        ClassFieldExtractor.extract(Product.class).stream()
                .map(productQueryFactory::getBuilder)
                .filter(Objects::nonNull)
                .forEach(qb -> qb.build(searchItem, queryBuilder));

        withQuery(q, queryBuilder);
        return search();
    }

    private void withPageable(PageRequest page, NativeQueryBuilder builder) {
        builder.withPageable(page);
    }

    private void withQuery(String q, NativeQueryBuilder builder) {
        if (Optional.ofNullable(q).isPresent()) {
            builder.withQuery(query -> query.multiMatch(mm -> mm
                    .query(q)
                    .fields(TITLE_FIELD + FIELD_PRIORITY, DESCRIPTION_FIELD, ATTRIBUTES_VALUES_FIELD)
                    .fuzziness(FUZZINESS_MODE)
            ));
        }
    }

    private List<ElasticProductSearchItem> search() {
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
