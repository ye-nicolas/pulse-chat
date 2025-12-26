package com.nicolas.pulse.util;

import java.time.Instant;
import java.time.OffsetDateTime;

public class TypeUtil {
    public static Instant toInstant(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toInstant();
    }
}
