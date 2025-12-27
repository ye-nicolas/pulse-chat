package com.nicolas.pulse.util;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.Set;

public class SecurityUtil {
    public static Mono<String> getCurrentAccountId() {
        return getSecurityAccount()
                .map(SecurityAccount::getId);
    }

    public static Mono<SecurityAccount> getSecurityAccount() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(auth -> ((SecurityAccount) auth))
                .switchIfEmpty(Mono.error(() -> new BadCredentialsException("Unable to retrieve account details.")));
    }

    public static Mono<Set<String>> getCurrentRoomIdSet() {
        return getSecurityAccount()
                .map(SecurityAccount::getRoomIdSet)
                .switchIfEmpty(Mono.just(Set.of()));
    }
}
