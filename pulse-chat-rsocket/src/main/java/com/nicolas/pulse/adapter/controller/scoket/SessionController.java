package com.nicolas.pulse.adapter.controller.scoket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolas.pulse.adapter.dto.mapper.ChatMessageMapper;
import com.nicolas.pulse.adapter.dto.res.ChatMessageRes;
import com.nicolas.pulse.infrastructure.config.MdcProperties;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import com.nicolas.pulse.service.usecase.sink.SubscribeChatRoomUseCase;
import com.nicolas.pulse.util.ExceptionHandlerUtils;
import io.rsocket.exceptions.ApplicationErrorException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class SessionController {
    private final ChatRoomManager chatRoomManager;
    private final SubscribeChatRoomUseCase subscribeChatRoomUseCase;
    private final ObjectMapper objectMapper;
    private final MdcProperties mdcProperties;

    public SessionController(ChatRoomManager chatRoomManager,
                             SubscribeChatRoomUseCase subscribeChatRoomUseCase,
                             ObjectMapper objectMapper,
                             MdcProperties mdcProperties) {
        this.chatRoomManager = chatRoomManager;
        this.subscribeChatRoomUseCase = subscribeChatRoomUseCase;
        this.objectMapper = objectMapper;
        this.mdcProperties = mdcProperties;
    }

    @MessageMapping("session.open.room.{roomId}")
    public Flux<ChatMessageRes> openSessionByRoom(@DestinationVariable("roomId") String roomId) {
        SubscribeChatRoomUseCase.Input input = new SubscribeChatRoomUseCase.Input(roomId);
        SubscribeChatRoomUseCase.Output output = new SubscribeChatRoomUseCase.Output();
        return subscribeChatRoomUseCase.execute(input, output)
                .thenMany(Flux.defer(output::getChatMessageFlux))
                .map(ChatMessageMapper::domainToRes)
                .doFinally(signalType -> chatRoomManager.unSubscribe(output.getAccountId(), roomId));
    }

    @MessageExceptionHandler(Exception.class)
    public Mono<Void> handleException(Exception ex) throws JsonProcessingException {
        ProblemDetail pd = ExceptionHandlerUtils.createProblemDetail(ex, MDC.get(mdcProperties.getTraceId()));
        return Mono.error(new ApplicationErrorException(objectMapper.writeValueAsString(pd)));
    }
}
