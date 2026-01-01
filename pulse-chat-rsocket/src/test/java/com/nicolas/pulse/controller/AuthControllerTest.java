package com.nicolas.pulse.controller;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.AbstractIntegrationTest;
import com.nicolas.pulse.adapter.controller.AuthController;
import com.nicolas.pulse.adapter.dto.req.CreateAccountReq;
import com.nicolas.pulse.adapter.dto.res.AccountRes;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerTest extends AbstractIntegrationTest {

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
                .uri(AuthController.BASE_URL + AuthController.CREATE_ACCOUNT_URL)
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
                .uri(AuthController.BASE_URL + AuthController.CREATE_ACCOUNT_URL)
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
}
