package com.vendo.search_service.adapter.product.in.annotation;

import com.vendo.search_service.adapter.product.in.dto.PriceRangeFilterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, PriceRangeFilterRequest> {

    @Override
    public boolean isValid(PriceRangeFilterRequest value, ConstraintValidatorContext context) {

        if (value == null
                || value.minPrice() == null
                || value.maxPrice() == null
        ) {
            return true;
        }

        return value.maxPrice().compareTo(value.minPrice()) >= 0;
    }

}
