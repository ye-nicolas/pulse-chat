package com.nicolas.pulse.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Component
@Order(-1)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;
    private final Tracer tracer;

    public GlobalErrorWebExceptionHandler(ObjectMapper objectMapper, Tracer tracer) {
        this.objectMapper = objectMapper;
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error(ex.getMessage(), ex);

        // 1. 根據例外類型獲取 ProblemDetail
        String traceId = (tracer.currentSpan() != null) ? Objects.requireNonNull(tracer.currentSpan()).context().traceId() : exchange.getRequest().getId();
        ProblemDetail problemDetail = ExceptionHandlerUtils.createProblemDetail(ex, traceId);

        // 2. 設置 HTTP 狀態碼和 Content-Type
        exchange.getResponse().setStatusCode(HttpStatus.resolve(problemDetail.getStatus()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 3. 將 ProblemDetail 序列化為 JSON

        // 4. 將 JSON 寫入回應流
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        return exchange.getResponse().writeWith(Mono.just(bufferFactory.wrap(simplifyProblemDetailToJson(problemDetail))));
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
