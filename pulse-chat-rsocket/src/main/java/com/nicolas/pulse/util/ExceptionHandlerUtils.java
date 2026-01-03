package com.nicolas.pulse.util;

import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.CustomerException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.Objects;

public class ExceptionHandlerUtils {
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String TARGET_NOT_FOUND = "TARGET_NOT_FOUND";
    public static final String RESOURCE_CONFLICT = "RESOURCE_CONFLICT";
    public static final String BUSINESS_ERROR = "BUSINESS_ERROR";
    public static final String ILLEGAL_ARGUMENT_FAILED = "ILLEGAL_ARGUMENT_FAILED";
    public static final String ILLEGAL_STATE_FAILED = "ILLEGAL_STATE_FAILED";
    public static final String UNHANDLED_EXCEPTION = "UNHANDLED_EXCEPTION";
    public static final String BAD_REQUEST = "BAD_REQUEST";

    public static ProblemDetail createProblemDetail(Throwable ex, ServerWebExchange exchange) {
        ProblemDetail body;
        if (ex instanceof AuthenticationException authEx) {
            body = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, authEx.getMessage());
            body.setTitle(UNAUTHORIZED);
        } else if (ex instanceof AccessDeniedException deniedEx) {
            body = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, deniedEx.getMessage());
            body.setTitle(FORBIDDEN);
        } else if (ex instanceof WebExchangeBindException bindEx) {
            body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for request body.");
            body.setTitle(VALIDATION_FAILED);
            body.setProperty("errors", bindEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> Map.of("field", error.getField(), "message", Objects.requireNonNull(error.getDefaultMessage())))
                    .toList());
        } else if (ex instanceof NoResourceFoundException targetEx) {
            body = targetEx.getBody();
            body.setTitle(RESOURCE_NOT_FOUND);
        } else if (ex instanceof TargetNotFoundException targetEx) {
            body = targetEx.getBody();
            body.setTitle(TARGET_NOT_FOUND);
        } else if (ex instanceof ConflictException conflictEx) {
            body = conflictEx.getBody();
            body.setTitle(RESOURCE_CONFLICT);
        } else if (ex instanceof CustomerException customerEx) {
            body = customerEx.getBody();
            body.setTitle(BUSINESS_ERROR);
        } else if (ex instanceof IllegalArgumentException) {
            body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
            body.setTitle(ILLEGAL_ARGUMENT_FAILED);
        } else if (ex instanceof IllegalStateException) {
            body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
            body.setTitle(ILLEGAL_STATE_FAILED);
        } else if (ex instanceof ResponseStatusException targetEx) {
            body = targetEx.getBody();
            body.setTitle(BAD_REQUEST);
        } else {
            body = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            body.setTitle(UNHANDLED_EXCEPTION);
            body.setProperty("errorClassName", ex.getClass().getSimpleName());
        }

        body.setProperty("requestId", exchange.getRequest().getId());
        return body;
    }
}
