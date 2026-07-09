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
import com.vendo.security_lib.exception.response.ExceptionResponse;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        String content = mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        assertThat(content).isNotNull();
        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).isNotNull();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
        assertThat(exceptionResponse.getErrors().get("priceRangeFilter.minPrice")).isEqualTo("Minimal price must not be less than zero.");
        assertThat(exceptionResponse.getPath()).isEqualTo("/search");
        assertThat(exceptionResponse.getCode()).isEqualTo(400);

        verifyNoInteractions(productSearchUseCase);
    }

    @Test
    void search_shouldReturnBadRequest_whenPageIsNegativeAndSizeIsLessThanOne() throws Exception {
        ProductSearchRequest request = ProductSearchRequestDataBuilder.empty()
                .page(-1)
                .size(0)
                .build();

        String content = mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        assertThat(content).isNotNull();
        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).isNotNull();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(2);
        assertThat(exceptionResponse.getErrors().get("page")).isEqualTo("Page must not be less than zero.");
        assertThat(exceptionResponse.getErrors().get("size")).isEqualTo("Page size must not be less than one.");
        assertThat(exceptionResponse.getPath()).isEqualTo("/search");
        assertThat(exceptionResponse.getCode()).isEqualTo(400);

        verifyNoInteractions(productSearchUseCase);
    }

    @Test
    void search_shouldReturnBadRequest_whenMinPriceRangeIsHigherThanMaxPrice() throws Exception {
        ProductSearchRequest request = ProductSearchRequestDataBuilder.empty()
                .priceRangeFilter(new PriceRangeFilterRequest(BigDecimal.valueOf(100), BigDecimal.valueOf(1)))
                .build();

        String content = mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        assertThat(content).isNotNull();
        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).isNotNull();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
        assertThat(exceptionResponse.getErrors().get("priceRangeFilter")).isEqualTo("Maximum price must be greater than or equal to minimum price.");
        assertThat(exceptionResponse.getPath()).isEqualTo("/search");
        assertThat(exceptionResponse.getCode()).isEqualTo(400);

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

        String content = mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        verifyNoInteractions(productSearchUseCase);

        assertThat(content).isNotNull();
        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).isNotNull();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
        assertThat(exceptionResponse.getErrors().get("attributeFilter.attributes[0].id")).isEqualTo("Attribute Id is required.");
        assertThat(exceptionResponse.getPath()).isEqualTo("/search");
        assertThat(exceptionResponse.getCode()).isEqualTo(400);
    }

    @Test
    void search_shouldReturnBadRequest_whenAttributeValuesAreEmpty() throws Exception {
        ProductSearchRequest request = ProductSearchRequestDataBuilder.empty()
                .attributeFilter(new AttributeFilterRequest(List.of(
                        new AttributeRequest("id", List.of()))))
                .build();

        String content = mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        verifyNoInteractions(productSearchUseCase);

        assertThat(content).isNotNull();
        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).isNotNull();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
        assertThat(exceptionResponse.getErrors().get("attributeFilter.attributes[0].values")).isEqualTo("Attribute values are required.");
        assertThat(exceptionResponse.getPath()).isEqualTo("/search");
        assertThat(exceptionResponse.getCode()).isEqualTo(400);
    }

}