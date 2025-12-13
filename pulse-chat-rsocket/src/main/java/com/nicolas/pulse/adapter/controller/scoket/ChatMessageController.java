package com.nicolas.pulse.adapter.controller.scoket;

import com.nicolas.pulse.adapter.dto.mapper.ChatMessageMapper;
import com.nicolas.pulse.adapter.dto.req.AddChatMessageReq;
import com.nicolas.pulse.adapter.dto.req.UpdateChatMessageReq;
import com.nicolas.pulse.adapter.dto.res.ChatMessageRes;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.service.usecase.chat.message.AddChatMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.DeleteChatMessageUseCase;
import com.nicolas.pulse.service.usecase.chat.message.UpdateChatMessageUseCase;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomManager chatRoomManager;
    private final AddChatMessageUseCase addChatMessageUseCase;
    private final UpdateChatMessageUseCase updateChatMessageUseCase;
    private final DeleteChatMessageUseCase deleteChatMessageUseCase;


    public ChatMessageController(Validator validator, ChatMessageRepository
                                         chatMessageRepository,
                                 ChatRoomManager chatRoomManager,
                                 AddChatMessageUseCase addChatMessageUseCase,
                                 UpdateChatMessageUseCase updateChatMessageUseCase,
                                 DeleteChatMessageUseCase deleteChatMessageUseCase) {
        this.validator = validator;
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomManager = chatRoomManager;
        this.addChatMessageUseCase = addChatMessageUseCase;
        this.updateChatMessageUseCase = updateChatMessageUseCase;
        this.deleteChatMessageUseCase = deleteChatMessageUseCase;
    }

    @MessageMapping("chat.message.add")
    public Mono<Void> addMessage(@Payload Mono<AddChatMessageReq> mono) {
        AddChatMessageUseCase.Output output = new AddChatMessageUseCase.Output();
        return mono.flatMap(this::validate)
                .flatMap(req1 -> addChatMessageUseCase.execute(AddChatMessageUseCase.Input.builder().roomId(req1.getRoomId())
                        .chatMessageType(req1.getType())
                        .content(req1.getContent())
                        .build(), output))
                .doOnSuccess(s -> chatRoomManager.broadcastMessage(output.getChatMessage()));
    }

    @MessageMapping("chat.message.update.{messageId}")
    public Mono<Void> updateMessage(@DestinationVariable String messageId,
                                    @Payload Mono<UpdateChatMessageReq> mono) {
        UpdateChatMessageUseCase.Output output = new UpdateChatMessageUseCase.Output();
        return mono.flatMap(this::validate)
                .flatMap(req -> updateChatMessageUseCase.execute(UpdateChatMessageUseCase.Input.builder()
                        .messageId(messageId)
                        .newContent(req.getNewContent())
                        .build(), output))
                .doOnSuccess(s -> chatRoomManager.broadcastMessage(output.getNewChatMessage()));
    }

    @MessageMapping("chat.message.delete.{messageId}")
    public Mono<Void> deleteMessage(@DestinationVariable String messageId) {
        DeleteChatMessageUseCase.Input input = new DeleteChatMessageUseCase.Input(messageId);
        DeleteChatMessageUseCase.Output output = new DeleteChatMessageUseCase.Output();
        return deleteChatMessageUseCase.execute(input, output)
                .doOnSuccess(s -> chatRoomManager.broadcastMessage(output.getNewChatMessage()));
    }

    @MessageMapping("chat.history.get.{roomId}")
    public Flux<ChatMessageRes> getHistory(@DestinationVariable String roomId) {
        return chatMessageRepository.findAllByRoomId(roomId)
                .map(ChatMessageMapper::domainToRes);
    }

    private <T> Mono<T> validate(T body) {
        Set<ConstraintViolation<T>> errors = validator.validate(body);
        if (!errors.isEmpty()) {
            return Mono.error(new ConstraintViolationException(errors));
        }
        return Mono.just(body);
    }
}
