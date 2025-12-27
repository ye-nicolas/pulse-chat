package com.nicolas.pulse.service.usecase.sink;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SubscribeChatRoomUseCase {
    private final ChatRoomManager chatRoomManager;
    private final ChatRoomRepository chatRoomRepository;

    public SubscribeChatRoomUseCase(ChatRoomManager chatRoomManager,
                                    ChatRoomRepository chatRoomRepository) {
        this.chatRoomManager = chatRoomManager;
        this.chatRoomRepository = chatRoomRepository;
    }

    public Mono<Void> execute(Input input, Output output) {
        return validateChatRoomExists(input.getRoomId())
                .then(this.validateCanSubscribe(input.getRoomId()))
                .then(SecurityUtil.getCurrentAccountId())
                .doOnSuccess(accountId -> {
                    output.setAccountId(accountId);
                    output.setChatMessageFlux(chatRoomManager.subscribe(accountId, input.getRoomId()).asFlux());
                })
                .then();
    }

    private Mono<Void> validateChatRoomExists(String roomId) {
        return this.chatRoomRepository.existsById(roomId)
                .filter(b -> b)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Chat room not found, room id = '%s'.".formatted(roomId))))
                .then();
    }

    private Mono<Void> validateCanSubscribe(String roomId) {
        return SecurityUtil.getSecurityAccount()
                .map(SecurityAccount::getRoomIdSet)
                .filter(set -> set.contains(roomId))
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Account is not a member of chat room, room id = '%s'".formatted(roomId))))
                .then();
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String roomId;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private Flux<ChatMessage> chatMessageFlux;
        private String accountId;
    }
}
