package com.nicolas.pulse.controller.socket;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.AbstractIntegrationTest;
import com.nicolas.pulse.adapter.dto.req.AddChatMessageReq;
import com.nicolas.pulse.adapter.dto.req.GetMessageReq;
import com.nicolas.pulse.adapter.dto.req.UpdateChatMessageReq;
import com.nicolas.pulse.adapter.dto.res.ChatMessageLastReadRes;
import com.nicolas.pulse.adapter.dto.res.ChatMessageRes;
import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import com.nicolas.pulse.util.JwtUtil;
import io.rsocket.metadata.WellKnownMimeType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.nicolas.pulse.controller.ChatRoomControllerTest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ChatMessageControllerTest extends AbstractIntegrationTest {
    @Autowired
    private RSocketRequester.Builder requesterBuilder;

    private final RSocketStrategies strategies = RSocketStrategies.builder()
            .encoder(new Jackson2CborEncoder()) // 編碼器
            .decoder(new Jackson2CborDecoder()) // 解碼器
            .build();

    @Value("${jwt.key}")
    private String secret;

    @LocalRSocketServerPort
    private int rsocketPort;

    private final MimeType authenticationMimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
    private Mono<RSocketRequester> requesterMono;
    private byte[] authMetadata;

    @BeforeEach
    void setup() {
        this.requesterMono = Mono.defer(() -> Mono.just(requesterBuilder
                .rsocketStrategies(strategies)
                .dataMimeType(MediaType.APPLICATION_CBOR)
                .websocket(URI.create("ws://localhost:%s/web-socket".formatted(rsocketPort)))));
        byte[] jwtBytes = JwtUtil.generateAccessToken(JwtUtil.generateSecretKey(secret), UlidCreator.getMonotonicUlid().toString(), USER_DETAILS_ACCOUNT_2.getId(), 300000L, Map.of()).getBytes(StandardCharsets.UTF_8);
        authMetadata = new byte[1 + jwtBytes.length];
        authMetadata[0] = (byte) 0x81;
        System.arraycopy(jwtBytes, 0, authMetadata, 1, jwtBytes.length);
    }

    @Test
    void addMessage_success() {
        // Arrange
        String roomId = ROOM_3.getId();
        String chatContent = "Hello RSocket!";
        AddChatMessageReq addMsgReq = AddChatMessageReq.builder()
                .roomId(roomId)
                .content(chatContent)
                .type(ChatMessageType.TEXT)
                .build();
        ChatMessageRes except = ChatMessageRes.builder()
                .roomId(roomId)
                .type(addMsgReq.getType())
                .content(chatContent)
                .createdBy(USER_DETAILS_ACCOUNT_2.getId())
                .isDelete(false)
                .build();

        // Act + Arrange
        RSocketRequester requester = requesterMono.block(Duration.ofSeconds(5));
        assertThat(requester).isNotNull();

        StepVerifier.create(requester.route("session.open.room.{roomId}", roomId)
                        .metadata(authMetadata, authenticationMimeType)
                        .retrieveFlux(ChatMessageRes.class))
                .expectSubscription()
                .thenAwait(Duration.ofMillis(200)) // 重要：給 Server 一點時間完成 Sink 註冊
                .then(() -> requester.route("chat.message.add")
                        .metadata(authMetadata, authenticationMimeType)
                        .data(Mono.just(addMsgReq))
                        .retrieveMono(ChatMessageRes.class)
                        .doOnNext(res -> {
                            assertThat(res)
                                    .usingRecursiveComparison()
                                    .ignoringFields("id")
                                    .ignoringFieldsOfTypes(Instant.class)
                                    .ignoringExpectedNullFields()
                                    .isEqualTo(except);
                        })
                        .subscribe())
                .assertNext(res -> assertThat(res)
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .ignoringFieldsOfTypes(Instant.class)
                        .ignoringExpectedNullFields()
                        .isEqualTo(except))
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void deleteMessage_success() {
        // Arrange
        ChatMessage first = ROOM_3_CHAT_MESSAGE_LIST.stream().filter(chatMessage -> chatMessage.getCreatedBy().equals(USER_DETAILS_ACCOUNT_2.getId())).toList().getFirst();
        ChatMessageRes except = ChatMessageRes.builder()
                .id(first.getId())
                .roomId(first.getRoomId())
                .type(first.getType())
                .createdBy(first.getCreatedBy())
                .content("")
                .isDelete(true)
                .build();

        // Act + Arrange
        RSocketRequester requester = requesterMono.block(Duration.ofSeconds(5));
        assertThat(requester).isNotNull();

        StepVerifier.create(requester.route("session.open.room.{roomId}", first.getRoomId())
                        .metadata(authMetadata, authenticationMimeType)
                        .retrieveFlux(ChatMessageRes.class))
                .expectSubscription()
                .thenAwait(Duration.ofMillis(200)) // 重要：給 Server 一點時間完成 Sink 註冊
                .then(() -> requester.route("chat.message.delete.{messageId}", first.getId())
                        .metadata(authMetadata, authenticationMimeType)
                        .retrieveMono(ChatMessageRes.class)
                        .doOnNext(res -> {
                            assertThat(res)
                                    .usingRecursiveComparison()
                                    .ignoringFields("id")
                                    .ignoringFieldsOfTypes(Instant.class)
                                    .ignoringExpectedNullFields()
                                    .isEqualTo(except);
                        })
                        .subscribe())
                .assertNext(res -> assertThat(res)
                        .usingRecursiveComparison()
                        .ignoringFieldsOfTypes(Instant.class)
                        .ignoringExpectedNullFields()
                        .isEqualTo(except))
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void updateMessage_success() {
        // Arrange
        ChatMessage message = ROOM_3_CHAT_MESSAGE_LIST.stream().filter(chatMessage -> chatMessage.getCreatedBy().equals(USER_DETAILS_ACCOUNT_2.getId())).toList().getFirst();
        UpdateChatMessageReq req = UpdateChatMessageReq.builder()
                .newContent("AAAAAA")
                .build();
        ChatMessageRes except = ChatMessageRes.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .type(message.getType())
                .content(req.getNewContent())
                .createdBy(message.getCreatedBy())
                .isDelete(message.isDelete())
                .build();

        // Act + Arrange
        RSocketRequester requester = requesterMono.block(Duration.ofSeconds(5));
        assertThat(requester).isNotNull();

        StepVerifier.create(requester.route("session.open.room.{roomId}", message.getRoomId())
                        .metadata(authMetadata, authenticationMimeType)
                        .retrieveFlux(ChatMessageRes.class))
                .expectSubscription()
                .thenAwait(Duration.ofMillis(200)) // 重要：給 Server 一點時間完成 Sink 註冊
                .then(() -> requester.route("chat.message.update.{messageId}", message.getId())
                        .metadata(authMetadata, authenticationMimeType)
                        .data(Mono.just(req))
                        .retrieveMono(ChatMessageRes.class)
                        .doOnNext(res -> {
                            assertThat(res)
                                    .usingRecursiveComparison()
                                    .ignoringFields("id")
                                    .ignoringFieldsOfTypes(Instant.class)
                                    .ignoringExpectedNullFields()
                                    .isEqualTo(except);
                        })
                        .subscribe())
                .assertNext(res -> assertThat(res)
                        .usingRecursiveComparison()
                        .ignoringFieldsOfTypes(Instant.class)
                        .ignoringExpectedNullFields()
                        .isEqualTo(except))
                .thenCancel()
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void readMessage_success() {
        // Arrange
        ChatMessage message = ROOM_3_CHAT_MESSAGE_LIST.getLast();
        ChatMessageLastReadRes except = ChatMessageLastReadRes.builder()
                .readChatMessageId(message.getId())
                .build();

        // Act + Arrange
        RSocketRequester requester = requesterMono.block(Duration.ofSeconds(2));
        assertThat(requester).isNotNull();

        StepVerifier.create(requester.route("chat.message.read.{messageId}", message.getId())
                        .metadata(authMetadata, authenticationMimeType)
                        .retrieveMono(ChatMessageLastReadRes.class))
                .expectSubscription()
                .assertNext(res -> assertThat(res).isEqualTo(except))
                .expectComplete()
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void getMessage_success() {
        // Arrange
        GetMessageReq req = GetMessageReq.builder().page(0).size(20).build();
        List<ChatMessage> expect = ROOM_3_CHAT_MESSAGE_LIST.stream()
                .sorted(Comparator.comparing(ChatMessage::getId).reversed())
                .limit(req.getSize())
                .toList();

        // Act + Arrange
        RSocketRequester requester = requesterMono.block(Duration.ofSeconds(5));
        assertThat(requester).isNotNull();

        StepVerifier.create(requester.route("chat.history.get.{roomId}", ROOM_3.getId())
                        .metadata(authMetadata, authenticationMimeType)
                        .data(Mono.just(req))
                        .retrieveFlux(ChatMessageRes.class))
                .expectSubscription()
                .recordWith(ArrayList::new) // 收集返回
                .expectNextCount(expect.size())
                .consumeRecordedWith(results -> assertThat(results)
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expect))
                .expectComplete()
                .verify(Duration.ofSeconds(2));
    }

    @AfterEach
    void tearDown() {
        requesterMono.flatMap(req -> {
            req.rsocketClient().dispose();
            return Mono.empty();
        }).subscribe();
    }

    @Override
    protected String provideSpecificSql() {
        return CHAT_ROOM_SQL + CHAT_ROOM_MEMBER_SQL + CHAT_ROOM_MESSAGE_SQL + CHAT_MESSAGE_LAST_READ_SQL;
    }
}
