package com.vendo.search_service.adapter.product.in;

import com.vendo.search_service.adapter.product.in.dto.ProductSearchRequest;
import com.vendo.search_service.adapter.product.in.dto.ProductSearchResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/search/products")
class ProductSearchController {

    @PostMapping
    ProductSearchResponse search(@RequestBody ProductSearchRequest request) {

    }

}
