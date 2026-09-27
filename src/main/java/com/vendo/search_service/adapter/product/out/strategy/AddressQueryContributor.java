package com.vendo.search_service.adapter.product.out.strategy;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.search_service.adapter.product.out.QueryContributor;
import com.vendo.search_service.domain.product.search.ProductSearchItem;
import com.vendo.search_service.domain.product.search.filter.AddressFilter;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.ADDRESS_CITY;

@Component
class AddressQueryContributor implements QueryContributor {

    @Override
    public void contribute(ProductSearchItem request, List<Query> filters) {
        if (request == null || request.getAddressFilter() == null) {
            return;
        }

        AddressFilter filter = request.getAddressFilter();
        if (StringUtils.isEmpty(filter.city())) {
            return;
        }

        Query cityQuery = TermQuery.of(b -> b.field(ADDRESS_CITY).value(filter.city()))._toQuery();
        filters.add(cityQuery);
    }
}
