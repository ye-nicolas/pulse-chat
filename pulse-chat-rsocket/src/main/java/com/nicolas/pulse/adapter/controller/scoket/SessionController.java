package com.nicolas.pulse.adapter.controller.scoket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.infrastructure.config.MdcProperties;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Controller
public class SessionController {
    private final ChatRoomManager chatRoomManager;

    public SessionController(ChatRoomManager chatRoomManager) {
        this.chatRoomManager = chatRoomManager;
    }

    @ConnectMapping
    public Mono<Void> onConnect(RSocketRequester requester) {
        return Mono.defer(SecurityUtil::getCurrentAccountId)
                .flatMap(accountId -> {
                    chatRoomManager.registerSession(requester, accountId);
                    Objects.requireNonNull(requester.rsocket()).onClose()
                            .doFinally(signalType -> chatRoomManager.handleUserOffline(requester))
                            .subscribe();
                    return Mono.empty();
                });
    }
}
