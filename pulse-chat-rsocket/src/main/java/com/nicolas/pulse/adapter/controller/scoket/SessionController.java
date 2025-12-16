package com.nicolas.pulse.adapter.controller.scoket;

import com.nicolas.pulse.adapter.dto.mapper.ChatMessageMapper;
import com.nicolas.pulse.adapter.dto.res.ChatMessageRes;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import com.nicolas.pulse.service.usecase.sink.SubscribeChatRoomUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class SessionController {
    private final ChatRoomManager chatRoomManager;
    private final SubscribeChatRoomUseCase subscribeChatRoomUseCase;

    public SessionController(ChatRoomManager chatRoomManager,
                             SubscribeChatRoomUseCase subscribeChatRoomUseCase) {
        this.chatRoomManager = chatRoomManager;
        this.subscribeChatRoomUseCase = subscribeChatRoomUseCase;
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
}
