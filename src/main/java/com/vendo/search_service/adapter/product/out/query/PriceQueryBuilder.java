package com.vendo.search_service.adapter.product.out.query;

import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public final class PriceQueryBuilder implements QueryBuilder<ProductSearchRequest> {

    @Override
    public void build(ProductSearchRequest payload, NativeQueryBuilder builder) {
        Optional<BigDecimal> minOpt = Optional.ofNullable(payload.minPrice());
        Optional<BigDecimal> maxOpt = Optional.ofNullable(payload.maxPrice());

        if (minOpt.isEmpty() || maxOpt.isPresent()) {
            builder.withQuery(query -> query.bool(b -> b.filter(f -> f.range(r -> r.number(n -> {
                n.field(getField());

                if (minOpt.isPresent()) {
                    n.gte(payload.minPrice().doubleValue());
                }

                if (maxOpt.isPresent()) {
                    n.lte(payload.maxPrice().doubleValue());
                }

                return n;
            })))));
        }
    }

    @Override
    public String getField() {
        return "price";
    }
}
