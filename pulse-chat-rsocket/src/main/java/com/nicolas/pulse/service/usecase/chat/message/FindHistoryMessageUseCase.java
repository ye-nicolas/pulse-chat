package com.nicolas.pulse.service.usecase.chat.message;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FindHistoryMessageUseCase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    public FindHistoryMessageUseCase(ChatRoomRepository chatRoomRepository,
                                     ChatRoomMemberRepository chatRoomMemberRepository,
                                     ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public void execute(Input input, Output output) {
        output.setMessageFlux(validateRoomIsExists(input.getRoomId())
                .then(validateIsMember(input.getRoomId()))
                .thenMany(chatMessageRepository.findAllByRoomId(input.getRoomId())));
    }

    private Mono<Void> validateRoomIsExists(String roomId) {
        return chatRoomRepository.existsById(roomId)
                .flatMap(bol -> bol
                        ? Mono.empty()
                        : Mono.error(() -> new TargetNotFoundException("Chat room not found, room id = '%s'.".formatted(roomId))));
    }

    private Mono<Void> validateIsMember(String roomId) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> chatRoomMemberRepository.existsByAccountIdAndRoomId(accountId, roomId)
                        .flatMap(bol -> bol
                                ? Mono.empty()
                                : Mono.error(() -> new AccessDeniedException("Can't read message by permission denied, account id = '%s'.".formatted(accountId)))));
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String roomId;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private Flux<ChatMessage> messageFlux;
    }
}
