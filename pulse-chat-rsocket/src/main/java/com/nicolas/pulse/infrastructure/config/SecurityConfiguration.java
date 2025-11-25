package com.nicolas.pulse.infrastructure.config;

import com.nicolas.pulse.infrastructure.filter.JwtAuthenticationWebFilter;
import com.nicolas.pulse.infrastructure.filter.MdcFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {
    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
    private final MdcFilter mdcFilter;

    public SecurityConfiguration(ReactiveUserDetailsService reactiveUserDetailsService,
                                 JwtAuthenticationWebFilter jwtAuthenticationWebFilter,
                                 MdcFilter mdcFilter) {
        this.reactiveUserDetailsService = reactiveUserDetailsService;
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
        this.mdcFilter = mdcFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         @Qualifier("UserDetailsRepositoryReactiveAuthenticationManager") ReactiveAuthenticationManager authenticationManager) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .addFilterBefore(mdcFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterBefore(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(e -> e
                        .accessDeniedHandler((webExchange, accessDeniedException) -> Mono.error(accessDeniedException))
                        .authenticationEntryPoint((webExchange, authenticationException) -> Mono.error(authenticationException)))
                .build();
    }

    @Bean("UserDetailsRepositoryReactiveAuthenticationManager")
    public ReactiveAuthenticationManager getUserDetailsRepositoryReactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
