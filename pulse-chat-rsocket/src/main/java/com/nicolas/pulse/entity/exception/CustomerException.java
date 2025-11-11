package com.nicolas.pulse.entity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CustomerException extends ResponseStatusException {
    public CustomerException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public CustomerException(HttpStatusCode status) {
        super(status);
    }

    public CustomerException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
