package com.nicolas.pulse.service.usecase.auth;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.AccountRoleRepository;
import com.nicolas.pulse.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.stream.Collectors;

@Service
public class RefreshTokenUseCase {
    private final long accessExpiresMills;
    private final long refreshExpiresMills;
    private final SecretKey secretKey;
    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;

    public RefreshTokenUseCase(@Value("${jwt.key}") String secret,
                               @Value("${auth.access.expires-minute}") Long accessExpiresMinute,
                               @Value("${auth.refresh.expires-minute}") Long refreshExpiresMinute,
                               AccountRepository accountRepository,
                               AccountRoleRepository accountRoleRepository) {
        this.accessExpiresMills = accessExpiresMinute * 60 * 1_000;
        this.refreshExpiresMills = refreshExpiresMinute * 60 * 1_000;
        this.secretKey = JwtUtil.generateSecretKey(secret);
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
    }

    public Mono<Void> execute(Input input, Output output) {
        return Mono.fromCallable(() -> JwtUtil.validateRefreshToken(secretKey, input.getRefreshToken()))
                .flatMap(claims -> accountRepository.findById(claims.getSubject()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Not Found Account")))
                .flatMap(account -> accountRoleRepository.findAllByAccountId(account.getId())
                        .flatMap(accountRole -> Flux.fromIterable(accountRole.getRole().getPrivilegeSet()))
                        .collect(Collectors.toSet())
                        .zipWith(Mono.just(account)))
                .map(tuple2 -> SecurityAccount.builder()
                        .id(tuple2.getT2().getId())
                        .username(tuple2.getT2().getName())
                        .password(tuple2.getT2().getPassword())
                        .state(tuple2.getT2().isActive())
                        .privilegeSet(tuple2.getT1())
                        .build())
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

