package com.vendo.search_service.adapter.product.out.strategy;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.search_service.adapter.product.out.QueryContributor;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.IS_NEW;

@Component
class IsNewQueryContributor implements QueryContributor {

    @Override
    public void contribute(ProductSearchItem request, List<Query> filters) {
        if (request == null || request.getIsNew() == null) {
            return;
        }

        Query query = Query.of(builder -> builder
                .term(t -> t
                        .field(IS_NEW)
                        .value(request.getIsNew())));

        filters.add(query);
    }
}
