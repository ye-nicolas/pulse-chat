package com.nicolas.pulse.infrastructure.config;

import io.micrometer.common.lang.NonNullApi;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.plugins.RSocketInterceptor;
import io.rsocket.util.RSocketProxy;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Configuration
public class RSocketServerConfig {
    private final MdcProperties mdcProperties;

    public RSocketServerConfig(MdcProperties mdcProperties) {
        this.mdcProperties = mdcProperties;
    }

    @Bean
    public RSocketServerCustomizer rsocketServerCustomizer() {
        return server -> server.interceptors(registry -> {
            registry.forResponder(new ServerMdcInterceptor());
        });
    }

    @NonNullApi
    class ServerMdcInterceptor implements RSocketInterceptor {

        @Override
        public RSocket apply(RSocket rsocket) {
            return new RSocketProxy(rsocket) {
                @Override
                public Mono<Void> fireAndForget(Payload payload) {
                    return super.fireAndForget(payload)
                            .contextWrite(this::addTraceId);
                }

                @Override
                public Mono<Payload> requestResponse(Payload payload) {
                    return super.requestResponse(payload)
                            .contextWrite(this::addTraceId);
                }

                @Override
                public Flux<Payload> requestStream(Payload payload) {
                    return super.requestStream(payload)
                            .contextWrite(this::addTraceId);
                }

                private Context addTraceId(Context context) {
                    if (!context.hasKey(mdcProperties.getTraceId())) {
                        return context.put(mdcProperties.getTraceId(), ObjectUtils.getIdentityHexString(this));
                    }
                    return context;
                }
            };
        }
    }
}
