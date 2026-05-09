package com.vendo.search_service.adapter.product.out.query;

import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class ActiveQueryBuilder implements QueryBuilder<ProductSearchRequest> {

    @Override
    public void build(ProductSearchRequest payload, NativeQueryBuilder builder) {
        if (Optional.ofNullable(payload.active()).isPresent()) {
            builder.withFilter(f -> f.term(t -> t.field(getField()).value(payload.active())));
        }
    }

    @Override
    public String getField() {
        return "active";
    }
}
