package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.req.LoginReq;
import com.nicolas.pulse.adapter.dto.response.LoginRes;
import com.nicolas.pulse.service.usecase.auth.LoginUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    public AuthController(
            @Value("${spring.webflux.base-path}") String basePath,
            @Value("${auth.refresh.expires-minute}") Long refreshExpiresMinutes,
            @Value("${cookie.secure}") boolean cookieSecure,
            LoginUseCase loginUseCase) {
        this.refreshTokenPath = basePath + AUTH_BASE_URL + REFRESH_URL;
        this.refreshExpiresSeconds = refreshExpiresMinutes * 60;
        this.cookieSecure = cookieSecure;
        this.loginUseCase = loginUseCase;

    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginRes>> login(@Valid @RequestBody Mono<LoginReq> req) {
        return req.map(r -> LoginUseCase.Input.builder()
                        .userName(r.getUserName())
                        .password(r.getPassword())
                        .build())
                .transform(loginUseCase::execute)
                .map(output -> {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, getCookie(output.getRefreshToken()).toString())
                            .body(LoginRes.builder()
                                    .accessToken(output.getAccessToken())
                                    .accountId(output.getAccountId())
                                    .build());
                });
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
