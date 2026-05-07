package com.vendo.search_service.domain.search;

import java.util.List;

public record AttributeFilter(

        String title,
        List<String> values

) {
}
