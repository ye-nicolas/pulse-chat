package com.nicolas.pulse.service.usecase.chat.message.read;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.entity.domain.chat.ChatMessageLastRead;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageReadLastRepository;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UpdateChatRoomMemberLastReadMessageUseCase {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageReadLastRepository chatMessageReadLastRepository;

    public UpdateChatRoomMemberLastReadMessageUseCase(ChatRoomMemberRepository chatRoomMemberRepository,
                                                      ChatMessageRepository chatMessageRepository,
                                                      ChatMessageReadLastRepository chatMessageReadLastRepository) {
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageReadLastRepository = chatMessageReadLastRepository;
    }

    public Mono<Void> execute(Input input, Output output) {
        return getMessage(input.getMessageId())
                .flatMap(chatMessage -> getMember(chatMessage.getRoomId()))
                .flatMap(chatRoomMember -> this.saveChatMessageRead(input.getMessageId(), chatRoomMember))
                .doOnNext(output::setChatMessageLastRead)
                .then();
    }

    private Mono<ChatMessageLastRead> saveChatMessageRead(String messageId, ChatRoomMember chatRoomMember) {
        return chatMessageReadLastRepository.findByLastMessageIdAndRoomIdAndMemberId(messageId, chatRoomMember.getChatRoom().getId(), chatRoomMember.getId())
                .switchIfEmpty(Mono.fromSupplier(() -> ChatMessageLastRead.builder()
                        .id(UlidCreator.getMonotonicUlid().toString())
                        .roomId(chatRoomMember.getChatRoom().getId())
                        .lastMessageId(messageId)
                        .memberId(chatRoomMember.getId())
                        .build()))
                .filter(chatMessageLastRead -> messageId.compareTo(chatMessageLastRead.getLastMessageId()) > 0)
                .doOnNext(chatMessageLastRead -> chatMessageLastRead.setLastMessageId(messageId))
                .flatMap(chatMessageReadLastRepository::save);
    }

    private Mono<ChatRoomMember> getMember(String roomId) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> chatRoomMemberRepository.findByAccountAndRoomId(accountId, roomId)
                        .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Account is not a member of the requested room, account id = '%s'.".formatted(accountId)))));
    }

    private Mono<ChatMessage> getMessage(String messageId) {
        return chatMessageRepository.findById(messageId)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Message not found, message id = '%s'.")));
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String messageId;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private ChatMessageLastRead chatMessageLastRead;
    }
}
