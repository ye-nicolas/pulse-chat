package com.nicolas.pulse.adapter.controller.scoket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolas.pulse.adapter.dto.mapper.ChatMessageMapper;
import com.nicolas.pulse.adapter.dto.req.AddChatMessageReq;
import com.nicolas.pulse.adapter.dto.req.GetMessageReqDTO;
import com.nicolas.pulse.adapter.dto.req.UpdateChatMessageReq;
import com.nicolas.pulse.adapter.dto.res.ChatMessageReadLastResDTO;
import com.nicolas.pulse.adapter.dto.res.ChatMessageRes;
import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.infrastructure.config.MdcProperties;
import com.nicolas.pulse.service.usecase.chat.message.AddChatMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.DeleteChatMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.FindHistoryMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.UpdateChatMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.read.UpdateChatRoomMemberLastReadMessageUseCase;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import io.rsocket.exceptions.ApplicationErrorException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Controller
public class ChatMessageController {
    private final Validator validator;
    private final ChatRoomManager chatRoomManager;
    private final FindHistoryMessageUseCase findHistoryMessageUseCase;
    private final AddChatMessageUseCase addChatMessageUseCase;
    private final UpdateChatMessageUseCase updateChatMessageUseCase;
    private final DeleteChatMessageUseCase deleteChatMessageUseCase;
    private final UpdateChatRoomMemberLastReadMessageUseCase updateChatRoomMemberLastReadMessageUseCase;
    private final ObjectMapper objectMapper;
    private final MdcProperties mdcProperties;

    public ChatMessageController(Validator validator,
                                 ChatRoomManager chatRoomManager,
                                 FindHistoryMessageUseCase findHistoryMessageUseCase,
                                 AddChatMessageUseCase addChatMessageUseCase,
                                 UpdateChatMessageUseCase updateChatMessageUseCase,
                                 DeleteChatMessageUseCase deleteChatMessageUseCase,
                                 UpdateChatRoomMemberLastReadMessageUseCase updateChatRoomMemberLastReadMessageUseCase,
                                 ObjectMapper objectMapper,
                                 MdcProperties mdcProperties) {
        this.validator = validator;
        this.chatRoomManager = chatRoomManager;
        this.findHistoryMessageUseCase = findHistoryMessageUseCase;
        this.addChatMessageUseCase = addChatMessageUseCase;
        this.updateChatMessageUseCase = updateChatMessageUseCase;
        this.deleteChatMessageUseCase = deleteChatMessageUseCase;
        this.updateChatRoomMemberLastReadMessageUseCase = updateChatRoomMemberLastReadMessageUseCase;
        this.objectMapper = objectMapper;
        this.mdcProperties = mdcProperties;
    }

    @MessageMapping("chat.message.add")
    public Mono<ChatMessageRes> addMessage(@Payload Mono<AddChatMessageReq> mono) {
        AddChatMessageUseCase.Output output = new AddChatMessageUseCase.Output();
        return mono.delayUntil(this::validate)
                .flatMap(req -> addChatMessageUseCase.execute(AddChatMessageUseCase.Input.builder().roomId(req.getRoomId())
                        .chatMessageType(req.getType())
                        .content(req.getContent())
                        .build(), output))
                .doOnSuccess(v -> chatRoomManager.broadcastMessage(output.getChatMessage()))
                .flatMap(v -> Mono.fromSupplier(() -> ChatMessageMapper.domainToRes(output.getChatMessage())));
    }

    @MessageMapping("chat.message.update.{messageId}")
    public Mono<ChatMessageRes> updateMessage(@DestinationVariable String messageId,
                                              @Payload Mono<UpdateChatMessageReq> reqMono) {
        UpdateChatMessageUseCase.Output output = new UpdateChatMessageUseCase.Output();
        return reqMono.delayUntil(this::validate)
                .flatMap(req -> updateChatMessageUseCase.execute(UpdateChatMessageUseCase.Input.builder()
                        .messageId(messageId)
                        .newContent(req.getNewContent())
                        .build(), output))
                .doOnSuccess(s -> chatRoomManager.broadcastMessage(output.getNewChatMessage()))
                .flatMap(v -> Mono.fromSupplier(() -> ChatMessageMapper.domainToRes(output.getNewChatMessage())));
    }

    @MessageMapping("chat.message.delete.{messageId}")
    public Mono<ChatMessageRes> deleteMessage(@DestinationVariable String messageId) {
        DeleteChatMessageUseCase.Output output = new DeleteChatMessageUseCase.Output();
        return deleteChatMessageUseCase.execute(new DeleteChatMessageUseCase.Input(messageId), output)
                .doOnSuccess(s -> chatRoomManager.broadcastMessage(output.getNewChatMessage()))
                .flatMap(v -> Mono.fromSupplier(() -> ChatMessageMapper.domainToRes(output.getNewChatMessage())));
    }

    @MessageMapping("chat.message.read.{messageId}")
    public Mono<ChatMessageReadLastResDTO> readMessage(@DestinationVariable String messageId) {
        UpdateChatRoomMemberLastReadMessageUseCase.Input input = new UpdateChatRoomMemberLastReadMessageUseCase.Input(messageId);
        UpdateChatRoomMemberLastReadMessageUseCase.Output output = new UpdateChatRoomMemberLastReadMessageUseCase.Output();
        return updateChatRoomMemberLastReadMessageUseCase.execute(input, output)
                .flatMap(v -> Mono.fromSupplier(() -> ChatMessageReadLastResDTO.builder().readChatMessageId(output.getChatMessageLastRead().getLastMessageId()).build()));
    }

    @MessageMapping("chat.history.get.{roomId}")
    public Flux<ChatMessageRes> getHistory(@DestinationVariable String roomId,
                                           @Payload Mono<GetMessageReqDTO> mono) {
        FindHistoryMessageUseCase.Output output = new FindHistoryMessageUseCase.Output();
        return mono.delayUntil(this::validate).
                flatMap(req -> findHistoryMessageUseCase.execute(FindHistoryMessageUseCase.Input.builder()
                        .roomId(roomId)
                        .size(req.getSize())
                        .page(req.getPage())
                        .build(), output))
                .thenMany(Flux.defer(() -> output.getMessageFlux().map(ChatMessageMapper::domainToRes)));
    }

    private <T> Mono<Void> validate(T body) {
        Set<ConstraintViolation<T>> errors = validator.validate(body);
        return errors.isEmpty()
                ? Mono.empty()
                : Mono.error(() -> new ConstraintViolationException(errors));
    }

    // 該版本需要放置在 @Controller 中才會被掃瞄到
    @MessageExceptionHandler(Exception.class)
    public Mono<Void> handleException(Exception ex) throws JsonProcessingException {
        ProblemDetail pd = ExceptionHandlerUtils.createProblemDetail(ex, MDC.get(mdcProperties.getTraceId()));
        return Mono.error(new ApplicationErrorException(objectMapper.writeValueAsString(pd)));
    }
}
