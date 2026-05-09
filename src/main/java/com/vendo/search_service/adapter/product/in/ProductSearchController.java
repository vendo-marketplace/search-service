package com.vendo.search_service.adapter.product.in;

import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/products")
@RequiredArgsConstructor
class ProductSearchController {

    private final ProductSearchUseCase productSearchUseCase;

    @GetMapping("/search")
    ResponseEntity<List<ProductSearchItem>> search(
            @RequestParam String q,
            @RequestBody(required = false) ProductSearchRequest request
    ) {
        return ResponseEntity.ok(productSearchUseCase.search(q, request));
    }

}
