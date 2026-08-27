package com.vendo.search_service.adapter.product.out.strategy;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.search_service.adapter.product.out.QueryContributor;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.ID;

@Component
public class ProductIdsQueryContributor implements QueryContributor {

    @Override
    public void contribute(ProductSearchItem request, List<Query> filters) {
        if (request == null || CollectionUtils.isEmpty(request.getIds())) {
            return;
        }

        TermsQuery idsQuery = QueryBuilders.terms()
                .field(ID)
                .terms(b -> b.value(toFieldValues(request.getIds())))
                .build();

        filters.add(idsQuery._toQuery());
    }

    private List<FieldValue> toFieldValues(List<String> ids) {
        return ids.stream()
                .map(FieldValue::of)
                .toList();
    }


}
