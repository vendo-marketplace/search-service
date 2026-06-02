package com.vendo.search_service.adapter.product.out;

import com.vendo.search_service.adapter.product.out.mapper.ElasticProductMapper;
import com.vendo.search_service.adapter.search.SearchRepository;
import com.vendo.search_service.domain.product.Product;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.test_utils.ElasticProductSearchItemDataBuilder;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticProductSearchAdapterTest {

    @Mock
    private SearchRepository<ElasticProductSearchItem, ProductSearchItem> repository;

    @Mock
    private ElasticProductMapper mapper;

    @InjectMocks
    private ElasticProductSearchAdapter adapter;

    @Test
    void search_shouldDelegateToRepositoryAndMapResult() {
        String q = "laptop";
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.withAllFields().build();
        List<ElasticProductSearchItem> entities = List.of(ElasticProductSearchItemDataBuilder.withAllFields().build());
        List<Product> products = List.of(ProductDataBuilder.withAllFields().build());

        when(repository.search(q, searchItem)).thenReturn(entities);
        when(mapper.toProducts(entities)).thenReturn(products);

        List<Product> result = adapter.search(q, searchItem);

        assertThat(result).isEqualTo(products);
        verify(repository).search(q, searchItem);
        verify(mapper).toProducts(entities);
    }

    @Test
    void search_shouldReturnEmptyList_whenRepositoryReturnsEmpty() {
        when(repository.search(any(), any())).thenReturn(List.of());
        when(mapper.toProducts(anyList())).thenReturn(List.of());

        List<Product> result = adapter.search("laptop", ProductSearchItemDataBuilder.empty().build());

        assertThat(result).isEmpty();
        verify(repository).search(any(), any());
        verify(mapper).toProducts(anyList());
    }

    @Test
    void search_shouldPassNullQueryThroughToRepository() {
        ProductSearchItem searchItem = ProductSearchItemDataBuilder.empty().build();
        when(repository.search(null, searchItem)).thenReturn(List.of());
        when(mapper.toProducts(anyList())).thenReturn(List.of());

        adapter.search(null, searchItem);

        verify(repository).search(null, searchItem);
    }
}