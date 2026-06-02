package com.vendo.search_service.application.product;

import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchPort;
import com.vendo.search_service.test_utils.ProductDataBuilder;
import com.vendo.search_service.test_utils.ProductSearchItemDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {

    @Mock
    private ProductSearchPort productSearchPort;

    @InjectMocks
    private ProductSearchService service;

    @Test
    void search_shouldDelegateToPortAndReturnResult() {
        String q = "laptop";
        ProductSearchItem item = ProductSearchItemDataBuilder.withAllFields().build();
        List<Product> products = List.of(ProductDataBuilder.withAllFields().build());

        when(productSearchPort.search(q, item)).thenReturn(products);

        List<Product> result = service.search(q, item);

        assertThat(result).isEqualTo(products);
        verify(productSearchPort).search(q, item);
        verifyNoMoreInteractions(productSearchPort);
    }

    @Test
    void search_shouldReturnEmptyList_whenPortReturnsEmpty() {
        when(productSearchPort.search(any(), any())).thenReturn(List.of());

        List<Product> result = service.search("laptop", ProductSearchItemDataBuilder.empty().build());

        assertThat(result).isEmpty();
    }

    @Test
    void search_shouldPassNullQueryThrough() {
        ProductSearchItem item = ProductSearchItemDataBuilder.empty().build();
        when(productSearchPort.search(null, item)).thenReturn(List.of());

        service.search(null, item);

        verify(productSearchPort).search(null, item);
    }
}