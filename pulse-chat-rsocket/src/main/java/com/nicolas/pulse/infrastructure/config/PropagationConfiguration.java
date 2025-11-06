package com.nicolas.pulse.infrastructure.config;

import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
public class PropagationConfiguration {
    private final MdcProperties mdcProperties;

    public PropagationConfiguration(MdcProperties mdcProperties) {
        this.mdcProperties = mdcProperties;
    }

    @PostConstruct
    public void registerMdc() {
        ContextRegistry.getInstance().registerThreadLocalAccessor(
                mdcProperties.getTraceId(),
                () -> MDC.get(mdcProperties.getTraceId()),
                traceId -> MDC.put(mdcProperties.getTraceId(), traceId),
                () -> MDC.remove(mdcProperties.getTraceId())
        );
        Hooks.enableAutomaticContextPropagation();
    }
}
