package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.vendo.search_service.domain.product.search.ProductSearchItem;

import java.util.List;

public interface QueryContributor {

    void contribute(ProductSearchItem request, List<Query> filters);

}
