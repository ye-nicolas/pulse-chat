package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.req.CreateAccountReq;
import com.nicolas.pulse.adapter.dto.req.LoginReq;
import com.nicolas.pulse.adapter.dto.response.AuthRes;
import com.nicolas.pulse.service.usecase.account.CreateAccountUseCase;
import com.nicolas.pulse.service.usecase.auth.LoginUseCase;
import com.nicolas.pulse.service.usecase.auth.RefreshTokenUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AuthController.AUTH_BASE_URL)
public class AuthController {
    public static final String AUTH_BASE_URL = "/auth";
    public static final String REFRESH_URL = "/refresh";
    public static final String REFRESH_TOKEN = "refresh_token";
    private final String refreshTokenPath;
    private final Long refreshExpiresSeconds;
    private final boolean cookieSecure;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final CreateAccountUseCase createAccountUseCase;

    public AuthController(
            @Value("${spring.webflux.base-path}") String basePath,
            @Value("${auth.refresh.expires-minute}") Long refreshExpiresMinutes,
            @Value("${cookie.secure}") boolean cookieSecure,
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            CreateAccountUseCase createAccountUseCase) {
        this.refreshTokenPath = basePath + AUTH_BASE_URL + REFRESH_URL;
        this.refreshExpiresSeconds = refreshExpiresMinutes * 60;
        this.cookieSecure = cookieSecure;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.createAccountUseCase = createAccountUseCase;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthRes>> login(@Valid @RequestBody Mono<LoginReq> req) {
        return req.map(r -> LoginUseCase.Input.builder()
                        .userName(r.getUserName())
                        .password(r.getPassword())
                        .build())
                .transform(loginUseCase::execute)
                .map(output -> {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, getCookie(output.getRefreshToken()).toString())
                            .body(AuthRes.builder()
                                    .accessToken(output.getAccessToken())
                                    .accountId(output.getAccountId())
                                    .build());
                });
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthRes>> refresh(@CookieValue(REFRESH_TOKEN) String refreshToken) {
        RefreshTokenUseCase.Input input = new RefreshTokenUseCase.Input(refreshToken);
        RefreshTokenUseCase.Output output = new RefreshTokenUseCase.Output();
        return refreshTokenUseCase.execute(input, output)
                .then(Mono.just(ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, getCookie(output.getRefreshToken()).toString())
                        .body(AuthRes.builder()
                                .accessToken(output.getAccessToken())
                                .accountId(output.getAccountId())
                                .build())));
    }

    @PostMapping("/account")
    public Mono<ResponseEntity<String>> createAccount(@Valid @RequestBody Mono<CreateAccountReq> req) {
        return req.map(r -> CreateAccountUseCase.Input.builder()
                        .name(r.getName())
                        .showName(r.getShowName())
                        .password(r.getPassword())
                        .remark(r.getRemark())
                        .roleIdSet(r.getRoleIdSet())
                        .build())
                .transform(createAccountUseCase::execute)
                .map(output -> ResponseEntity.ok().body(output.getUserId()));
    }

    private ResponseCookie getCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(refreshTokenPath)// refresh的路徑
                .maxAge(refreshExpiresSeconds)
                .build();
    }
}
