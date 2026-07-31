package com.vendo.search_service.adapter.product.in.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressFilterRequest(
        @NotBlank(message = "City is required.")
        String city,
        String region
) {
}
