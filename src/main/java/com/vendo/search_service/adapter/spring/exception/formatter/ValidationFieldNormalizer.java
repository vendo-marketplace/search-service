package com.vendo.search_service.adapter.spring.exception.formatter;

import com.vendo.core_lib.util.FieldNormalizer;
import org.springframework.stereotype.Component;

@Component
public class ValidationFieldNormalizer implements FieldNormalizer<String, String> {

    private static final String ARRAY_BRACKET_OPEN = "[";

    private static final String ARRAY_BRACKET_CLOSE = "]";

    @Override
    public String normalize(String field) {
        if (isNestedValidation(field)) {
            field = retrieveNestedField(field);
        }
        return field;
    }

    private static boolean isNestedValidation(String validationField) {
        return validationField.contains(ARRAY_BRACKET_OPEN) && validationField.contains(ARRAY_BRACKET_CLOSE);
    }

    private static String retrieveNestedField(String field) throws StringIndexOutOfBoundsException {
        int start = field.lastIndexOf(ARRAY_BRACKET_OPEN);
        int end = field.indexOf(ARRAY_BRACKET_CLOSE);

        if (start < 0 || end < 0 || start >= end) {
            return field;
        }

        return field.substring(start + 1, end);
    }
}
