package com.vendo.search_service.adapter.product.out.query;

import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;

public interface QueryBuilder<T> {

    void build(T payload, NativeQueryBuilder builder);

    String getField();

}
