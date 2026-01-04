package com.nicolas.pulse.controller;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.AbstractIntegrationTest;
import com.nicolas.pulse.adapter.controller.ChatRoomController;
import com.nicolas.pulse.adapter.dto.req.AddChatRoomMemberReq;
import com.nicolas.pulse.adapter.dto.req.CreateChatRoomReq;
import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatRoomControllerTest extends AbstractIntegrationTest {

    private static final ChatRoom ROOM_1 = ChatRoom.builder()
            .id(UlidCreator.getMonotonicUlid().toString())
            .name("Room_1")
            .createdBy(ACCOUNT_DATA_1.getId())
            .updatedBy(ACCOUNT_DATA_1.getId())
            .createdAt(instant)
            .updatedAt(instant)
            .build();

    private static final List<ChatRoomMember> ROOM_1_MEMBER = List.of(
            ChatRoomMember.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .accountId(ACCOUNT_DATA_1.getId())
                    .chatRoom(ROOM_1)
                    .isMuted(false)
                    .isPinned(false)
                    .createdBy(ACCOUNT_DATA_1.getId())
                    .updatedBy(ACCOUNT_DATA_1.getId())
                    .createdAt(instant)
                    .updatedAt(instant)
                    .build(),
            ChatRoomMember.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .accountId(ACCOUNT_DATA_2.getId())
                    .chatRoom(ROOM_1)
                    .isMuted(false)
                    .isPinned(false)
                    .createdBy(ACCOUNT_DATA_2.getId())
                    .updatedBy(ACCOUNT_DATA_2.getId())
                    .createdAt(instant)
                    .updatedAt(instant)
                    .build(),
            ChatRoomMember.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .accountId(ACCOUNT_DATA_3.getId())
                    .chatRoom(ROOM_1)
                    .isMuted(false)
                    .isPinned(false)
                    .createdBy(ACCOUNT_DATA_3.getId())
                    .updatedBy(ACCOUNT_DATA_3.getId())
                    .createdAt(instant)
                    .updatedAt(instant)
                    .build()

    );

    private static final ChatRoom ADD_MEMBER_ROOM_2 = ChatRoom.builder()
            .id(UlidCreator.getMonotonicUlid().toString())
            .name("Room_2")
            .createdBy(ACCOUNT_DATA_1.getId())
            .updatedBy(ACCOUNT_DATA_1.getId())
            .createdAt(instant)
            .updatedAt(instant)
            .build();

    private static final List<ChatRoomMember> ROOM_2_MEMBER = List.of(
            ChatRoomMember.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .accountId(ACCOUNT_DATA_1.getId())
                    .chatRoom(ADD_MEMBER_ROOM_2)
                    .isMuted(false)
                    .isPinned(false)
                    .createdBy(ACCOUNT_DATA_1.getId())
                    .updatedBy(ACCOUNT_DATA_1.getId())
                    .createdAt(instant)
                    .updatedAt(instant)
                    .build(),
            ChatRoomMember.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .accountId(ACCOUNT_DATA_4.getId())
                    .chatRoom(ADD_MEMBER_ROOM_2)
                    .isMuted(false)
                    .isPinned(false)
                    .createdBy(ACCOUNT_DATA_4.getId())
                    .updatedBy(ACCOUNT_DATA_4.getId())
                    .createdAt(instant)
                    .updatedAt(instant)
                    .build()

    );
    private static final ChatRoom ROOM_3 = ChatRoom.builder()
            .id(UlidCreator.getMonotonicUlid().toString())
            .name("Room_3")
            .createdBy(ACCOUNT_DATA_2.getId())
            .updatedBy(ACCOUNT_DATA_2.getId())
            .createdAt(instant)
            .updatedAt(instant)
            .build();

    private static final List<ChatRoomMember> ROOM_3_MEMBER = List.of(
            ChatRoomMember.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .accountId(ACCOUNT_DATA_2.getId())
                    .chatRoom(ROOM_3)
                    .isMuted(false)
                    .isPinned(false)
                    .createdBy(ACCOUNT_DATA_2.getId())
                    .updatedBy(ACCOUNT_DATA_2.getId())
                    .createdAt(instant)
                    .updatedAt(instant)
                    .build(),
            ChatRoomMember.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .accountId(ACCOUNT_DATA_3.getId())
                    .chatRoom(ROOM_3)
                    .isMuted(false)
                    .isPinned(false)
                    .createdBy(ACCOUNT_DATA_3.getId())
                    .updatedBy(ACCOUNT_DATA_3.getId())
                    .createdAt(instant)
                    .updatedAt(instant)
                    .build(),
            ChatRoomMember.builder()
                    .id(UlidCreator.getMonotonicUlid().toString())
                    .accountId(ACCOUNT_DATA_4.getId())
                    .chatRoom(ROOM_3)
                    .isMuted(false)
                    .isPinned(false)
                    .createdBy(ACCOUNT_DATA_4.getId())
                    .updatedBy(ACCOUNT_DATA_4.getId())
                    .createdAt(instant)
                    .updatedAt(instant)
                    .build()

    );


    private static final String CHAT_ROOM_SQL = """
            INSERT INTO %s (%s,%s,%s,%s,%s,%s)
            VALUES %s;
            """.formatted(DbMeta.ChatRoomData.TABLE_NAME,
            DbMeta.ChatRoomData.COL_ID, DbMeta.ChatRoomData.COL_NAME,
            DbMeta.ChatRoomData.COL_CREATED_BY, DbMeta.ChatRoomData.COL_UPDATED_BY, DbMeta.ChatRoomData.COL_CREATED_AT, DbMeta.ChatRoomData.COL_UPDATED_AT,
            Stream.of(ROOM_1, ADD_MEMBER_ROOM_2, ROOM_3)
                    .map(chatRoom -> "('%s','%s','%s','%s','%s','%s')"
                            .formatted(chatRoom.getId(), chatRoom.getName(), chatRoom.getCreatedBy(), chatRoom.getUpdatedBy(), chatRoom.getCreatedAt(), chatRoom.getUpdatedAt()))
                    .collect(Collectors.joining(",\n")));

    private static final String CHAT_ROOM_MEMBER_SQL = """
            INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s)
            VALUES %s;
            """.formatted(DbMeta.ChatRoomMemberData.TABLE_NAME,
            DbMeta.ChatRoomMemberData.COL_ID, DbMeta.ChatRoomMemberData.COL_ACCOUNT_ID, DbMeta.ChatRoomMemberData.COL_ROOM_ID, DbMeta.ChatRoomMemberData.COL_IS_MUTED, DbMeta.ChatRoomMemberData.COL_IS_PINNED,
            DbMeta.ChatRoomMemberData.COL_CREATED_BY, DbMeta.ChatRoomMemberData.COL_UPDATED_BY, DbMeta.ChatRoomMemberData.COL_CREATED_AT, DbMeta.ChatRoomMemberData.COL_UPDATED_AT,
            Stream.of(ROOM_1_MEMBER, ROOM_2_MEMBER, ROOM_3_MEMBER)
                    .flatMap(List::stream)
                    .map(chatRoomMember -> "('%s','%s','%s','%s','%s','%s','%s','%s','%s')"
                            .formatted(chatRoomMember.getId(), chatRoomMember.getAccountId(), chatRoomMember.getChatRoom().getId(), chatRoomMember.isMuted(), chatRoomMember.isPinned(),
                                    chatRoomMember.getCreatedBy(), chatRoomMember.getUpdatedBy(), chatRoomMember.getCreatedAt(), chatRoomMember.getUpdatedAt()))
                    .collect(Collectors.joining(",\n")));

    @BeforeEach
    void setUp() {
        databaseClient.sql(CHAT_ROOM_SQL)
                .fetch()
                .rowsUpdated()
                .block();
        databaseClient.sql(CHAT_ROOM_MEMBER_SQL)
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    void findAllChatRoom_Account1_success() {
        // Arrange
        List<ChatRoom> expect = List.of(ROOM_1, ADD_MEMBER_ROOM_2);

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .get()
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/")
                .exchange();

        // Assert
        exchange.expectStatus()
                .isOk()
                .expectBodyList(ChatRoom.class)
                .consumeWith(result -> {
                    List<ChatRoom> responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotEmpty();
                    assertThat(responseBody)
                            .usingRecursiveComparison()
                            .ignoringCollectionOrder()
                            .isEqualTo(expect);
                });
    }

    @Test
    void findAllChatRoom_Account2_success() {
        // Arrange
        List<ChatRoom> expect = List.of(ROOM_1, ROOM_3);

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_2))
                .get()
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/")
                .exchange();

        // Assert
        exchange.expectStatus()
                .isOk()
                .expectBodyList(ChatRoom.class)
                .consumeWith(result -> {
                    List<ChatRoom> responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotEmpty();
                    assertThat(responseBody)
                            .usingRecursiveComparison()
                            .ignoringCollectionOrder()
                            .isEqualTo(expect);
                });
    }

    @Test
    void findByChatRoomId_success() {
        // Arrange
        String roomId = ROOM_1.getId();

        // Act
        WebTestClient.ResponseSpec exchange = getRoom(roomId, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus()
                .isOk()
                .expectBody(ChatRoom.class)
                .consumeWith(result -> {
                    ChatRoom responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody).isEqualTo(ROOM_1);
                });
    }

    private WebTestClient.ResponseSpec getRoom(String roomId, UserDetails userDetails) {
        return webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userDetails))
                .get()
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/" + roomId)
                .exchange();
    }

    @Test
    void findByChatRoomId_notFound_shouldReturn404() {
        // Arrange
        String roomId = UlidCreator.getMonotonicUlid().toString();

        // Act
        WebTestClient.ResponseSpec exchange = getRoom(roomId, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus()
                .isNotFound()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Chat room not found, room id = '%s'.".formatted(roomId));
                });
    }

    @Test
    void findByChatRoomId_notRoomMember_shouldReturn403() {
        // Arrange
        String roomId = ROOM_3.getId();

        // Act
        WebTestClient.ResponseSpec exchange = getRoom(roomId, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus()
                .isForbidden()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.FORBIDDEN);
                    assertThat(responseBody.getDetail()).isEqualTo("Not allow get chat room, room id = '%s'.".formatted(roomId));
                });
    }

    @Test
    void createRoom_success() {
        // Arrange
        CreateChatRoomReq chatRoomReq = CreateChatRoomReq.builder()
                .roomName(UlidCreator.getMonotonicUlid().toString())
                .accountIdSet(Set.of(ACCOUNT_DATA_1.getId(), ACCOUNT_DATA_3.getId(), ACCOUNT_DATA_4.getId()))
                .build();
        ChatRoom expectRoom = ChatRoom.builder()
                .name(chatRoomReq.getRoomName())
                .createdBy(USER_DETAILS_ACCOUNT_1.getId())
                .updatedBy(USER_DETAILS_ACCOUNT_1.getId())
                .build();
        List<ChatRoomMember> expectMemberList = chatRoomReq.getAccountIdSet().stream()
                .map(accountId -> ChatRoomMember.builder()
                        .accountId(accountId)
                        .updatedBy(expectRoom.getUpdatedBy())
                        .createdBy(expectRoom.getCreatedBy())
                        .chatRoom(expectRoom)
                        .isMuted(false)
                        .isPinned(false)
                        .build())
                .toList();

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .post()
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/")
                .bodyValue(chatRoomReq)
                .exchange();

        // Assert
        String roomId = exchange
                .expectStatus().isCreated()
                .expectBody(String.class).returnResult()
                .getResponseBody();
        expectRoom.setId(roomId);

        getRoom(roomId, USER_DETAILS_ACCOUNT_1)
                .expectStatus().isOk()
                .expectBody(ChatRoom.class)
                .consumeWith(result -> {
                    ChatRoom responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody)
                            .usingRecursiveComparison()
                            .ignoringFieldsOfTypes(Instant.class)
                            .isEqualTo(expectRoom);
                });
        findRoomMemberByRoomId(roomId, USER_DETAILS_ACCOUNT_1)
                .expectStatus().isOk()
                .expectBodyList(ChatRoomMember.class)
                .hasSize(chatRoomReq.getAccountIdSet().size())
                .consumeWith(result -> {
                    List<ChatRoomMember> responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotEmpty();
                    assertThat(responseBody)
                            .usingRecursiveComparison()
                            .ignoringCollectionOrder()
                            .ignoringFieldsOfTypes(Instant.class)
                            .ignoringFields("id")
                            .isEqualTo(expectMemberList);
                });
    }


    @Test
    void createRoom_accountIdIsExists_shouldReturn409() {
        // Arrange
        String accountId = UlidCreator.getMonotonicUlid().toString();
        CreateChatRoomReq chatRoomReq = CreateChatRoomReq.builder()
                .roomName(UlidCreator.getMonotonicUlid().toString())
                .accountIdSet(Set.of(accountId, ACCOUNT_DATA_3.getId(), ACCOUNT_DATA_4.getId()))
                .build();

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .post()
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/")
                .bodyValue(chatRoomReq)
                .exchange();

        // Assert
        exchange.expectStatus()
                .isNotFound()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Account not found, account id = '%s'.".formatted(accountId));
                });
    }

    @Test
    void findRoomMemberByRoomId_success() {
        // Arrange
        String roomId = ROOM_1.getId();
        List<ChatRoomMember> except = new ArrayList<>(ROOM_1_MEMBER);

        // Act
        WebTestClient.ResponseSpec exchange = findRoomMemberByRoomId(roomId, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus().isOk()
                .expectBodyList(ChatRoomMember.class)
                .consumeWith(result -> {
                    List<ChatRoomMember> responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotEmpty();
                    assertThat(responseBody)
                            .usingRecursiveComparison()
                            .ignoringCollectionOrder()
                            .isEqualTo(except);
                });
    }

    @Test
    void findRoomMemberByRoomId_roomIsExists_shouldReturn409() {
        // Arrange
        String roomId = UlidCreator.getMonotonicUlid().toString();

        // Act
        WebTestClient.ResponseSpec exchange = findRoomMemberByRoomId(roomId, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus()
                .isNotFound()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Chat room not found, room id = '%s'.".formatted(roomId));
                });
    }


    @Test
    void findRoomMemberByRoomId_notRoomMember_shouldReturn403() {
        // Arrange
        String roomId = ROOM_3.getId();

        // Act
        WebTestClient.ResponseSpec exchange = findRoomMemberByRoomId(roomId, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus().isForbidden()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.FORBIDDEN);
                    assertThat(responseBody.getDetail()).isEqualTo("Not allow get chat room member, room id = '%s'.".formatted(roomId));
                });
    }

    private WebTestClient.ResponseSpec findRoomMemberByRoomId(String roomId, UserDetails userDetails) {
        return webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userDetails))
                .get()
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/%s".formatted(roomId) + "/member")
                .exchange();
    }


    @Test
    void addNewChatMember_success() {
        // Arrange
        String roomId = ADD_MEMBER_ROOM_2.getId();
        Set<String> newAccountIdSet = Set.of(ACCOUNT_DATA_2.getId(), ACCOUNT_DATA_3.getId());
        List<ChatRoomMember> expectMemberList = newAccountIdSet.stream()
                .map(accountId -> ChatRoomMember.builder()
                        .accountId(accountId)
                        .chatRoom(ADD_MEMBER_ROOM_2)
                        .updatedBy(USER_DETAILS_ACCOUNT_1.getId())
                        .createdBy(USER_DETAILS_ACCOUNT_1.getId())
                        .isMuted(false)
                        .isPinned(false)
                        .build())
                .toList();
        AddChatRoomMemberReq req = AddChatRoomMemberReq.builder()
                .accountIdList(new ArrayList<>(newAccountIdSet))
                .build();

        // Act
        WebTestClient.ResponseSpec exchange = webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(USER_DETAILS_ACCOUNT_1))
                .post()
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/%s".formatted(roomId) + "/member")
                .bodyValue(req)
                .exchange();

        // Assert
        exchange.expectStatus().isCreated();
        findRoomMemberByRoomId(roomId, USER_DETAILS_ACCOUNT_1)
                .expectStatus().isOk()
                .expectBodyList(ChatRoomMember.class)
                .hasSize(ROOM_2_MEMBER.size() + req.getAccountIdList().size())
                .consumeWith(result -> {
                    List<ChatRoomMember> responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotEmpty();
                    assertThat(responseBody)
                            .filteredOn(member -> newAccountIdSet.contains(member.getAccountId()))
                            .hasSize(newAccountIdSet.size())
                            .usingRecursiveComparison()
                            .ignoringCollectionOrder()
                            .ignoringFields("id")
                            .ignoringFieldsOfTypes(Instant.class)
                            .isEqualTo(expectMemberList);
                });
    }

    @Test
    void addNewChatMember_roomNotFound_shouldReturn404() {
        String roomId = UlidCreator.getMonotonicUlid().toString();
        Set<String> newAccountIdSet = Set.of(ACCOUNT_DATA_2.getId(), ACCOUNT_DATA_3.getId());
        AddChatRoomMemberReq req = AddChatRoomMemberReq.builder()
                .accountIdList(new ArrayList<>(newAccountIdSet))
                .build();
        // Act
        WebTestClient.ResponseSpec exchange = addChatMember(roomId, req, USER_DETAILS_ACCOUNT_1);
        
        // Assert
        exchange.expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Chat Room not found, room id = '%s'.".formatted(roomId));
                });
    }

    @Test
    void addNewChatMember_notAllow_shouldReturn403() {
        // Arrange
        String roomId = ADD_MEMBER_ROOM_2.getId();
        Set<String> newAccountIdSet = Set.of(ACCOUNT_DATA_2.getId(), ACCOUNT_DATA_3.getId());
        AddChatRoomMemberReq req = AddChatRoomMemberReq.builder()
                .accountIdList(new ArrayList<>(newAccountIdSet))
                .build();

        // Act
        WebTestClient.ResponseSpec exchange = addChatMember(roomId, req, USER_DETAILS_ACCOUNT_2);

        // Assert
        exchange.expectStatus().isForbidden()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.FORBIDDEN);
                    assertThat(responseBody.getDetail()).isEqualTo("Not allow add new member, room id = '%s'.".formatted(roomId));
                });
    }

    @Test
    void addNewChatMember_accountIsExists_shouldReturn403() {
        // Arrange
        String roomId = ADD_MEMBER_ROOM_2.getId();
        String accountId = UlidCreator.getMonotonicUlid().toString();
        Set<String> newAccountIdSet = Set.of(accountId, ACCOUNT_DATA_3.getId());
        AddChatRoomMemberReq req = AddChatRoomMemberReq.builder()
                .accountIdList(new ArrayList<>(newAccountIdSet))
                .build();
        // Act
        WebTestClient.ResponseSpec exchange = addChatMember(roomId, req, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Account not found, account id = '%s'.".formatted(accountId));
                });
    }

    @Test
    void addNewChatMember_accountIsExistsInRoom_shouldReturn409() {
        // Arrange
        String roomId = ADD_MEMBER_ROOM_2.getId();
        String accountId = ACCOUNT_DATA_1.getId();
        Set<String> newAccountIdSet = Set.of(accountId, ACCOUNT_DATA_3.getId());
        AddChatRoomMemberReq req = AddChatRoomMemberReq.builder()
                .accountIdList(new ArrayList<>(newAccountIdSet))
                .build();
        // Act
        WebTestClient.ResponseSpec exchange = addChatMember(roomId, req, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.RESOURCE_CONFLICT);
                    assertThat(responseBody.getDetail()).isEqualTo("Account is exists in room, account id = '%s'.".formatted(accountId));
                });
    }

    private WebTestClient.ResponseSpec addChatMember(String roomId, AddChatRoomMemberReq req, UserDetails userDetails) {
        return webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userDetails))
                .post()
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/%s".formatted(roomId) + "/member")
                .bodyValue(req)
                .exchange();
    }
}
