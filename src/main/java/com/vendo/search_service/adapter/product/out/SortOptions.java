package com.vendo.search_service.adapter.product.out;

import co.elastic.clients.elasticsearch._types.SortOrder;

record SortOptions(String sortField, SortOrder order) {
}
