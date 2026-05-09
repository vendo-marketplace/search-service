package com.vendo.search_service.domain.search;

import java.util.List;

public record AttributeFilter(List<Attribute> attributes) {

    public record Attribute(
            String id,
            String title,
            String type,
            List<String> values
    ) {}
}
