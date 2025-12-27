package com.nicolas.pulse.util;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

public class SecurityUtil {
    public static Mono<String> getCurrentAccountId() {
        return getBasicSecurityAccount()
                .switchIfEmpty(Mono.error(() -> new BadCredentialsException("Unable to retrieve account details.")))
                .map(SecurityAccount::getId);
    }

    public static Mono<SecurityAccount> getBasicSecurityAccount() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(auth -> ((SecurityAccount) auth));

    }
}
