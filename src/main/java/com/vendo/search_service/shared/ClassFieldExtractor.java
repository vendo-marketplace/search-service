package com.vendo.search_service.shared;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public final class ClassFieldExtractor {

    public static <T> Set<String> extract(Class<T> tClass) {
        Set<String> extracted = new HashSet<>();

        for (Field field : tClass.getDeclaredFields()) {
            field.setAccessible(true);
            extracted.add(field.getName());
        }

        return extracted;
    }
}
