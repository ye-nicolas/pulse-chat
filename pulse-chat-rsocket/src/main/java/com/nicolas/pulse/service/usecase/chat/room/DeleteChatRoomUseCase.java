package com.nicolas.pulse.service.usecase.chat.room;

import com.nicolas.pulse.entity.event.DeleteRoomEvent;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageReadLastRepository;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.service.usecase.sink.ChatEventBus;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class DeleteChatRoomUseCase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageReadLastRepository chatMessageReadLastRepository;

    public DeleteChatRoomUseCase(ChatRoomRepository chatRoomRepository,
                                 ChatRoomMemberRepository chatRoomMemberRepository,
                                 ChatMessageRepository chatMessageRepository,
                                 ChatMessageReadLastRepository chatMessageReadLastRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageReadLastRepository = chatMessageReadLastRepository;
    }

    @Transactional
    public Mono<Void> execute(Input input) {
        return validateRoomIsExists(input.roomId)
                .then(validateDeleteAllow(input.getRoomId()))
                .then(Mono.when(chatMessageReadLastRepository.deleteByRoomId(input.getRoomId()),
                        chatMessageRepository.deleteByRoomId(input.getRoomId()),
                        chatRoomMemberRepository.deleteByRoomId(input.getRoomId()),
                        chatRoomRepository.deleteById(input.getRoomId())));
    }

    private Mono<Void> validateRoomIsExists(String roomId) {
        return chatRoomRepository.existsById(roomId)
                .flatMap(bol -> bol
                        ? Mono.empty()
                        : Mono.error(() -> new TargetNotFoundException("Chat room not found, room id = '%s'.".formatted(roomId))));
    }

    private Mono<Void> validateDeleteAllow(String roomId) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> chatRoomMemberRepository.existsByAccountIdAndRoomId(accountId, roomId))
                .flatMap(bol -> bol
                        ? Mono.empty()
                        : Mono.error(() -> new AccessDeniedException("Not allow delete chat room, room id = '%s'.".formatted(roomId))));
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String roomId;
    }
}
