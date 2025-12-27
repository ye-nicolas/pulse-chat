package com.nicolas.pulse.service.usecase.chat.message;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;


@Service
public class AddChatMessageUseCase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    public AddChatMessageUseCase(ChatRoomRepository chatRoomRepository,
                                 ChatRoomMemberRepository chatRoomMemberRepository,
                                 ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional
    public Mono<Void> execute(Input input, Output output) {
        return getChatRoom(input.getRoomId())
                .flatMap(chatRoom -> getChatMember(chatRoom.getId()))
                .flatMap(chatRoomMember -> createChatMessage(chatRoomMember, input.getContent(), input.chatMessageType))
                .doOnNext(output::setChatMessage)
                .then();
    }

    private Mono<ChatMessage> createChatMessage(ChatRoomMember chatRoomMember, String content, ChatMessageType messageType) {
        return chatMessageRepository.save(ChatMessage.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .memberId(chatRoomMember.getId())
                .roomId(chatRoomMember.getChatRoom().getId())
                .content(content)
                .type(messageType)
                .build());
    }

    private Mono<ChatRoom> getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Room not found, id = '%s'.".formatted(roomId))));
    }

    private Mono<ChatRoomMember> getChatMember(String roomId) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(id -> chatRoomMemberRepository.findByAccountAndRoomId(id, roomId))
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Account is not a member of chat room, room id = '%s'.".formatted(roomId))));
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String roomId;
        private String content;
        private ChatMessageType chatMessageType;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private ChatMessage chatMessage;
    }
}
