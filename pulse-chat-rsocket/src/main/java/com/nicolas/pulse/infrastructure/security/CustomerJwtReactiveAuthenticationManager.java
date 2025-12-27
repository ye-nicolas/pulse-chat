package com.nicolas.pulse.infrastructure.security;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nicolas.pulse.entity.domain.SecurityAccount.*;

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

    private Set<String> toStringSet(List<?> list) {
        return list.stream().map(i -> (String) i)
                .collect(Collectors.toSet());
    }

    private Set<Privilege> toPrivilegeSet(List<?> list) {
        return toStringSet(list).stream()
                .map(Privilege::valueOf)
                .collect(Collectors.toSet());
    }

    private Mono<Authentication> userDetailsToAuthentication(UserDetails userDetails) {
        return Mono.fromCallable(() -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}
