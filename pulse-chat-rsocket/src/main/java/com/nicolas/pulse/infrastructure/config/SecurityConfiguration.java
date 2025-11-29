package com.nicolas.pulse.infrastructure.config;

import com.nicolas.pulse.infrastructure.filter.JwtAuthenticationWebFilter;
import com.nicolas.pulse.infrastructure.filter.MdcFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@EnableReactiveMethodSecurity
@EnableRSocketSecurity
@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {
    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
    private final MdcFilter mdcFilter;
    private final String jwtKey;

    public SecurityConfiguration(ReactiveUserDetailsService reactiveUserDetailsService,
                                 JwtAuthenticationWebFilter jwtAuthenticationWebFilter,
                                 MdcProperties mdcProperties,
                                 @Value("${jwt.key}") String jwtKey) {
        this.reactiveUserDetailsService = reactiveUserDetailsService;
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
        this.mdcFilter = new MdcFilter(mdcProperties);
        this.jwtKey = jwtKey;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         @Qualifier("UserDetailsRepositoryReactiveAuthenticationManager") ReactiveAuthenticationManager authenticationManager) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> {
//                    exchanges.pathMatchers(AuthController.AUTH_BASE_URL).permitAll();
                    exchanges.anyExchange().permitAll();
                })
                .addFilterAt(mdcFilter, SecurityWebFiltersOrder.FIRST)
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
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

    @Bean
    PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket) {
        return rsocket
                .authorizePayload(authorize -> authorize
                        .setup().authenticated()
                        .anyRequest().authenticated()
                ).jwt(Customizer.withDefaults())
                .build();
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(
                jwtKey.getBytes(StandardCharsets.UTF_8),
                "HmacSha256"
        );
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }
}
