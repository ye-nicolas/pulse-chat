package com.nicolas.pulse.entity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TargetNotFoundException extends CustomerException {
    public TargetNotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }

    public TargetNotFoundException(String reason, Throwable cause) {
        super(HttpStatus.NOT_FOUND, reason, cause);
    }

    public TargetNotFoundException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

    public TargetNotFoundException(HttpStatusCode status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
