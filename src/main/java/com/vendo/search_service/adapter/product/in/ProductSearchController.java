package com.vendo.search_service.adapter.product.in;

import com.vendo.search_service.adapter.product.in.dto.ProductSearchRequest;
import com.vendo.search_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.search_service.domain.product.search.ProductSearchData;
import com.vendo.search_service.port.ProductSearchUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
class ProductSearchController {

    private final ProductSearchUseCase productSearchUseCase;

    private final DtoProductMapper mapper;

    @PostMapping
    ResponseEntity<ProductSearchData> search(
            @RequestParam(required = false) String q,
            @Valid @RequestBody(required = false) ProductSearchRequest request
    ) {
        ProductSearchData data = productSearchUseCase.search(q, mapper.toSearchItem(request));
        System.out.println(data);
        return ResponseEntity.ok(data);
    }

}
