package com.nicolas.pulse.service.usecase.chat;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.enumerate.ChatRoomMemberRole;
import com.nicolas.pulse.entity.enumerate.ChatRoomType;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
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

    public Mono<Void> execute(Input input, Output output) {
        return Mono.when(validateAccountIdSetExists(input.getAccountIdSet()),
                        validateMemberCountForRoomType(input.roomType, input.getAccountIdSet()))
                .then(createChatRoom(input))
                .doOnSuccess(chatRoom -> output.setRoomId(chatRoom.getId()))
                .flatMap(chatRoom -> createChatRoomMember(chatRoom, input.getAccountIdSet()));
    }

    private Mono<Void> validateMemberCountForRoomType(ChatRoomType roomType, Set<String> accountIdSet) {
        if (roomType.equals(ChatRoomType.PRIVATE) && accountIdSet.size() > 2) {
            return Mono.error(new IllegalArgumentException("Private chat rooms can only contain two members."));
        }
        return Mono.empty();
    }

    private Mono<Void> validateAccountIdSetExists(Set<String> accountIdSet) {
        return Flux.fromIterable(accountIdSet)
                .flatMap(id -> accountRepository.existsById(id)
                        .filter(res -> res)
                        .switchIfEmpty(Mono.error(new AccountNotFoundException("Account with ID " + id + " does not exist."))))
                .then();
    }

    private Mono<ChatRoom> createChatRoom(Input input) {
        return chatRoomRepository.save(ChatRoom.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .name(input.getRoomName())
                .type(input.getRoomType())
                .build());
    }

    private Mono<Void> createChatRoomMember(ChatRoom chatRoom, Set<String> accountIdSet) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> chatRoomMemberRepository
                        .saveAll(accountIdSet.stream()
                                .map(id -> ChatRoomMember.builder()
                                        .id(UlidCreator.getMonotonicUlid().toString())
                                        .chatRoom(chatRoom)
                                        .accountId(id)
                                        .role(accountId.equals(id) ? ChatRoomMemberRole.OWNER : ChatRoomMemberRole.MEMBER)
                                        .isMuted(false)
                                        .isPinned(false)
                                        .build())
                                .toList()
                        ).then()
                );
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String roomName;
        private ChatRoomType roomType;
        private Set<String> accountIdSet;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private String roomId;
    }
}
