package com.vendo.search_service.adapter.product.out.strategy;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.search_service.adapter.product.out.QueryContributor;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.PRICE;

@Component
class PriceQueryContributor implements QueryContributor {

    @Override
    public void contribute(ProductSearchItem request, List<Query> filters) {
        if (request == null || request.getPriceRangeFilter() == null) {
            return;
        }

        BigDecimal min = request.getPriceRangeFilter().minPrice();
        BigDecimal max = request.getPriceRangeFilter().maxPrice();

        if (min == null && max == null) {
            return;
        }

        Query query = Query.of(builder -> builder
                .range(r -> r
                        .number(n -> {
                            n.field(PRICE);

                            if (min != null) {
                                n.gte(min.doubleValue());
                            }

                            if (max != null) {
                                n.lte(max.doubleValue());
                            }

                            return n;
                        })));

        filters.add(query);
    }
}
