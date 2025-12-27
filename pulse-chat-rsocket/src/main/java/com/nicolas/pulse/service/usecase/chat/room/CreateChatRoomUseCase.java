package com.nicolas.pulse.service.usecase.chat.room;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Set;

@Service
public class CreateChatRoomUseCase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final AccountRepository accountRepository;

    public CreateChatRoomUseCase(ChatRoomRepository chatRoomRepository,
                                 ChatRoomMemberRepository chatRoomMemberRepository,
                                 AccountRepository accountRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Mono<Void> execute(Input input, Output output) {
        return validateAccountIdSetExists(input.getAccountIdSet())
                .then(createChatRoom(input))
                .doOnNext(chatRoom -> output.setRoomId(chatRoom.getId()))
                .flatMap(chatRoom -> createChatRoomMember(chatRoom, input.getAccountIdSet()));
    }

    private Mono<Void> validateAccountIdSetExists(Set<String> accountIdSet) {
        return Flux.fromIterable(accountIdSet)
                .flatMap(id -> accountRepository.existsById(id)
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Account not found, account id = '%s'.".formatted(id))))
                        .then())
                .then();
    }

    private Mono<ChatRoom> createChatRoom(Input input) {
        return chatRoomRepository.save(ChatRoom.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .name(input.getRoomName())
                .build());
    }

    private Mono<Void> createChatRoomMember(ChatRoom chatRoom, Set<String> accountIdSet) {
        return chatRoomMemberRepository.saveAll(accountIdSet.stream()
                        .map(id -> ChatRoomMember.builder()
                                .id(UlidCreator.getMonotonicUlid().toString())
                                .chatRoom(chatRoom)
                                .accountId(id)
                                .isMuted(false)
                                .isPinned(false)
                                .build())
                        .toList())
                .then();
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String roomName;
        private Set<String> accountIdSet;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private String roomId;
    }
}
