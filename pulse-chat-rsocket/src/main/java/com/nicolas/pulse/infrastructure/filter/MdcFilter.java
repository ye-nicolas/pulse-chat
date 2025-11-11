package com.nicolas.pulse.infrastructure.filter;


import com.nicolas.pulse.infrastructure.config.MdcProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

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
        String traceId = exchange.getRequest().getId();
        return chain.filter(exchange)
                .contextWrite(context -> context.put(mdcProperties.getTraceId(), traceId));
    }
}
