package com.vendo.search_service.adapter.product.out.query;

import com.vendo.search_service.domain.product.ProductSearchItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class ProductQueryFactory {

    private final List<QueryBuilder<ProductSearchItem>> queryBuilders;

    public QueryBuilder<ProductSearchItem> getBuilder(String field) {
        return queryBuilders.stream().filter(qb -> qb.getField().equals(field))
                .findFirst()
                .orElse(null);
    }

}
