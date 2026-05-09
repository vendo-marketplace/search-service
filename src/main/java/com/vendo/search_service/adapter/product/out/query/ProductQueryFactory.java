package com.vendo.search_service.adapter.product.out.query;

import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class ProductQueryFactory {

    private final List<QueryBuilder<ProductSearchRequest>> queryBuilders;

    public QueryBuilder<ProductSearchRequest> getBuilder(String field) {
        return queryBuilders.stream().filter(qb -> qb.getField().equals(field))
                .findFirst()
                .orElse(null);
    }

}
