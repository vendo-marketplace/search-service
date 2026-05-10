package com.vendo.search_service.domain.product;

import java.util.List;

public record AttributeFilter(List<Attribute> attributes) {

    public record Attribute(
            String id,
            List<String> values
    ) {}
}
