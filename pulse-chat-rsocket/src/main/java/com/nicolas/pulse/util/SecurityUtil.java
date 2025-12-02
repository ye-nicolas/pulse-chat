package com.nicolas.pulse.util;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

public class SecurityUtil {
    public static final String ROOT_ID = "01KBAC2JMNN7R6YJFRDNKFFHVA";

    public static Mono<String> getCurrentAccountId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(auth -> ((SecurityAccount) auth).getId())
                .switchIfEmpty(Mono.just(ROOT_ID));
    }
}
