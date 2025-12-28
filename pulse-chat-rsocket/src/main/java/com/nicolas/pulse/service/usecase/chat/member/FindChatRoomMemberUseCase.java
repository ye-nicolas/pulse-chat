package com.nicolas.pulse.service.usecase.chat.member;

import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
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
public class FindChatRoomMemberUseCase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public FindChatRoomMemberUseCase(ChatRoomRepository chatRoomRepository,
                                     ChatRoomMemberRepository chatRoomMemberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
    }

    public Mono<Void> execute(Input input, Output output) {
        return validateRoomIsExists(input.getRoomId())
                .then(validateGetAllow(input.getRoomId()))
                .then(Mono.fromRunnable(() -> output.setChatRoomMemberFlux(chatRoomMemberRepository.findAllByRoomId(input.getRoomId()))));
    }

    public Mono<Void> validateRoomIsExists(String roomId) {
        return chatRoomRepository.existsById(roomId)
                .flatMap(bol -> bol
                        ? Mono.empty()
                        : Mono.error(() -> new TargetNotFoundException("Chat room not found, room id = '%s'.".formatted(roomId))));
    }

    public Mono<Void> validateGetAllow(String roomId) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> chatRoomMemberRepository.existsByAccountIdAndRoomId(accountId, roomId))
                .flatMap(bol -> bol
                        ? Mono.empty()
                        : Mono.error(() -> new AccessDeniedException("Not allow get chat room member, room id = '%s'.".formatted(roomId))));
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String roomId;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private Flux<ChatRoomMember> chatRoomMemberFlux;
    }
}
