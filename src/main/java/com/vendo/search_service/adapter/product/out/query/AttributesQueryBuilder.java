package com.vendo.search_service.adapter.product.out.query;

import co.elastic.clients.elasticsearch._types.FieldValue;
import com.vendo.search_service.domain.product.AttributeFilter;
import com.vendo.search_service.domain.product.ProductSearchItem;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class AttributesQueryBuilder implements QueryBuilder<ProductSearchItem> {

    @Override
    public void build(ProductSearchItem payload, NativeQueryBuilder builder) {
        AttributeFilter filter = payload.attributeFilter();
        if (Optional.ofNullable(filter).isPresent() && !filter.attributes().isEmpty()) {
            filter.attributes()
                    .forEach(attribute -> builder
                            .withFilter(f -> f
                                    .terms(t -> t
                                            .field(attribute.id())
                                            .terms(s -> s
                                                    .value(attribute.values().stream().map(FieldValue::of).toList()))))
                    );
        }
    }

    @Override
    public String getField() {
        return "attributes";
    }
}
