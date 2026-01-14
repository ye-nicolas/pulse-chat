package com.nicolas.pulse.adapter.controller.scoket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolas.pulse.adapter.dto.mapper.ChatMessageMapper;
import com.nicolas.pulse.adapter.dto.req.AddChatMessageReq;
import com.nicolas.pulse.adapter.dto.req.GetMessageReq;
import com.nicolas.pulse.adapter.dto.req.UpdateChatMessageReq;
import com.nicolas.pulse.adapter.dto.res.ChatMessageLastReadRes;
import com.nicolas.pulse.adapter.dto.res.ChatMessageRes;
import com.nicolas.pulse.adapter.dto.res.MessageRes;
import com.nicolas.pulse.service.usecase.chat.message.AddChatMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.DeleteChatMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.FindHistoryMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.UpdateChatMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.read.UpdateChatRoomMemberLastReadMessageUseCase;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import com.nicolas.pulse.service.usecase.sink.SubscribeChatRoomUseCase;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Controller
public class ChatMessageController {
    private final Validator validator;
    private final SubscribeChatRoomUseCase subscribeChatRoomUseCase;
    private final ChatRoomManager chatRoomManager;
    private final FindHistoryMessageUseCase findHistoryMessageUseCase;
    private final AddChatMessageUseCase addChatMessageUseCase;
    private final UpdateChatMessageUseCase updateChatMessageUseCase;
    private final DeleteChatMessageUseCase deleteChatMessageUseCase;
    private final UpdateChatRoomMemberLastReadMessageUseCase updateChatRoomMemberLastReadMessageUseCase;
    private final Tracer tracer;

    public ChatMessageController(Validator validator,
                                 SubscribeChatRoomUseCase subscribeChatRoomUseCase,
                                 ChatRoomManager chatRoomManager,
                                 FindHistoryMessageUseCase findHistoryMessageUseCase,
                                 AddChatMessageUseCase addChatMessageUseCase,
                                 UpdateChatMessageUseCase updateChatMessageUseCase,
                                 DeleteChatMessageUseCase deleteChatMessageUseCase,
                                 UpdateChatRoomMemberLastReadMessageUseCase updateChatRoomMemberLastReadMessageUseCase,
                                 ObjectMapper objectMapper,
                                 Tracer tracer) {
        this.validator = validator;
        this.subscribeChatRoomUseCase = subscribeChatRoomUseCase;
        this.chatRoomManager = chatRoomManager;
        this.findHistoryMessageUseCase = findHistoryMessageUseCase;
        this.addChatMessageUseCase = addChatMessageUseCase;
        this.updateChatMessageUseCase = updateChatMessageUseCase;
        this.deleteChatMessageUseCase = deleteChatMessageUseCase;
        this.updateChatRoomMemberLastReadMessageUseCase = updateChatRoomMemberLastReadMessageUseCase;
        this.tracer = tracer;
    }

    @MessageMapping("session.open.room.{roomId}")
    public Flux<MessageRes<ChatMessageRes>> openSessionByRoom(RSocketRequester requester, @DestinationVariable("roomId") String roomId) {
        SubscribeChatRoomUseCase.Input input = new SubscribeChatRoomUseCase.Input(roomId);
        SubscribeChatRoomUseCase.Output output = new SubscribeChatRoomUseCase.Output();
        return subscribeChatRoomUseCase.execute(input, output)
                .thenMany(Flux.defer(() -> output.getChatMessageFlux()
                        .map(ChatMessageMapper::domainToRes)
                        .map(v -> MessageRes.<ChatMessageRes>builder().data(v).build())))
                .onErrorResume(throwable -> Flux.just(processException(throwable, ChatMessageRes.class)));
    }

    @MessageMapping("chat.message.add")
    public Mono<MessageRes<ChatMessageRes>> addMessage(@Payload Mono<AddChatMessageReq> mono) {
        AddChatMessageUseCase.Output output = new AddChatMessageUseCase.Output();
        return mono.delayUntil(this::validate)
                .flatMap(req -> addChatMessageUseCase.execute(AddChatMessageUseCase.Input.builder().roomId(req.getRoomId())
                        .chatMessageType(req.getType())
                        .content(req.getContent())
                        .build(), output))
                .then(Mono.fromRunnable(() -> chatRoomManager.broadcastMessage(output.getChatMessage())))
                .then(Mono.fromSupplier(() -> MessageRes.<ChatMessageRes>builder().data(ChatMessageMapper.domainToRes(output.getChatMessage())).build()))
                .onErrorResume(throwable -> Mono.fromSupplier(() -> processException(throwable, ChatMessageRes.class)));
    }

    @MessageMapping("chat.message.update.{messageId}")
    public Mono<MessageRes<ChatMessageRes>> updateMessage(@DestinationVariable String messageId,
                                                          @Payload Mono<UpdateChatMessageReq> reqMono) {
        UpdateChatMessageUseCase.Output output = new UpdateChatMessageUseCase.Output();
        return reqMono.delayUntil(this::validate)
                .flatMap(req -> updateChatMessageUseCase.execute(UpdateChatMessageUseCase.Input.builder()
                        .messageId(messageId)
                        .newContent(req.getNewContent())
                        .build(), output))
                .then(Mono.fromRunnable(() -> chatRoomManager.broadcastMessage(output.getNewChatMessage())))
                .then(Mono.fromSupplier(() -> MessageRes.<ChatMessageRes>builder().data(ChatMessageMapper.domainToRes(output.getNewChatMessage())).build()))
                .onErrorResume(throwable -> Mono.fromSupplier(() -> processException(throwable, ChatMessageRes.class)));
    }

    @MessageMapping("chat.message.delete.{messageId}")
    public Mono<MessageRes<ChatMessageRes>> deleteMessage(@DestinationVariable String messageId) {
        DeleteChatMessageUseCase.Output output = new DeleteChatMessageUseCase.Output();
        return deleteChatMessageUseCase.execute(new DeleteChatMessageUseCase.Input(messageId), output)
                .then(Mono.fromRunnable(() -> chatRoomManager.broadcastMessage(output.getNewChatMessage())))
                .then(Mono.fromSupplier(() -> MessageRes.<ChatMessageRes>builder().data(ChatMessageMapper.domainToRes(output.getNewChatMessage())).build()))
                .onErrorResume(throwable -> Mono.fromSupplier(() -> processException(throwable, ChatMessageRes.class)));
    }

    @MessageMapping("chat.message.read.{messageId}")
    public Mono<MessageRes<ChatMessageLastReadRes>> readMessage(@DestinationVariable String messageId) {
        UpdateChatRoomMemberLastReadMessageUseCase.Input input = new UpdateChatRoomMemberLastReadMessageUseCase.Input(messageId);
        UpdateChatRoomMemberLastReadMessageUseCase.Output output = new UpdateChatRoomMemberLastReadMessageUseCase.Output();
        return updateChatRoomMemberLastReadMessageUseCase.execute(input, output)
                .then(Mono.fromSupplier(() -> MessageRes.<ChatMessageLastReadRes>builder().data(new ChatMessageLastReadRes(output.getChatMessageLastRead().getLastMessageId())).build()))
                .onErrorResume(throwable -> Mono.fromSupplier(() -> processException(throwable, ChatMessageLastReadRes.class)));
    }

    @MessageMapping("chat.history.get.{roomId}")
    public Flux<MessageRes<ChatMessageRes>> getHistory(@DestinationVariable String roomId,
                                                       @Payload Mono<GetMessageReq> mono) {
        FindHistoryMessageUseCase.Output output = new FindHistoryMessageUseCase.Output();
        return mono.delayUntil(this::validate)
                .flatMap(req -> findHistoryMessageUseCase.execute(FindHistoryMessageUseCase.Input.builder()
                        .roomId(roomId)
                        .size(req.getSize())
                        .page(req.getPage())
                        .build(), output))
                .thenMany(Flux.defer(() -> output.getMessageFlux()
                        .map(ChatMessageMapper::domainToRes)
                        .map(v -> MessageRes.<ChatMessageRes>builder().data(v).build())))
                .onErrorResume(throwable -> Flux.just(processException(throwable, ChatMessageRes.class)));
    }

    private <T> Mono<Void> validate(T body) {
        Set<ConstraintViolation<T>> errors = validator.validate(body);
        return errors.isEmpty()
                ? Mono.empty()
                : Mono.error(() -> new ConstraintViolationException(errors));
    }

    private <T> MessageRes<T> processException(Throwable throwable, Class<T> tclass) {
     String traceId = (tracer.currentSpan() != null) ? Objects.requireNonNull(tracer.currentSpan()).context().traceId() : null;
       
        ProblemDetail problemDetail = ExceptionHandlerUtils.createProblemDetail(throwable, traceId);
        return MessageRes.<T>builder().problemDetail(problemDetail).status(problemDetail.getStatus()).build();
    }
}
