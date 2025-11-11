package com.nicolas.pulse.entity.exception;

import org.springframework.http.HttpStatusCode;

public class TargetNotFoundException extends CustomerException {
    public TargetNotFoundException(String reason) {
        super(reason);
    }

    public TargetNotFoundException(HttpStatusCode status) {
        super(status);
    }

    public TargetNotFoundException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
