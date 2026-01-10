package com.nicolas.pulse.infrastructure.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityExceptionHandler implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public SecurityExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return renderErrorResponse(exchange, denied);
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return renderErrorResponse(exchange, ex);
    }

    private byte[] simplifyProblemDetailToJson(ProblemDetail pd) {
        try {
            return objectMapper.writeValueAsBytes(pd);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Mono<Void> renderErrorResponse(ServerWebExchange exchange, Exception ex) {
        return Mono.defer(() -> {
            ProblemDetail problemDetail = ExceptionHandlerUtils.createProblemDetail(ex, exchange.getRequest().getId());
            exchange.getResponse().setStatusCode(HttpStatus.resolve(problemDetail.getStatus()));
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(simplifyProblemDetailToJson(problemDetail));
            return exchange.getResponse().writeWith(Mono.just(buffer))
                    .doOnError(error -> DataBufferUtils.release(buffer));
        });
    }
}
