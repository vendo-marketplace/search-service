package com.vendo.search_service.domain.product;

import com.vendo.search_service.domain.product.nested.Address;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record Product(
        String id,

        String title,
        String description,
        Integer quantity,
        BigDecimal price,
        List<Attribute> attributes,
        List<String> images,
        Address address,

        Boolean isNew,
        Boolean active,

        String ownerId,
        String categoryId,

        Instant createdAt
        ) {

    public record Attribute(
            String id,
            List<String> values) {
    }

}
