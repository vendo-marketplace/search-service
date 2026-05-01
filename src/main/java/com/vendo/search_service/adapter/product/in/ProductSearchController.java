package com.vendo.search_service.adapter.product.in;

import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/search/products")
@RequiredArgsConstructor
class ProductSearchController {

    private final ProductSearchUseCase productSearchUseCase;

    @GetMapping
    ResponseEntity<List<ProductSearchItem>> search(@RequestParam String q) {
        return ResponseEntity.ok(productSearchUseCase.search(q));
    }

}
