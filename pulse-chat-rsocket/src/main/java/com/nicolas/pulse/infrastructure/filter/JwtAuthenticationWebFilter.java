package com.nicolas.pulse.infrastructure.filter;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nicolas.pulse.entity.domain.SecurityAccount.*;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {
    private static final String AUTH_HEADER = "Bearer ";
    private final SecretKey secretKey;

    public JwtAuthenticationWebFilter(@Value("${jwt.key}") String secretKey) {
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
                .flatMap(this::claimsToUserDetails)
                .flatMap(this::userDetailstoAuthentication)
                .onErrorMap(e -> new BadCredentialsException(e.getMessage(), e)) // 將validateAccessToken\processClaims\toAuthentication的錯誤轉換成BadCredentialsException
                .flatMap(auth -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)));
    }

    private Mono<Claims> validateAccessToken(String token) {
        return Mono.fromCallable(() -> JwtUtil.validateAccessToken(secretKey, token));
    }

    private Mono<SecurityAccount> claimsToUserDetails(Claims claims) {

        return Mono.fromCallable(() -> SecurityAccount.builder()
                .id(claims.getSubject())
                .username(claims.get(USER_NAME, String.class))
                .privilegeSet(toPrivilegeSet(claims.get(PRIVILEGE, List.class)))
                .roomIdSet(toStringSet(claims.get(ROOM, List.class)))
                .state(true)
                .build());
    }

    private Set<String> toStringSet(List<?> list) {
        return list.stream().map(i -> (String) i)
                .collect(Collectors.toSet());
    }

    private Set<Privilege> toPrivilegeSet(List<?> list) {
        return toStringSet(list).stream()
                .map(Privilege::valueOf)
                .collect(Collectors.toSet());
    }

    private Mono<UsernamePasswordAuthenticationToken> userDetailstoAuthentication(UserDetails userDetails) {
        return Mono.fromCallable(() -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}
