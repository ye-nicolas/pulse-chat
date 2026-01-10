package com.nicolas.pulse.controller;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.AbstractIntegrationTest;
import com.nicolas.pulse.adapter.controller.ChatRoomController;
import com.nicolas.pulse.adapter.dto.req.AddChatRoomMemberReq;
import com.nicolas.pulse.adapter.dto.req.CreateChatRoomReq;
import com.nicolas.pulse.adapter.dto.req.RemoveChatRoomMemberReq;
import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.entity.domain.chat.ChatMessageLastRead;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import com.nicolas.pulse.entity.event.DeleteMemberEvent;
import com.nicolas.pulse.entity.event.DeleteRoomEvent;
import com.nicolas.pulse.service.repository.ChatMessageReadLastRepository;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.service.usecase.sink.ChatEventBus;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class ChatRoomControllerTest extends AbstractIntegrationTest {
    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    ChatMessageReadLastRepository chatMessageReadLastRepository;
    @MockitoSpyBean
    ChatEventBus chatEventBus;

    public static final ChatRoom ROOM_1 = ChatRoom.builder()
            .id(UlidCreator.getMonotonicUlid().toString())
            .name("Room_1")
            .createdBy(ACCOUNT_DATA_1.getId())
            .updatedBy(ACCOUNT_DATA_1.getId())
            .createdAt(instant)
            .updatedAt(instant)
            .build();

    public static final List<ChatRoomMember> ROOM_1_MEMBER = List.of(
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
    public static final ChatRoom ROOM_3 = ChatRoom.builder()
            .id(UlidCreator.getMonotonicUlid().toString())
            .name("Room_3")
            .createdBy(ACCOUNT_DATA_2.getId())
            .updatedBy(ACCOUNT_DATA_2.getId())
            .createdAt(instant)
            .updatedAt(instant)
            .build();

    public static final List<ChatRoomMember> ROOM_3_MEMBER = List.of(
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

    public static final List<ChatMessage> ROOM_3_CHAT_MESSAGE_LIST = ROOM_3_MEMBER.stream()
            .flatMap(member -> IntStream.range(0, ThreadLocalRandom.current().nextInt(ROOM_3_MEMBER.size(), 11))
                    .mapToObj(i -> ChatMessage.builder()
                            .id(UlidCreator.getMonotonicUlid().toString())
                            .roomId(member.getChatRoom().getId())
                            .memberId(member.getId())
                            .type(ChatMessageType.TEXT)
                            .content(UlidCreator.getMonotonicUlid().toString())
                            .isDelete(false)
                            .createdBy(member.getAccountId())
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build()))
            .toList();

    private static final List<ChatMessageLastRead> ROOM_3_CHAT_MESSAGE_LAST_READ_LIST = ROOM_3_MEMBER.stream().map(m -> {
        ChatMessage chatMessage = ROOM_3_CHAT_MESSAGE_LIST.get(ThreadLocalRandom.current().nextInt(ROOM_3_MEMBER.size(), ROOM_3_CHAT_MESSAGE_LIST.size()));
        return ChatMessageLastRead.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .memberId(m.getId())
                .lastMessageId(chatMessage.getId())
                .roomId(chatMessage.getRoomId())
                .createdBy(m.getAccountId())
                .createdAt(Instant.now())
                .updateAt(Instant.now())
                .build();
    }).toList();


    public static final String CHAT_ROOM_SQL = """
            INSERT INTO %s (%s,%s,%s,%s,%s,%s)
            VALUES %s;
            """.formatted(DbMeta.ChatRoomData.TABLE_NAME,
            DbMeta.ChatRoomData.COL_ID, DbMeta.ChatRoomData.COL_NAME,
            DbMeta.ChatRoomData.COL_CREATED_BY, DbMeta.ChatRoomData.COL_UPDATED_BY, DbMeta.ChatRoomData.COL_CREATED_AT, DbMeta.ChatRoomData.COL_UPDATED_AT,
            Stream.of(ROOM_1, ADD_MEMBER_ROOM_2, ROOM_3)
                    .map(chatRoom -> "('%s','%s','%s','%s','%s','%s')"
                            .formatted(chatRoom.getId(), chatRoom.getName(), chatRoom.getCreatedBy(), chatRoom.getUpdatedBy(), chatRoom.getCreatedAt(), chatRoom.getUpdatedAt()))
                    .collect(Collectors.joining(",\n")));

    public static final String CHAT_ROOM_MEMBER_SQL = """
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

    public static final String CHAT_ROOM_MESSAGE_SQL = """
            INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s)
            VALUES %s;
            """.formatted(DbMeta.ChatMessageData.TABLE_NAME,
            DbMeta.ChatMessageData.COL_ID, DbMeta.ChatMessageData.COL_ROOM_ID, DbMeta.ChatMessageData.COL_MEMBER_ID, DbMeta.ChatMessageData.COL_TYPE, DbMeta.ChatMessageData.COL_CONTENT, DbMeta.ChatMessageData.COL_IS_DELETE,
            DbMeta.ChatMessageData.COL_CREATED_BY, DbMeta.ChatMessageData.COL_CREATED_AT, DbMeta.ChatMessageData.COL_UPDATED_AT,
            ROOM_3_CHAT_MESSAGE_LIST.stream().map(chatMessage -> "('%s','%s','%s','%s','%s',%s,'%s','%s','%s')"
                            .formatted(chatMessage.getId(), chatMessage.getRoomId(), chatMessage.getMemberId(), chatMessage.getType(), chatMessage.getContent(), chatMessage.isDelete(),
                                    chatMessage.getCreatedBy(), chatMessage.getCreatedAt(), chatMessage.getUpdatedAt()))
                    .collect(Collectors.joining(",\n")));

    public static final String CHAT_MESSAGE_LAST_READ_SQL = """
            INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s)
            VALUES %s;
            """.formatted(DbMeta.ChatMessageLastReadData.TABLE_NAME,
            DbMeta.ChatMessageLastReadData.COL_ID, DbMeta.ChatMessageLastReadData.COL_LAST_MESSAGE_ID, DbMeta.ChatMessageLastReadData.COL_ROOM_ID, DbMeta.ChatMessageLastReadData.COL_MEMBER_ID,
            DbMeta.ChatMessageLastReadData.COL_CREATED_BY, DbMeta.ChatMessageLastReadData.COL_CREATED_AT, DbMeta.ChatMessageLastReadData.COL_UPDATED_AT,
            ROOM_3_CHAT_MESSAGE_LAST_READ_LIST.stream().map(chatMessageLastRead -> "('%s','%s','%s','%s','%s','%s','%s')"
                            .formatted(chatMessageLastRead.getId(), chatMessageLastRead.getLastMessageId(), chatMessageLastRead.getRoomId(), chatMessageLastRead.getMemberId(),
                                    chatMessageLastRead.getCreatedBy(), chatMessageLastRead.getCreatedAt(), chatMessageLastRead.getUpdateAt()))
                    .collect(Collectors.joining(",\n")));

    @Override
    protected String provideSpecificSql() {
        return CHAT_ROOM_SQL + CHAT_ROOM_MEMBER_SQL + CHAT_ROOM_MESSAGE_SQL + CHAT_MESSAGE_LAST_READ_SQL;
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
                    assertThat(responseBody.getDetail()).isEqualTo("Chat room not found, room id = '%s'.".formatted(roomId));
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

    @Test
    void removeRoomMember_success() {
        // Arrange
        String roomId = ROOM_1.getId();
        ChatRoomMember removeMember = ROOM_1_MEMBER.getLast();
        List<ChatRoomMember> except = ROOM_1_MEMBER.stream().filter(chatRoomMember -> !chatRoomMember.equals(removeMember)).toList();
        RemoveChatRoomMemberReq req = RemoveChatRoomMemberReq.builder()
                .memberIdList(List.of(removeMember.getId()))
                .build();
        // Act
        WebTestClient.ResponseSpec exchange = removeChatMember(roomId, req, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus().isOk();
        findRoomMemberByRoomId(roomId, USER_DETAILS_ACCOUNT_1)
                .expectStatus().isOk()
                .expectBodyList(ChatRoomMember.class)
                .hasSize(except.size())
                .consumeWith(result -> {
                    List<ChatRoomMember> responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotEmpty();
                    assertThat(responseBody)
                            .usingRecursiveComparison()
                            .ignoringCollectionOrder()
                            .ignoringFieldsOfTypes(Instant.class)
                            .isEqualTo(except);
                });
        verify(chatEventBus, timeout(2000)).publishMemberDelete(new DeleteMemberEvent(roomId, Set.of(removeMember.getAccountId())));
    }

    @Test
    void removeRoomMember_roomNotFound_shouldReturn404() {
        String roomId = UlidCreator.getMonotonicUlid().toString();
        ChatRoomMember removeMember = ROOM_1_MEMBER.getFirst();
        RemoveChatRoomMemberReq req = RemoveChatRoomMemberReq.builder()
                .memberIdList(List.of(removeMember.getId()))
                .build();
        // Act
        WebTestClient.ResponseSpec exchange = removeChatMember(roomId, req, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Chat room not found, room id = '%s'.".formatted(roomId));
                });
    }

    @Test
    void removeRoomMember_memberNotFound_shouldReturn404() {
        String roomId = ROOM_1.getId();
        String memberId = UlidCreator.getMonotonicUlid().toString();
        RemoveChatRoomMemberReq req = RemoveChatRoomMemberReq.builder()
                .memberIdList(List.of(memberId))
                .build();
        // Act
        WebTestClient.ResponseSpec exchange = removeChatMember(roomId, req, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Member not found by '%s', member id = '%s'.".formatted(ROOM_1.getName(), memberId));
                });
    }

    @Test
    void removeRoomMember_notAllow_shouldReturn403() {
        String roomId = ADD_MEMBER_ROOM_2.getId();
        ChatRoomMember removeMember = ROOM_2_MEMBER.getFirst();
        RemoveChatRoomMemberReq req = RemoveChatRoomMemberReq.builder()
                .memberIdList(List.of(removeMember.getId()))
                .build();
        // Act
        WebTestClient.ResponseSpec exchange = removeChatMember(roomId, req, USER_DETAILS_ACCOUNT_2);

        // Assert
        exchange.expectStatus().isForbidden()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.FORBIDDEN);
                    assertThat(responseBody.getDetail()).isEqualTo("Not allow delete chat room member, room id = '%s'.".formatted(roomId));
                });
    }

    private WebTestClient.ResponseSpec removeChatMember(String roomId, RemoveChatRoomMemberReq req, UserDetails userDetails) {
        return webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userDetails))
                .method(HttpMethod.DELETE) //
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/%s".formatted(roomId) + "/member")
                .bodyValue(req)
                .exchange();
    }

    @Test
    void deleteRoom_success() {
        // Arrange
        String roomId = ROOM_3.getId();
        SecurityAccount securityAccount = USER_DETAILS_ACCOUNT_2;

        // Act
        WebTestClient.ResponseSpec responseSpec = deleteChatRoom(roomId, securityAccount);

        // Assert
        responseSpec.expectStatus().isOk();
        getRoom(roomId, securityAccount).expectStatus().isNotFound();
        findRoomMemberByRoomId(roomId, securityAccount).expectStatus().isNotFound();
        StepVerifier.create(chatMessageRepository.findAllByRoomId(roomId))
                .expectNextCount(0)
                .verifyComplete();
        StepVerifier.create(chatMessageReadLastRepository.findAllByRoomId(roomId))
                .expectNextCount(0)
                .verifyComplete();
        verify(chatEventBus, timeout(2000)).publishRoomDelete(new DeleteRoomEvent(roomId));
    }

    @Test
    void deleteRoom_notAllow_shouldReturn403() {
        // Arrange
        String roomId = ROOM_3.getId();

        // Act
        WebTestClient.ResponseSpec exchange = deleteChatRoom(roomId, USER_DETAILS_ACCOUNT_1);


        // Assert
        exchange.expectStatus().isForbidden()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.FORBIDDEN);
                    assertThat(responseBody.getDetail()).isEqualTo("Not allow delete chat room, room id = '%s'.".formatted(roomId));
                });
    }

    @Test
    void deleteRoom_roomNotFound_shouldReturn404() {
        // Arrange
        String roomId = UlidCreator.getMonotonicUlid().toString();

        // Act
        WebTestClient.ResponseSpec exchange = deleteChatRoom(roomId, USER_DETAILS_ACCOUNT_1);

        // Assert
        exchange.expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    ProblemDetail responseBody = result.getResponseBody();
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.getTitle()).isEqualTo(ExceptionHandlerUtils.TARGET_NOT_FOUND);
                    assertThat(responseBody.getDetail()).isEqualTo("Chat room not found, room id = '%s'.".formatted(roomId));
                });
    }

    private WebTestClient.ResponseSpec deleteChatRoom(String roomId, UserDetails userDetails) {
        return webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userDetails))
                .method(HttpMethod.DELETE)
                .uri(ChatRoomController.CHAT_ROOM_BASE_URL + "/%s".formatted(roomId))
                .exchange();
    }
}
