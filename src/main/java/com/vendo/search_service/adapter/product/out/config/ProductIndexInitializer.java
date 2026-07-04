package com.vendo.search_service.adapter.product.out.config;

import com.vendo.search_service.adapter.product.out.ElasticProductSearchItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductIndexInitializer {

    private final ElasticsearchOperations operations;

    @EventListener(ApplicationRunner.class)
    public void init() {
        IndexOperations indexOps = operations.indexOps(ElasticProductSearchItem.class);

        if (!indexOps.exists()) {
            log.info("Initializing index for product.");
            indexOps.create();
            indexOps.putMapping();
        }
    }

}
