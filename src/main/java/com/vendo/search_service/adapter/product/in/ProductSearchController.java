package com.vendo.search_service.adapter.product.in;

import com.vendo.search_service.application.product.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/products")
@RequiredArgsConstructor
class ProductSearchController {

    private final ProductSearchUseCase productSearchUseCase;

    @PostMapping("/search")
    ResponseEntity<List<ProductSearchItem>> search(
            @RequestParam String q,
            @RequestBody(required = false) ProductSearchRequest request
    ) {
        return ResponseEntity.ok(productSearchUseCase.search(q, request));
    }

}
