package com.nicolas.pulse.service.usecase.chat.member;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class AddChatRoomMemberUseCase {
    public final AccountRepository accountRepository;
    public final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public AddChatRoomMemberUseCase(AccountRepository accountRepository,
                                    ChatRoomRepository chatRoomRepository,
                                    ChatRoomMemberRepository chatRoomMemberRepository) {
        this.accountRepository = accountRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
    }

    public Mono<Void> execute(Input input) {
        return getChatRoom(input.getRoomId())
                .delayUntil(chatRoom -> Mono.when(
                        validateAddMemberAllow(chatRoom),
                        validateAccountExists(input.getAccountIdSet()),
                        validateAccountExistsInRoom(chatRoom, input.getAccountIdSet())))
                .flatMap(chatRoom -> addChatMember(chatRoom, input.getAccountIdSet()));
    }

    private Mono<Void> addChatMember(ChatRoom chatRoom, Set<String> accountIdSet) {
        return chatRoomMemberRepository.saveAll(accountIdSet.stream()
                        .map(accountId -> ChatRoomMember.builder()
                                .id(UlidCreator.getMonotonicUlid().toString())
                                .chatRoom(chatRoom)
                                .accountId(accountId)
                                .isMuted(false)
                                .isPinned(false)
                                .build())
                        .toList())
                .then();
    }

    private Mono<Void> validateAddMemberAllow(ChatRoom chatRoom) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> chatRoomMemberRepository.existsByAccountIdAndRoomId(accountId, chatRoom.getId())
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Not allow add new member, room id = '%s'.".formatted(chatRoom.getId()))))
                        .then());
    }

    private Mono<Void> validateAccountExistsInRoom(ChatRoom chatRoom, Set<String> accountIdSet) {
        return Flux.fromIterable(accountIdSet)
                .flatMap(accountId -> chatRoomMemberRepository.existsByAccountIdAndRoomId(accountId, chatRoom.getId())
                        .filter(exists -> !exists)
                        .switchIfEmpty(Mono.error(() -> new ConflictException("Account is exists in room, account id = '%s'.".formatted(accountId))))
                        .then())
                .then();
    }

    private Mono<Void> validateAccountExists(Set<String> accountIdSet) {
        return Flux.fromIterable(accountIdSet)
                .flatMap(id -> accountRepository.existsById(id)
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Account not found, account id = '%s'.".formatted(id))))
                        .then())
                .then();
    }

    private Mono<ChatRoom> getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Chat Room not found, room id = '%s'.".formatted(roomId))));
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Input {
        private String roomId;
        private Set<String> accountIdSet;
    }
}
