package com.vendo.search_service.adapter.product.out.nested;

import lombok.AllArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

@AllArgsConstructor
public class ElasticAddress {
    @Field(type = FieldType.Keyword)
    private String region;
    @Field(type = FieldType.Keyword)
    private String city;

    @GeoPointField
    private ElasticLocation location;

    @AllArgsConstructor
    public static class ElasticLocation {
        private double lat;
        private double lon;
    }
}



