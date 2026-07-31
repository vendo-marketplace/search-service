package com.vendo.search_service.adapter.product.out.strategy;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.search_service.adapter.product.out.QueryContributor;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.domain.product.filter.AddressFilter;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.ADDRESS_CITY;
import static com.vendo.search_service.adapter.product.out.constants.ProductSearchFields.ADDRESS_REGION;

@Component
class AddressQueryContributor implements QueryContributor {

    @Override
    public void contribute(ProductSearchItem request, List<Query> filters) {
        if (request == null || request.addressFilter() == null) {
            return;
        }

        AddressFilter filter = request.addressFilter();
        if (StringUtils.isEmpty(filter.city())) {
            return;
        }

        Query cityQuery = TermQuery.of(b -> b.field(ADDRESS_CITY).value(filter.city()))._toQuery();
        if (StringUtils.isEmpty(filter.region())) {
            filters.add(cityQuery);
            return;
        }

        Query regionQuery = TermQuery.of(b -> b.field(ADDRESS_REGION).value(filter.region()))._toQuery();
        filters.addAll(List.of(cityQuery, regionQuery));
    }
}
