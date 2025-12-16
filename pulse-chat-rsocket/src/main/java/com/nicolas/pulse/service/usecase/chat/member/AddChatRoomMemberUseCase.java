package com.nicolas.pulse.service.usecase.chat.member;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
        return validateAccountExists(input.getAccountIdSet())
                .then(getChatRoom(input.getRoomId()))
                .flatMap(chatRoom -> validateAccountExistsInRoom(chatRoom, input.getAccountIdSet())
                        .thenEmpty(addChatMember(chatRoom, input.getAccountIdSet()))
                );
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

    private Mono<Void> validateAccountExistsInRoom(ChatRoom chatRoom, Set<String> accountIdSet) {
        return Flux.fromIterable(accountIdSet)
                .flatMap(id -> chatRoomMemberRepository.existsByIdAndRoomId(chatRoom.getId(), id)
                        .filter(exists -> !exists)
                        .switchIfEmpty(Mono.error(new ConflictException("Account is exists in room, account id = '%s'.".formatted(id))))
                        .then())
                .then();
    }

    private Mono<Void> validateAccountExists(Set<String> accountIdSet) {
        return Flux.fromIterable(accountIdSet)
                .flatMap(id -> accountRepository.existsById(id)
                        .filter(exists -> exists)
                        .switchIfEmpty(Mono.error(new TargetNotFoundException("Account not found, account id = '%s'.".formatted(id))))
                        .then())
                .then();
    }

    private Mono<ChatRoom> getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Chat Room not found, room id = '%s'.".formatted(roomId))));
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
