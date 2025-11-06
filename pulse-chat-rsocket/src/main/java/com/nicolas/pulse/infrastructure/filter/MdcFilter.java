package com.nicolas.pulse.infrastructure.filter;


import com.nicolas.pulse.infrastructure.config.MdcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Component
@Order(-1)
public class MdcFilter implements WebFilter {
    private final MdcProperties mdcProperties;

    public MdcFilter(
            MdcProperties mdcProperties) {
        this.mdcProperties = mdcProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-Trace-Id")).orElse(UUID.randomUUID().toString());
        return chain.filter(exchange)
                .contextWrite(context -> context.put(mdcProperties.getTraceId(), traceId));
    }
}
