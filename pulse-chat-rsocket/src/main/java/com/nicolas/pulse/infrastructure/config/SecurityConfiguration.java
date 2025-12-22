package com.nicolas.pulse.infrastructure.config;

import com.nicolas.pulse.adapter.controller.AuthController;
import com.nicolas.pulse.infrastructure.filter.MdcFilter;
import com.nicolas.pulse.infrastructure.security.CustomerJwtReactiveAuthenticationManager;
import com.nicolas.pulse.infrastructure.security.SecurityExceptionHandler;
import com.nicolas.pulse.service.usecase.account.ReactiveUserDetailsServiceImpl;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.rsocket.metadata.BearerTokenAuthenticationEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@EnableReactiveMethodSecurity
@EnableRSocketSecurity
@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {
    private final MdcFilter mdcFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    public SecurityConfiguration(ReactiveUserDetailsService reactiveUserDetailsService,
                                 MdcProperties mdcProperties,
                                 SecurityExceptionHandler securityExceptionHandler) {
        this.mdcFilter = new MdcFilter(mdcProperties);
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         CustomerJwtReactiveAuthenticationManager customerJwtReactiveAuthenticationManager) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> {
                    exchanges.pathMatchers(AuthController.AUTH_BASE_URL + "/**").permitAll();
                    exchanges.anyExchange().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.authenticationManager(customerJwtReactiveAuthenticationManager))
                        .authenticationEntryPoint(securityExceptionHandler)
                )
                .addFilterAt(mdcFilter, SecurityWebFiltersOrder.FIRST)
                .exceptionHandling(e -> e
                        .accessDeniedHandler(securityExceptionHandler)
                        .authenticationEntryPoint(securityExceptionHandler))
                .build();
    }


    @Bean
    PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket,
                                                        CustomerJwtReactiveAuthenticationManager reactiveAuthenticationManager) {
        return rsocket
                .authenticationManager(reactiveAuthenticationManager)
                .authorizePayload(authorize -> authorize
                        .setup().authenticated()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public RSocketStrategiesCustomizer rsocketStrategiesCustomizer() {
        return strategies -> {
            strategies.encoder(new BearerTokenAuthenticationEncoder());
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("UserDetailsRepositoryReactiveAuthenticationManager")
    public UserDetailsRepositoryReactiveAuthenticationManager getUserDetailsRepositoryReactiveAuthenticationManager(ReactiveUserDetailsServiceImpl reactiveUserDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager reactiveAuthenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        reactiveAuthenticationManager.setPasswordEncoder(passwordEncoder());
        return reactiveAuthenticationManager;
    }
}
