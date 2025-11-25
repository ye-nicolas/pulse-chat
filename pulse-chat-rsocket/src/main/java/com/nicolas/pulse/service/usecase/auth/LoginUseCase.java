package com.nicolas.pulse.service.usecase.auth;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Service
public class LoginUseCase {
    private final long accessExpiresMills;
    private final long refreshExpiresMills;
    private final SecretKey secretKey;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    public LoginUseCase(@Value("${jwt.key}") String secret,
                        @Value("${auth.access.expires-minute}") Long accessExpiresMinute,
                        @Value("${auth.refresh.expires-minute}") Long refreshExpiresMinute,
                        ReactiveAuthenticationManager reactiveAuthenticationManager) {
        this.accessExpiresMills = accessExpiresMinute * 60  * 1_000;
        this.refreshExpiresMills = refreshExpiresMinute * 60 * 1_000;
        this.secretKey = JwtUtil.generateSecretKey(secret);
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
    }

    public Mono<Output> execute(Mono<Input> input) {
        return input.map(input1 -> new UsernamePasswordAuthenticationToken(input1.getUserName(), input1.getPassword()))
                .flatMap(reactiveAuthenticationManager::authenticate)
                .map(authentication -> (SecurityAccount) authentication.getPrincipal())
                .map(securityAccount -> {
                    String accessTokenId = UlidCreator.getMonotonicUlid().toString();
                    String refreshTokenId = UlidCreator.getMonotonicUlid().toString();
                    return Output.builder()
                            .accountId(securityAccount.getId())
                            .accessTokenId(accessTokenId)
                            .refreshTokenId(refreshTokenId)
                            .accessToken(JwtUtil.generateAccessToken(secretKey, accessTokenId, securityAccount.getId(), accessExpiresMills, securityAccount.toMap()))
                            .refreshToken(JwtUtil.generateRefreshToken(secretKey, refreshTokenId, securityAccount.getId(), refreshExpiresMills))
                            .build();
                });
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
    public static class Output {
        private String accountId;
        private String accessTokenId;
        private String refreshTokenId;
        private String accessToken;
        private String refreshToken;
    }
}
