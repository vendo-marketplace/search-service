package com.vendo.search_service.adapter.product.out.mapper;

import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem;
import com.vendo.search_service.domain.product.ProductSearchItem;
import com.vendo.search_service.infrastructure.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ElasticProductMapper {

    List<ProductSearchItem> toProducts(List<ElasticProductSearchItem> entities);

}
