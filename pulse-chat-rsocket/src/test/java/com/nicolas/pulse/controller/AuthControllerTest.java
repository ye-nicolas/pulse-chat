package com.nicolas.pulse.controller;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.AbstractIntegrationTest;
import com.nicolas.pulse.adapter.controller.AuthController;
import com.nicolas.pulse.adapter.dto.req.CreateAccountReq;
import com.nicolas.pulse.adapter.dto.req.LoginReq;
import com.nicolas.pulse.adapter.dto.res.AccountRes;
import com.nicolas.pulse.adapter.dto.res.AuthRes;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static com.nicolas.pulse.adapter.controller.AuthController.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerTest extends AbstractIntegrationTest {
    @Value("${spring.webflux.base-path}")
    private String basePath;
    @Value("${auth.refresh.expires-minute}")
    private Long refreshExpiresMinutes;
    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Test
    void login_success() {
        // Arrange
        LoginReq loginReq = LoginReq.builder()
                .userName(ACCOUNT_DATA_1.getName())
                .password(ACCOUNT_DATA_1.getName())
                .build();

        // Act
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(BASE_URL + AuthController.LOGIN_URL)
                .bodyValue(loginReq)
                .exchange();

        // Assert
        response.expectStatus()
                .isOk()
                .expectHeader().valueMatches(HttpHeaders.SET_COOKIE, getValidateCookieString())
                .expectBody(AuthRes.class)
                .consumeWith(result -> {
                    AuthRes accountRes = result.getResponseBody();
                    assertThat(accountRes).isNotNull();
                    assertThat(accountRes.getAccountId()).isEqualTo(ACCOUNT_DATA_1.getId());
                    assertThat(accountRes.getAccessToken().isBlank()).isFalse();
                });
    }

    private String getValidateCookieString() {
        return "%s=[^;]+; ".formatted(REFRESH_TOKEN) +
                "Path=" + basePath + BASE_URL + REFRESH_URL + "; " +
                "Max-Age=" + Duration.ofMinutes(refreshExpiresMinutes).toSeconds() + "; " +
                "Expires=[^;]+; " +
                "HttpOnly; " +
                "SameSite=Lax";
    }

    @Test
    void login_whenNameNoExists_shouldReturn401() {
        // Arrange
        LoginReq loginReq = LoginReq.builder()
                .userName(UlidCreator.getMonotonicUlid().toString())
                .password(ACCOUNT_DATA_1.getName())
                .build();

        // Act
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(BASE_URL + AuthController.LOGIN_URL)
                .bodyValue(loginReq)
                .exchange();

        // Assert
        response.expectStatus()
                .isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.UNAUTHORIZED);
                });
    }

    @Test
    void login_whenPasswordNoMatch_shouldReturn401() {
        // Arrange
        LoginReq loginReq = LoginReq.builder()
                .userName(ACCOUNT_DATA_1.getName())
                .password(UlidCreator.getMonotonicUlid().toString())
                .build();

        // Act
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(BASE_URL + AuthController.LOGIN_URL)
                .bodyValue(loginReq)
                .exchange();

        // Assert
        response.expectStatus()
                .isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.UNAUTHORIZED);
                });
    }

    @Test
    void refresh_success() {
        // Arrange
        EntityExchangeResult<AuthRes> loginResult = webTestClient.post()
                .uri(BASE_URL + AuthController.LOGIN_URL) // 這裡根據你測試環境的實際情況決定是否加 basePath
                .bodyValue(new LoginReq(ACCOUNT_DATA_1.getName(), ACCOUNT_DATA_1.getName()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthRes.class)
                .returnResult();
        // Act
        String setCookieHeader = loginResult.getResponseHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookieHeader).isNotNull();
        assertThat(setCookieHeader).contains("%s=".formatted(REFRESH_TOKEN));

        // Assert
        webTestClient.post()
                .uri(BASE_URL + REFRESH_URL) // /pulse-chat/auth/refresh
                .cookie(REFRESH_TOKEN, setCookieHeader.split(";")[0].split("=")[1])
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .valueMatches(HttpHeaders.SET_COOKIE, getValidateCookieString())
                .expectBody(AuthRes.class)
                .consumeWith(result -> {
                    AuthRes accountRes = result.getResponseBody();
                    assertThat(accountRes).isNotNull();
                    assertThat(accountRes.getAccountId()).isEqualTo(ACCOUNT_DATA_1.getId());
                    assertThat(accountRes.getAccessToken().isBlank()).isFalse();
                });

    }

    @Test
    void createAccount_success() {
        // Arrange
        CreateAccountReq accountReq = CreateAccountReq.builder()
                .name("n_name")
                .showName("n_showName")
                .password(UlidCreator.getMonotonicUlid().toString())
                .build();

        // Act
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(BASE_URL + AuthController.CREATE_ACCOUNT_URL)
                .bodyValue(accountReq)
                .exchange();

        // Assert
        response.expectStatus()
                .isCreated()
                .expectBody(AccountRes.class)
                .consumeWith(result -> {
                    AccountRes accountRes = result.getResponseBody();
                    assertThat(accountRes).isNotNull();
                    assertThat(accountRes.getName()).isEqualTo(accountReq.getName());
                    assertThat(accountRes.getShowName()).isEqualTo(accountReq.getShowName());
                    assertThat(accountRes.isActive()).isTrue();
                    assertThat(accountRes.getCreatedBy()).isEqualTo(accountRes.getUpdatedBy());
                    assertThat(accountRes.getCreatedAt()).isEqualTo(accountRes.getUpdatedAt());
                });
    }

    @Test
    void createAccount_whenNameExists_shouldReturn409Conflict() {
        // Arrange
        String name = ACCOUNT_DATA_1.getName();
        CreateAccountReq accountReq = CreateAccountReq.builder()
                .name(name)
                .showName("n_showName")
                .password(UlidCreator.getMonotonicUlid().toString())
                .build();

        // Act
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(BASE_URL + AuthController.CREATE_ACCOUNT_URL)
                .bodyValue(accountReq)
                .exchange();

        // Assert
        response.expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.RESOURCE_CONFLICT);
                    assertThat(responseBody.getDetail()).isEqualTo("User name already exists, name = '%s'.".formatted(name));
                });
    }

    @Override
    protected String provideSpecificSql() {
        return "";
    }
}
