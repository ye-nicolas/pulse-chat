package com.nicolas.pulse.controller;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.AbstractIntegrationTest;
import com.nicolas.pulse.adapter.controller.AccountController;
import com.nicolas.pulse.adapter.dto.mapper.AccountMapper;
import com.nicolas.pulse.adapter.dto.res.AccountRes;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountControllerTest extends AbstractIntegrationTest {

    @Test
    void findAccountById_success() {
        // Arrange
        AccountRes exceptRes = AccountMapper.domainToRes(ACCOUNT_DATA_1);

        // Act
        WebTestClient.ResponseSpec response = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS))
                .get()
                .uri(AccountController.BASE_URL + "/%s".formatted(exceptRes.getId()))
                .exchange();

        // Assert
        response.expectStatus()
                .isOk()
                .expectBody(AccountRes.class)
                .consumeWith(result -> {
                    AccountRes accountRes = result.getResponseBody();
                    assertThat(accountRes).isNotNull();
                    assertThat(accountRes).usingRecursiveComparison().isEqualTo(exceptRes);
                });
    }

    @Test
    void findAccountById_whenIdNotExists_shouldReturn404NotFound() {
        // Arrange
        String id = UlidCreator.getMonotonicUlid().toString();

        // Act
        WebTestClient.ResponseSpec response = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS))
                .get()
                .uri(AccountController.BASE_URL + "/%s".formatted(id))
                .exchange();

        // Assert
        response.expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Account not found, id = '%s'.".formatted(id));
                });
    }

    @Test
    void findAccountById_whenAccessingOtherAccount_shouldReturn403() {
        // Arrange
        String id = ACCOUNT_DATA_2.getId();

        // Act
        WebTestClient.ResponseSpec response = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS))
                .get()
                .uri(AccountController.BASE_URL + "/%s".formatted(id))
                .exchange();

        // Assert
        response.expectStatus()
                .isEqualTo(HttpStatus.FORBIDDEN)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.FORBIDDEN);
                    assertThat(responseBody.getDetail()).isEqualTo("Cant get other account.");
                });
    }
}