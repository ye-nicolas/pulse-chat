package com.nicolas.pulse.service.usecase.auth;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Service
public class LoginUseCase {
    private final long accessExpiresMills;
    private final long refreshExpiresMills;
    private final SecretKey secretKey;
    private final UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager;

    public LoginUseCase(@Value("${jwt.key}") String secret,
                        @Value("${auth.access.expires-minute}") Long accessExpiresMinute,
                        @Value("${auth.refresh.expires-minute}") Long refreshExpiresMinute,
                        @Qualifier("UserDetailsRepositoryReactiveAuthenticationManager") UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager) {
        this.accessExpiresMills = accessExpiresMinute * 60 * 1_000;
        this.refreshExpiresMills = refreshExpiresMinute * 60 * 1_000;
        this.secretKey = JwtUtil.generateSecretKey(secret);
        this.userDetailsRepositoryReactiveAuthenticationManager = userDetailsRepositoryReactiveAuthenticationManager;
    }

    public Mono<Void> execute(Input input, Output output) {
        return userDetailsRepositoryReactiveAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getUserName(), input.getPassword()))
                .map(authentication -> (SecurityAccount) authentication.getPrincipal())
                .doOnSuccess(securityAccount -> {
                    String accessTokenId = UlidCreator.getMonotonicUlid().toString();
                    String refreshTokenId = UlidCreator.getMonotonicUlid().toString();
                    output.setAccountId(securityAccount.getId());
                    output.setAccessTokenId(accessTokenId);
                    output.setRefreshTokenId(refreshTokenId);
                    output.setAccessToken(JwtUtil.generateAccessToken(secretKey, accessTokenId, securityAccount.getId(), accessExpiresMills, securityAccount.toMap()));
                    output.setRefreshToken(JwtUtil.generateRefreshToken(secretKey, refreshTokenId, securityAccount.getId(), refreshExpiresMills));
                })
                .then();
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String userName;
        private String password;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output {
        private String accountId;
        private String accessTokenId;
        private String refreshTokenId;
        private String accessToken;
        private String refreshToken;
    }
}
