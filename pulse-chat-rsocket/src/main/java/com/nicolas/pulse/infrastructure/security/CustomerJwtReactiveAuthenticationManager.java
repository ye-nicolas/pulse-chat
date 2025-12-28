package com.nicolas.pulse.infrastructure.security;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

import static com.nicolas.pulse.entity.domain.SecurityAccount.USER_NAME;

@Slf4j
@Primary
@Component
public class CustomerJwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final SecretKey secretKey;

    public CustomerJwtReactiveAuthenticationManager(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter((a) -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap(this::validateAccessToken)
                .flatMap(this::claimsToUserDetails)
                .flatMap(this::userDetailsToAuthentication)
                .onErrorMap(ex -> new BadCredentialsException(ex.getMessage(), ex));
    }

    private Mono<Claims> validateAccessToken(String token) {
        return Mono.fromCallable(() -> JwtUtil.validateAccessToken(secretKey, token));
    }

    private Mono<SecurityAccount> claimsToUserDetails(Claims claims) {
        return Mono.fromCallable(() -> SecurityAccount.builder()
                .id(claims.getSubject())
                .username(claims.get(USER_NAME, String.class))
                .state(true)
                .build());
    }

    private Mono<Authentication> userDetailsToAuthentication(UserDetails userDetails) {
        return Mono.fromCallable(() -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}
