package com.vendo.search_service.adapter.product.out.mapper;

import com.vendo.search_service.adapter.product.in.dto.ProductSearchRequest;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.infrastructure.mapper.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DtoProductMapper {

    ProductSearchItem toSearchItem(ProductSearchRequest request);

}
