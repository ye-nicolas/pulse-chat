package com.nicolas.pulse.infrastructure.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(5)
public class LogWebFilter implements WebFilter {
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public LogWebFilter(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return requestMappingHandlerMapping.getHandler(exchange)
                .flatMap(object -> {
                    String name;
                    if (object instanceof HandlerMethod hm) {
                        name = hm.getMethod().getName();
                        log.info("Start: {}, ", name);
                    } else {
                        name = object.getClass().getSimpleName();
                    }
                    return chain.filter(exchange).doFinally(signalType -> {
                        log.info("END: {}", name);
                    });
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
