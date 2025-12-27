package com.nicolas.pulse.service.usecase.auth;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.service.usecase.account.ReactiveUserDetailsServiceImpl;
import com.nicolas.pulse.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Service
public class RefreshTokenUseCase {
    private final long accessExpiresMills;
    private final long refreshExpiresMills;
    private final SecretKey secretKey;
    private final ReactiveUserDetailsServiceImpl reactiveUserDetailsService;

    public RefreshTokenUseCase(@Value("${jwt.key}") String secret,
                               @Value("${auth.access.expires-minute}") Long accessExpiresMinute,
                               @Value("${auth.refresh.expires-minute}") Long refreshExpiresMinute,
                               ReactiveUserDetailsServiceImpl reactiveUserDetailsService) {
        this.accessExpiresMills = accessExpiresMinute * 60 * 1_000;
        this.refreshExpiresMills = refreshExpiresMinute * 60 * 1_000;
        this.secretKey = JwtUtil.generateSecretKey(secret);
        this.reactiveUserDetailsService = reactiveUserDetailsService;
    }

    public Mono<Void> execute(Input input, Output output) {
        return Mono.fromCallable(() -> JwtUtil.validateRefreshToken(secretKey, input.getRefreshToken()))
                .flatMap(claims -> reactiveUserDetailsService.findById(claims.getSubject()))
                .doOnNext(securityAccount -> {
                    String accessTokenId = UlidCreator.getMonotonicUlid().toString();
                    String refreshTokenId = UlidCreator.getMonotonicUlid().toString();
                    output.setAccountId(securityAccount.getUsername());
                    output.setAccessTokenId(accessTokenId);
                    output.setRefreshTokenId(refreshTokenId);
                    output.setAccessToken(JwtUtil.generateAccessToken(secretKey, accessTokenId, securityAccount.getId(), accessExpiresMills, securityAccount.toMap()));
                    output.setRefreshToken(JwtUtil.generateRefreshToken(secretKey, refreshTokenId, securityAccount.getId(), refreshExpiresMills));
                })
                .then();
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String refreshToken;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private String accountId;
        private String accessTokenId;
        private String refreshTokenId;
        private String accessToken;
        private String refreshToken;
    }
}

