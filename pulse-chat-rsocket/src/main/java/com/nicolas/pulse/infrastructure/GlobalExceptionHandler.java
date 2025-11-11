package com.nicolas.pulse.infrastructure;

import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.CustomerException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

// 參考 ResponseEntityExceptionHandler
@ControllerAdvice
public class GlobalExceptionHandler {

    // 處理@Valid 或 @Validated 驗證，WebFlux是WebExchangeBindException，MVC是MethodArgumentNotValidException，共同實作BindingResult
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleMethodArgumentNotValidException(WebExchangeBindException ex, ServerWebExchange exchange) {
        record KeyValue(String field, String value) {
        }
        ProblemDetail body = ex.getBody();
        body.setTitle("VALIDATION_FAILED");
        body.setProperties(Map.of(
                "error", ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> new KeyValue(error.getField(), error.getDefaultMessage()))
                        .toList()));
        return toResponse(body, exchange);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        body.setTitle("ILLEGAL_ARGUMENT_FAILED");
        return toResponse(body, exchange);
    }

    @ExceptionHandler(TargetNotFoundException.class)
    public final Mono<ResponseEntity<ProblemDetail>> handleTargetNotFoundException(TargetNotFoundException e, ServerWebExchange exchange) {
        ProblemDetail body = e.getBody();
        body.setTitle("TARGET_NOT_FOUND");
        return toResponse(body, exchange);
    }

    @ExceptionHandler(ConflictException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleConflictException(ConflictException e, ServerWebExchange exchange) {
        ProblemDetail body = e.getBody();
        body.setTitle("RESOURCE_CONFLICT");
        return toResponse(body, exchange);
    }

    @ExceptionHandler(CustomerException.class)
    public final Mono<ResponseEntity<ProblemDetail>> handleCustomerException(CustomerException ex, ServerWebExchange exchange) {
        ProblemDetail body = ex.getBody();
        body.setTitle("BUSINESS_ERROR");
        return toResponse(body, exchange);
    }

    @ExceptionHandler(Exception.class)
    public final Mono<ResponseEntity<ProblemDetail>> handleAll(Exception ex, ServerWebExchange exchange) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        body.setTitle("UNHANDLED_EXCEPTION");
        body.setProperties(Map.of("error_class_name", ex.getClass().getSimpleName()));
        return toResponse(body, exchange);
    }

    private Mono<ResponseEntity<ProblemDetail>> toResponse(ProblemDetail body, ServerWebExchange exchange) {
        body.setProperties(Map.of("requestId", exchange.getRequest().getId()));
        return Mono.just(ResponseEntity.status(body.getStatus()).body(body));
    }
}
