package com.vendo.search_service.adapter.product.out.strategy;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.search_service.adapter.product.out.QueryContributor;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.search.filter.AttributeFilter;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.*;

@Component
class AttributesQueryContributor implements QueryContributor {

    @Override
    public void contribute(ProductSearchItem request, List<Query> filters) {
        if (request == null || request.getAttributeFilter() == null) {
            return;
        }

        AttributeFilter filter = request.getAttributeFilter();
        if (CollectionUtils.isEmpty(filter.attributes())) {
            return;
        }

        List<Query> queries = filter.attributes().stream().map(attribute -> Query.of(q -> q.nested(n -> n
                .path(ATTRIBUTES)
                .query(nq -> nq
                        .bool(b -> b
                                .must(m -> m.term(t -> t
                                        .field(ATTRIBUTES_ID)
                                        .value(attribute.id())))
                                .must(m -> m.terms(t -> t
                                        .field(ATTRIBUTES_VALUES)
                                        .terms(ts -> ts.value(
                                                attribute.values()
                                                        .stream()
                                                        .map(FieldValue::of)
                                                        .toList()))))))))
        ).toList();

        filters.addAll(queries);
    }

}
