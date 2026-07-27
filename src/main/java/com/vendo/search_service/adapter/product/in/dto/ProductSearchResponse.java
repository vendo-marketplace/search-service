package com.vendo.search_service.adapter.product.in.dto;

import com.vendo.search_service.domain.product.Product;

import java.util.List;

public record ProductSearchResponse(List<Product> data) {
}
