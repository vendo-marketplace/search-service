package com.vendo.search_service.adapter.product.out.query;

import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.utils_lib.StringUtils;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public final class CategoryQueryBuilder implements QueryBuilder<ProductSearchItem> {

    @Override
    public void build(ProductSearchItem payload, NativeQueryBuilder builder) {
        if (!StringUtils.isEmpty(payload.categoryId())) {
            builder.withQuery(query -> query.term(t -> t.field(getField()).value(payload.categoryId())));
        }
    }

    @Override
    public String getField() {
        return "categoryId";
    }
}
