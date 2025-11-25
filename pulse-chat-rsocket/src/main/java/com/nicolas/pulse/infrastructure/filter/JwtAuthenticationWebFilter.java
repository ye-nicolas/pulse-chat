package com.nicolas.pulse.infrastructure.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import java.util.Set;

import static com.nicolas.pulse.entity.domain.SecurityAccount.PRIVILEGE;
import static com.nicolas.pulse.entity.domain.SecurityAccount.USER_NAME;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {
    private static final String AUTH_HEADER = "Bearer ";

    private final ObjectMapper objectMapper;
    private final SecretKey secretKey;

    public JwtAuthenticationWebFilter(ObjectMapper objectMapper,
                                      @Value("${jwt.key}") String secretKey) {
        this.objectMapper = objectMapper;
        this.secretKey = JwtUtil.generateSecretKey(secretKey);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 輕量處理
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER)) {
            return chain.filter(exchange);
        }
        String token = authHeader.substring(AUTH_HEADER.length());
        return this.validateAccessToken(token)
                .flatMap(this::processClaims)
                .flatMap(this::toAuthentication)
                .flatMap(auth -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                .onErrorMap(e -> new BadCredentialsException(e.getMessage(), e));
    }

    private Mono<Claims> validateAccessToken(String token) {
        // Mono.fromCallable：包裝一個同步的、可能阻塞的程式碼 或 可能拋出異常，並且您希望將這個異常優雅地在反應式流中處理
        return Mono.fromCallable(() -> JwtUtil.validateAccessToken(secretKey, token));
    }

    private Mono<SecurityAccount> processClaims(Claims claims) {
        return Mono.fromCallable(() -> SecurityAccount.builder()
                .id(claims.getSubject())
                .username(claims.get(USER_NAME, String.class))
                .privilegeSet(objectMapper.readValue(claims.get(PRIVILEGE, String.class), new TypeReference<Set<Privilege>>() {
                }))
                .state(true)
                .build());
    }

    private Mono<UsernamePasswordAuthenticationToken> toAuthentication(UserDetails userDetails) {
        return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}
