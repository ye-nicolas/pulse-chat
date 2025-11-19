package com.nicolas.pulse.infrastructure.filter;

import com.nicolas.pulse.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

//@Component
//public class JwtAuthenticationWebFilter implements WebFilter {
//    private static final String AUTH_HEADER = "Bearer ";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER)) {
//            // 沒 token 就直接走下一個 filter
//            return chain.filter(exchange);
//        }
//
//        String token = authHeader.substring(AUTH_HEADER.length());
//
//        return JwtUtil.validateToken(token)
//                .flatMap(claims -> {
//                    List<SimpleGrantedAuthority> authorities = List.of();
//                    Authentication auth = new UsernamePasswordAuthenticationToken("AA", token, authorities);
//                    // 把 Authentication 放到 Reactor context，讓 downstream 可以用 SecurityContextReactive
//                    return chain.filter(exchange)
//                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
//                })
//                // token 無效或錯誤 → 繼續但不帶認證（也可以直接 return 401）
//                .switchIfEmpty(chain.filter(exchange));
//    }
//}
