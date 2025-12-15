package com.nicolas.pulse.service.usecase.chat;

import com.nicolas.pulse.service.repository.ChatMessageReadRepository;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteChatRoomUseCase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageReadRepository chatMessageReadRepository;

    public DeleteChatRoomUseCase(ChatRoomRepository chatRoomRepository,
                                 ChatRoomMemberRepository chatRoomMemberRepository,
                                 ChatMessageRepository chatMessageRepository,
                                 ChatMessageReadRepository chatMessageReadRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageReadRepository = chatMessageReadRepository;
    }

    public Mono<Void> execute(Input input) {
        return chatRoomRepository.existsById(input.getRoomId())
                .filter(f -> f)
                .then(validateDeleteAllow(input.getRoomId()))
                .then(Mono.when(chatMessageReadRepository.deleteByRoomId(input.getRoomId()),
                        chatRoomMemberRepository.deleteByRoomId(input.getRoomId()),
                        chatMessageRepository.deleteByRoomId(input.getRoomId()),
                        chatRoomRepository.deleteById(input.getRoomId())));
    }

    private Mono<Void> validateDeleteAllow(String roomId) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(a -> chatRoomMemberRepository.existsByIdAndRoomId(a, roomId))
                .filter(bol -> bol)
                .switchIfEmpty(Mono.error(new AccessDeniedException("Not allow delete chat room, room id = '%s'.".formatted(roomId))))
                .then();
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String roomId;
    }
}
