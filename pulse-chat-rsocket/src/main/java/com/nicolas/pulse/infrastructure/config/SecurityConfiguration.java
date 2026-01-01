package com.nicolas.pulse.infrastructure.config;

import com.nicolas.pulse.adapter.controller.AuthController;
import com.nicolas.pulse.infrastructure.filter.MdcFilter;
import com.nicolas.pulse.infrastructure.security.CustomerJwtReactiveAuthenticationManager;
import com.nicolas.pulse.infrastructure.security.SecurityExceptionHandler;
import com.nicolas.pulse.service.usecase.account.ReactiveUserDetailsServiceImpl;
import com.nicolas.pulse.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import javax.crypto.SecretKey;

@EnableReactiveMethodSecurity
@EnableRSocketSecurity
@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {
    private final MdcFilter mdcFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    public SecurityConfiguration(MdcProperties mdcProperties,
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
                    exchanges.pathMatchers(AuthController.BASE_URL + "/**").permitAll();
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
                .authorizePayload(authorize -> authorize
                        .setup().permitAll()
                        .anyRequest().authenticated())
                .jwt(j -> j.authenticationManager(reactiveAuthenticationManager))
                .build();
    }

    @Bean
    SecretKey get(@Value("${jwt.key}") String secret) {
        return JwtUtil.generateSecretKey(secret);
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
