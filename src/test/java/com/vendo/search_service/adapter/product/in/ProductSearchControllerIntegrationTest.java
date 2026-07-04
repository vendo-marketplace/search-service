package com.vendo.search_service.adapter.product.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.search_service.adapter.product.in.dto.AttributeFilterRequest;
import com.vendo.search_service.adapter.product.in.dto.AttributeFilterRequest.AttributeRequest;
import com.vendo.search_service.adapter.product.in.dto.PriceRangeFilterRequest;
import com.vendo.search_service.adapter.product.in.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.port.ProductSearchUseCase;
import com.vendo.search_service.test_utils.ProductDataBuilder;
import com.vendo.search_service.test_utils.ProductSearchItemDataBuilder;
import com.vendo.search_service.test_utils.ProductSearchRequestDataBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductSearchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductSearchUseCase productSearchUseCase;

    @Nested
    class SearchTests {

        @Test
        void search_shouldReturnProducts() throws Exception {
            ProductSearchRequest request = ProductSearchRequestDataBuilder.withAllFields().build();
            ProductSearchItem item = ProductSearchItemDataBuilder.withAllFields().build();
            List<Product> products = List.of(ProductDataBuilder.withAllFields().id("p-1").build());

            when(productSearchUseCase.search("laptop", item)).thenReturn(products);

            mockMvc.perform(post("/search")
                            .param("q", "laptop")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value("p-1"))
                    .andExpect(jsonPath("$[0].title").value("Gaming Laptop"));

            verify(productSearchUseCase).search("laptop", item);
        }

        @Test
        void search_shouldReturnEmptyArray_whenNothingFound() throws Exception {
            ProductSearchRequest request = ProductSearchRequestDataBuilder.withAllFields().build();
            ProductSearchItem item = ProductSearchItemDataBuilder.withAllFields().build();

            when(productSearchUseCase.search("laptop", item)).thenReturn(List.of());

            mockMvc.perform(post("/search")
                            .param("q", "laptop")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void search_shouldReturnOk_whenNoQueryParamAndNoBody() throws Exception {
            when(productSearchUseCase.search(isNull(), isNull())).thenReturn(List.of());

            mockMvc.perform(post("/search"))
                    .andExpect(status().isOk());

            verify(productSearchUseCase).search(isNull(), isNull());
        }
    }

    @Test
    void search_shouldReturnBadRequest_whenMinPriceIsNegative() throws Exception {
        ProductSearchRequest request = ProductSearchRequestDataBuilder.empty()
                .priceRangeFilter(new PriceRangeFilterRequest(BigDecimal.valueOf(-1), null))
                .build();

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productSearchUseCase);
    }

    @Test
    void search_shouldReturnOk_whenMinPriceIsZero() throws Exception {
        ProductSearchRequest request = ProductSearchRequestDataBuilder.empty()
                .priceRangeFilter(new PriceRangeFilterRequest(BigDecimal.ZERO, null))
                .build();
        ProductSearchItem item = ProductSearchItemDataBuilder.empty()
                .priceRangeFilter(new com.vendo.search_service.domain.product.filter.PriceRangeFilter(BigDecimal.ZERO, null))
                .build();

        when(productSearchUseCase.search(isNull(), eq(item))).thenReturn(List.of());

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(productSearchUseCase).search(isNull(), eq(item));
    }

    @Test
    void search_shouldReturnBadRequest_whenAttributeIdIsNull() throws Exception {
        ProductSearchRequest request = ProductSearchRequestDataBuilder.empty()
                .attributeFilter(new AttributeFilterRequest(List.of(
                        new AttributeRequest(null, List.of("red")))))
                .build();

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productSearchUseCase);
    }

    @Test
    void search_shouldReturnBadRequest_whenAttributeValuesAreEmpty() throws Exception {
        ProductSearchRequest request = ProductSearchRequestDataBuilder.empty()
                .attributeFilter(new AttributeFilterRequest(List.of(
                        new AttributeRequest("id", List.of()))))
                .build();

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productSearchUseCase);
    }

}