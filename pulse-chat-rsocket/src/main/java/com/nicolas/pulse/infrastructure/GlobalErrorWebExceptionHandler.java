package com.nicolas.pulse.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.CustomerException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@Order(-1)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    public GlobalErrorWebExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error(ex.getMessage(), ex);

        // 1. 根據例外類型獲取 ProblemDetail
        ProblemDetail problemDetail = createProblemDetail(ex, exchange);

        // 2. 設置 HTTP 狀態碼和 Content-Type
        exchange.getResponse().setStatusCode(HttpStatus.resolve(problemDetail.getStatus()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 3. 將 ProblemDetail 序列化為 JSON

        // 4. 將 JSON 寫入回應流
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        return exchange.getResponse().writeWith(Mono.just(bufferFactory.wrap(simplifyProblemDetailToJson(problemDetail))));
    }

    // --- 核心邏輯：將 Throwable 轉換為 ProblemDetail ---

    private ProblemDetail createProblemDetail(Throwable ex, ServerWebExchange exchange) {
        ProblemDetail body;
        HttpStatus status;

        // 1. Security Exeception (原 @ControllerAdvice 無法捕獲的)
        if (ex instanceof AuthenticationException authEx) {
            status = HttpStatus.UNAUTHORIZED;
            body = ProblemDetail.forStatusAndDetail(status, authEx.getMessage());
            body.setTitle("UNAUTHORIZED");
        } else if (ex instanceof AccessDeniedException deniedEx) {
            status = HttpStatus.FORBIDDEN;
            body = ProblemDetail.forStatusAndDetail(status, deniedEx.getMessage());
            body.setTitle("FORBIDDEN");

            // 2. 驗證例外 (WebExchangeBindException)
        } else if (ex instanceof WebExchangeBindException bindEx) {
            status = HttpStatus.BAD_REQUEST;
            body = ProblemDetail.forStatusAndDetail(status, "Validation failed for request body.");
            body.setTitle("VALIDATION_FAILED");
            body.setProperties(Map.of("errors", bindEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> Map.of("field", error.getField(), "message", Objects.requireNonNull(error.getDefaultMessage())))
                    .toList()));

            // 3. 業務/自訂例外 (使用原有的 ProblemDetail 屬性)
        } else if (ex instanceof TargetNotFoundException targetEx) {
            body = targetEx.getBody();
            body.setTitle("TARGET_NOT_FOUND");
        } else if (ex instanceof ConflictException conflictEx) {
            body = conflictEx.getBody();
            body.setTitle("RESOURCE_CONFLICT");
        } else if (ex instanceof CustomerException customerEx) {
            body = customerEx.getBody();
            body.setTitle("BUSINESS_ERROR");

            // 4. 標準 Java 例外 (IllegalArgumentException)
        } else if (ex instanceof IllegalArgumentException illegalEx) {
            status = HttpStatus.BAD_REQUEST;
            body = ProblemDetail.forStatusAndDetail(status, illegalEx.getMessage());
            body.setTitle("ILLEGAL_ARGUMENT_FAILED");

            // 5. 其他未處理的例外 (Handle All)
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 應該改為 500
            body = ProblemDetail.forStatusAndDetail(status, "An internal unhandled error occurred.");
            body.setTitle("UNHANDLED_EXCEPTION");
            body.setProperties(Map.of("error_class_name", ex.getClass().getSimpleName()));
        }

        // 共同屬性設置
        body.setProperties(Map.of("requestId", exchange.getRequest().getId()));
        return body;
    }

    // 簡化的 JSON 序列化，實際項目中應使用 ObjectMapper
    private byte[] simplifyProblemDetailToJson(ProblemDetail pd) {
        try {
            return objectMapper.writeValueAsBytes(pd);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
