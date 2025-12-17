package com.nicolas.pulse.infrastructure.config;

import com.nicolas.pulse.infrastructure.CustomerJwtReactiveAuthenticationManager;
import com.nicolas.pulse.infrastructure.filter.MdcFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@EnableReactiveMethodSecurity
@EnableRSocketSecurity
@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {
    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final MdcFilter mdcFilter;
    private final CustomerJwtReactiveAuthenticationManager customerJwtReactiveAuthenticationManager;

    public SecurityConfiguration(ReactiveUserDetailsService reactiveUserDetailsService,
                                 MdcProperties mdcProperties,
                                 CustomerJwtReactiveAuthenticationManager customerJwtReactiveAuthenticationManager) {
        this.reactiveUserDetailsService = reactiveUserDetailsService;
        this.mdcFilter = new MdcFilter(mdcProperties);
        this.customerJwtReactiveAuthenticationManager = customerJwtReactiveAuthenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(customerJwtReactiveAuthenticationManager);
        jwtFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> {
//                    exchanges.pathMatchers(AuthController.AUTH_BASE_URL).permitAll();
                    exchanges.anyExchange().permitAll();
                })
                .addFilterAt(mdcFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(e -> e
                        .accessDeniedHandler((webExchange, accessDeniedException) -> Mono.error(accessDeniedException))
                        .authenticationEntryPoint((webExchange, authenticationException) -> Mono.error(authenticationException)))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket,
                                                        @Qualifier("CustomerReactiveAuthenticationManager") ReactiveAuthenticationManager reactiveAuthenticationManager) {
        return rsocket.authorizePayload(authorize -> authorize
                        .setup().authenticated()
                        .anyRequest().authenticated())
                .jwt(jwtSpec -> jwtSpec.authenticationManager(reactiveAuthenticationManager))
                .build();
    }


    @Bean("UserDetailsRepositoryReactiveAuthenticationManager")
    public UserDetailsRepositoryReactiveAuthenticationManager getUserDetailsRepositoryReactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager reactiveAuthenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        reactiveAuthenticationManager.setPasswordEncoder(passwordEncoder());
        return reactiveAuthenticationManager;
    }

    @Bean
    public RSocketStrategiesCustomizer rsocketStrategiesCustomizer() {
        return strategies -> {
            strategies.encoder(new BearerTokenAuthenticationEncoder());
        };
    }
}
