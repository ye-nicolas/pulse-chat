package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.mapper.AccountMapper;
import com.nicolas.pulse.adapter.dto.req.CreateAccountReq;
import com.nicolas.pulse.adapter.dto.req.LoginReq;
import com.nicolas.pulse.adapter.dto.res.AccountRes;
import com.nicolas.pulse.adapter.dto.res.AuthRes;
import com.nicolas.pulse.service.usecase.account.CreateAccountUseCase;
import com.nicolas.pulse.service.usecase.auth.LoginUseCase;
import com.nicolas.pulse.service.usecase.auth.RefreshTokenUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AuthController.BASE_URL)
public class AuthController {
    public static final String BASE_URL = "/auth";
    public static final String LOGIN_URL = "/login";
    public static final String REFRESH_URL = "/refresh";
    public static final String CREATE_ACCOUNT_URL = "/account";
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
        this.refreshTokenPath = basePath + BASE_URL + REFRESH_URL;
        this.refreshExpiresSeconds = refreshExpiresMinutes * 60;
        this.cookieSecure = cookieSecure;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.createAccountUseCase = createAccountUseCase;
    }

    @PostMapping(LOGIN_URL)
    public Mono<ResponseEntity<AuthRes>> login(@Valid @RequestBody Mono<LoginReq> reqMono) {
        LoginUseCase.Output output = new LoginUseCase.Output();
        return reqMono.map(req -> LoginUseCase.Input.builder()
                        .userName(req.getUserName())
                        .password(req.getPassword())
                        .build())
                .flatMap(input -> loginUseCase.execute(input, output))
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, getCookie(output.getRefreshToken()).toString())
                        .body(AuthRes.builder()
                                .accessToken(output.getAccessToken())
                                .accountId(output.getAccountId())
                                .build()))));
    }

    @PostMapping(REFRESH_URL)
    public Mono<ResponseEntity<AuthRes>> refresh(@CookieValue(REFRESH_TOKEN) String refreshToken) {
        RefreshTokenUseCase.Input input = new RefreshTokenUseCase.Input(refreshToken);
        RefreshTokenUseCase.Output output = new RefreshTokenUseCase.Output();
        return refreshTokenUseCase.execute(input, output)
                .then(Mono.fromSupplier(() -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, getCookie(output.getRefreshToken()).toString())
                        .body(AuthRes.builder()
                                .accessToken(output.getAccessToken())
                                .accountId(output.getAccountId())
                                .build())));
    }

    @PostMapping(CREATE_ACCOUNT_URL)
    public Mono<ResponseEntity<AccountRes>> createAccount(@Valid @RequestBody Mono<CreateAccountReq> reqMono) {
        CreateAccountUseCase.Output output = new CreateAccountUseCase.Output();
        return reqMono.map(req -> CreateAccountUseCase.Input.builder()
                        .name(req.getName())
                        .showName(req.getShowName())
                        .password(req.getPassword())
                        .remark(req.getRemark())
                        .build())
                .flatMap(input -> createAccountUseCase.execute(input, output))
                .then(Mono.fromSupplier(() -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(AccountMapper.domainToRes(output.getAccount()))));
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
