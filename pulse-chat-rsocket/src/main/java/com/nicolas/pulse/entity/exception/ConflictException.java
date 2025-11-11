package com.nicolas.pulse.entity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ConflictException extends CustomerException {
    public ConflictException(String reason) {
        super(HttpStatus.CONFLICT, reason);
    }

    public ConflictException(String reason, Throwable cause) {
        super(HttpStatus.CONFLICT, reason, cause);
    }

    public ConflictException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

    public ConflictException(HttpStatusCode status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
