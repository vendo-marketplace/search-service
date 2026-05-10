package com.vendo.search_service.adapter.product.in.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AttributeFilterRequest(List<AttributeRequest> attributes) {

    public record AttributeRequest(
            @NotNull(message = "Attribute Id is required.")
            String id,

            @NotEmpty(message = "Attribute values are required.")
            List<String> values
    ) {}
}
