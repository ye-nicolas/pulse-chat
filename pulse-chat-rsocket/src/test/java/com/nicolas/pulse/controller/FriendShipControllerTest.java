package com.nicolas.pulse.controller;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.AbstractIntegrationTest;
import com.nicolas.pulse.adapter.controller.FriendShipController;
import com.nicolas.pulse.adapter.dto.mapper.AccountMapper;
import com.nicolas.pulse.adapter.dto.mapper.FriendShipMapper;
import com.nicolas.pulse.adapter.dto.req.CreateFriendShipReq;
import com.nicolas.pulse.adapter.dto.res.FriendShipRes;
import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.entity.domain.FriendShip;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendShipControllerTest extends AbstractIntegrationTest {
    static final FriendShip PENDING = FriendShip.builder()
            .id(UlidCreator.getMonotonicUlid().toString())
            .requesterAccount(ACCOUNT_DATA_1)
            .recipientAccount(ACCOUNT_DATA_2)
            .status(FriendShipStatus.PENDING)
            .createdBy(ACCOUNT_DATA_1.getId())
            .updatedBy(ACCOUNT_DATA_1.getId())
            .createdAt(instant)
            .updatedAt(instant)
            .build();
    static final FriendShip ACCEPTED = FriendShip.builder()
            .id(UlidCreator.getMonotonicUlid().toString())
            .requesterAccount(ACCOUNT_DATA_1)
            .recipientAccount(ACCOUNT_DATA_3)
            .status(FriendShipStatus.ACCEPTED)
            .createdBy(ACCOUNT_DATA_1.getId())
            .updatedBy(ACCOUNT_DATA_3.getId())
            .createdAt(instant)
            .updatedAt(instant)
            .build();
    static final List<FriendShip> FRIEND_SHIPS = List.of(PENDING, ACCEPTED);
    private static final String FRIEND_SHIP_SQL = """
            INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s)
            VALUES %s;
            """.formatted(DbMeta.FriendShipData.TABLE_NAME,
            DbMeta.FriendShipData.COL_ID, DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID, DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID, DbMeta.FriendShipData.COL_STATUS,
            DbMeta.FriendShipData.COL_CREATED_BY, DbMeta.FriendShipData.COL_UPDATED_BY, DbMeta.FriendShipData.COL_CREATED_AT, DbMeta.FriendShipData.COL_UPDATED_AT,
            FRIEND_SHIPS.stream()
                    .map(friendShip -> "('%s','%s','%s','%s','%s','%s','%s','%s')".formatted(
                            friendShip.getId(), friendShip.getRequesterAccount().getId(), friendShip.getRecipientAccount().getId(), friendShip.getStatus(),
                            friendShip.getCreatedBy(), friendShip.getUpdatedBy(), friendShip.getCreatedAt(), friendShip.getUpdatedAt()))
                    .collect(Collectors.joining(",\n")));

    @BeforeEach
    void setUp() {
        databaseClient.sql(FRIEND_SHIP_SQL)
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    void findAllByAccount_success() {
        // Arrange
        List<FriendShipRes> except = FRIEND_SHIPS.stream().map(FriendShipMapper::domainToRes).toList();

        // Act
        WebTestClient.ResponseSpec exchange = getFriendShip();

        // Assert
        exchange.expectStatus()
                .isOk()
                .expectBodyList(FriendShipRes.class)
                .hasSize(except.size())
                .consumeWith(result -> {
                    List<FriendShipRes> friends = result.getResponseBody();
                    assertThat(friends).isNotNull();
                    assertThat(friends).usingRecursiveComparison()
                            .ignoringCollectionOrder()
                            .isEqualTo(except);
                });

    }

    private WebTestClient.ResponseSpec getFriendShip() {
        return webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .get()
                .uri(FriendShipController.FRIEND_SHIP_BASE_URL + "/")
                .exchange();
    }

    @Test
    void createFriendShip_success() {
        // Arrange
        CreateFriendShipReq req = new CreateFriendShipReq(ACCOUNT_DATA_4.getId());
        FriendShipRes except = FriendShipMapper.domainToRes(FriendShip.builder()
                .requesterAccount(ACCOUNT_DATA_1)
                .recipientAccount(ACCOUNT_DATA_4)
                .status(FriendShipStatus.PENDING)
                .createdBy(ACCOUNT_DATA_1.getId())
                .updatedBy(ACCOUNT_DATA_1.getId())
                .build());

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .post()
                .uri(FriendShipController.FRIEND_SHIP_BASE_URL + "/")
                .bodyValue(req)
                .exchange();

        // Assert
        String friendShipId = exchange.expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        getFriendShip().expectStatus()
                .isOk()
                .expectBodyList(FriendShipRes.class)
                .hasSize(FRIEND_SHIPS.size() + 1)
                .consumeWith(result -> {
                    List<FriendShipRes> friends = result.getResponseBody();
                    assertThat(friends).isNotNull();
                    assertThat(friends)
                            .filteredOn(friend -> friend.getId().equals(friendShipId))
                            .hasSize(1)
                            .first()
                            .usingRecursiveComparison()
                            .ignoringFields("id")
                            .ignoringFieldsOfTypes(Instant.class)
                            .isEqualTo(except);
                });
    }

    @Test
    void createFriendShip_requesterAndRecipientSame_shouldReturn409() {
        // Arrange
        CreateFriendShipReq req = new CreateFriendShipReq(ACCOUNT_DATA_1.getId());

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .post()
                .uri(FriendShipController.FRIEND_SHIP_BASE_URL + "/")
                .bodyValue(req)
                .exchange();

        // Assert
        exchange.expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.RESOURCE_CONFLICT);
                    assertThat(responseBody.getDetail()).isEqualTo("Requester and Recipient are same.");
                });
    }

    @Test
    void createFriendShip_requesterAndRecipientExists_shouldReturn409() {
        // Arrange
        CreateFriendShipReq req = new CreateFriendShipReq(PENDING.getRecipientAccount().getId());

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .post()
                .uri(FriendShipController.FRIEND_SHIP_BASE_URL + "/")
                .bodyValue(req)
                .exchange();

        // Assert
        exchange.expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.RESOURCE_CONFLICT);
                    assertThat(responseBody.getDetail()).isEqualTo("Friend ship already exists, requesterAccountId = '%s' and recipientAccountId = '%s'.".formatted(USER_DETAILS_ACCOUNT_1.getId(), PENDING.getRecipientAccount().getId()));
                });
    }

    @Test
    void updateFriendShipStatusToAccepted_success() {
        // Arrange
        FriendShipRes except = FriendShipRes.builder()
                .id(PENDING.getId())
                .requesterAccount(AccountMapper.domainToRes(PENDING.getRequesterAccount()))
                .recipientAccount(AccountMapper.domainToRes(PENDING.getRecipientAccount()))
                .status(FriendShipStatus.ACCEPTED)
                .createdBy(PENDING.getRequesterAccount().getId())
                .updatedBy(PENDING.getRecipientAccount().getId())
                .build();

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_2))
                .patch()
                .uri(FriendShipController.FRIEND_SHIP_BASE_URL + "/" + except.getId())
                .exchange();

        // Assert
        exchange.expectStatus()
                .isOk();

        getFriendShip().expectStatus()
                .isOk()
                .expectBodyList(FriendShipRes.class)
                .consumeWith(result -> {
                    List<FriendShipRes> friends = result.getResponseBody();
                    assertThat(friends).isNotNull();
                    assertThat(friends)
                            .filteredOn(friend -> friend.getId().equals(except.getId()))
                            .hasSize(1)
                            .first()
                            .usingRecursiveComparison()
                            .ignoringFieldsOfTypes(Instant.class)
                            .isEqualTo(except);
                });
    }

    @Test
    void updateFriendShipStatusToAccepted_notFound_shouldReturn404() {
        // Arrange
        String id = UlidCreator.getMonotonicUlid().toString();

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_2))
                .patch()
                .uri(FriendShipController.FRIEND_SHIP_BASE_URL + "/" + id)
                .exchange();

        // Assert
        exchange.expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Friend ship not found, id = '%s'.".formatted(id));
                });
    }

    @Test
    void updateFriendShipStatusToAccepted_statusIsNoPending_shouldReturn409() {
        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_2))
                .patch()
                .uri(FriendShipController.FRIEND_SHIP_BASE_URL + "/" + ACCEPTED.getId())
                .exchange();

        // Assert
        exchange.expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.RESOURCE_CONFLICT);
                    assertThat(responseBody.getDetail()).isEqualTo("Friendship status is '%s', cannot be accepted. Only PENDING status can be updated.".formatted(FriendShipStatus.ACCEPTED));
                });
    }

    @Test
    void updateFriendShipStatusToAccepted_requesterIsNoRecipient_shouldReturn403() {
        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .patch()
                .uri(FriendShipController.FRIEND_SHIP_BASE_URL + "/" + PENDING.getId())
                .exchange();

        // Assert
        exchange.expectStatus()
                .isEqualTo(HttpStatus.FORBIDDEN)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.FORBIDDEN);
                    assertThat(responseBody.getDetail()).isEqualTo("Current user is not the recipient of friendship '%s'.".formatted(PENDING.getId()));
                });
    }
}
