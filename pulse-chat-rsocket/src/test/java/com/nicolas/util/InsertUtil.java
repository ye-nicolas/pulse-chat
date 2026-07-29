package com.nicolas.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InsertUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> String getInsert(String tableName, List<T> data, Map<String, Function<T, ?>> mapping) {
        return """
                INSERT INTO %s (%s)
                VALUES %s;
                """.formatted(tableName,
                mapping.keySet().stream().map("\"%s\""::formatted).collect(Collectors.joining(",")),
                data.stream().map(d -> getInsertValueOneRow(d, mapping.values()))
                        .collect(Collectors.joining(",\n"))
        );
    }

    private static <T> String getInsertValueOneRow(T data, Collection<Function<T, ?>> mapping) {
        return """
                (%s)
                """.formatted(mapping.stream()
                .map(f -> Optional.ofNullable(f.apply(data))
                        .map(InsertUtil::valueFormat)
                        .orElse("NULL"))
                .collect(Collectors.joining(",")));
    }

    private static String valueFormat(Object o) {
        return switch (o) {
            case Boolean b -> "%s".formatted(b);
            case Integer i -> "%d".formatted(i);
            case Long l -> "%d".formatted(l);
            case String s -> "'%s'".formatted(s);
            case Instant instant -> "'%s'".formatted(instant.toString());
            case Enum<?> e -> "'%s'".formatted(e.name());
            default -> "'%s'".formatted(objectToString(o));
        };
    }

    private static String objectToString(Object o) {
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
