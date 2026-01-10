package com.nicolas.pulse.service.usecase.chat.room;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FindChatRoomByIdUseCase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public FindChatRoomByIdUseCase(ChatRoomRepository chatRoomRepository,
                                   ChatRoomMemberRepository chatRoomMemberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
    }

    public Mono<Void> execute(Input input, Output output) {
        return getChatRoom(input.getRoomId())
                .delayUntil(chatRoom -> validateGetAllow(chatRoom.getId()))
                .doOnNext(output::setChatRoom)
                .then();
    }

    private Mono<ChatRoom> getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Chat room not found, room id = '%s'.".formatted(roomId))));
    }

    private Mono<Void> validateGetAllow(String roomId) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> chatRoomMemberRepository.existsByAccountIdAndRoomId(accountId, roomId))
                .flatMap(bol -> bol
                        ? Mono.empty()
                        : Mono.error(() -> new AccessDeniedException("Not allow get chat room, room id = '%s'.".formatted(roomId))));
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String roomId;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private ChatRoom chatRoom;
    }
}
