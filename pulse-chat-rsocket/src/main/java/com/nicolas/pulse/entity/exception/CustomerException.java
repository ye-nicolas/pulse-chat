package com.nicolas.pulse.entity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CustomerException extends ResponseStatusException {
    public CustomerException(String reason) {
        this(HttpStatus.BAD_REQUEST, reason);
    }

    public CustomerException(String reason, Throwable cause) {
        this(HttpStatus.BAD_REQUEST, reason, cause);
    }

    public CustomerException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

    public CustomerException(HttpStatusCode status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
