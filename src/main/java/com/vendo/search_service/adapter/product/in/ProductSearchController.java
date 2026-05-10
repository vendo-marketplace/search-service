package com.vendo.search_service.adapter.product.in;

import com.vendo.search_service.adapter.product.in.dto.ProductSearchRequest;
import com.vendo.search_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.port.ProductSearchUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/products")
@RequiredArgsConstructor
class ProductSearchController {

    private final ProductSearchUseCase productSearchUseCase;

    private final DtoProductMapper mapper;

    @PostMapping("/search")
    ResponseEntity<List<Product>> search(
            @RequestParam(required = false) String q,
            @Valid @RequestBody(required = false) ProductSearchRequest request
    ) {
        return ResponseEntity.ok(productSearchUseCase.search(q, mapper.toSearchItem(request)));
    }

}
