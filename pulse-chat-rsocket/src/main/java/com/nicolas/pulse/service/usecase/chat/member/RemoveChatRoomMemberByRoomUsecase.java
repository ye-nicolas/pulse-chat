package com.nicolas.pulse.service.usecase.chat.member;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageReadLastRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RemoveChatRoomMemberByRoomUsecase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageReadLastRepository chatMessageReadLastRepository;

    public RemoveChatRoomMemberByRoomUsecase(ChatRoomRepository chatRoomRepository,
                                             ChatRoomMemberRepository chatRoomMemberRepository,
                                             ChatMessageReadLastRepository chatMessageReadLastRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageReadLastRepository = chatMessageReadLastRepository;
    }

    @Transactional
    public Mono<Void> execute(Input input, Output output) {
        return getChatRoom(input.getRoomId())
                .delayUntil(this::validateDeleteMemberAllow)
                .flatMap(room -> this.getChatRoomMemberAccountId(room, input.getDeleteMemberIdSet())
                        .map(ChatRoomMember::getAccountId)
                        .collectList())
                .filter(list -> !list.isEmpty())
                .flatMap(accountIdList -> this.deleteChatRoomMember(input.getDeleteMemberIdSet()).thenReturn(accountIdList))
                .doOnNext(output::setDeleteAccountIdList)
                .then();
    }

    private Mono<Void> deleteChatRoomMember(Set<String> deleteMemberIdList) {
        return Flux.fromIterable(deleteMemberIdList)
                .flatMap(memberId -> chatRoomMemberRepository.deleteById(memberId)
                        .then(chatMessageReadLastRepository.deleteByMemberId(memberId)))
                .then();
    }

    private Mono<ChatRoom> getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Chat room not found, room id = '%s'.".formatted(roomId))));
    }

    private Flux<ChatRoomMember> getChatRoomMemberAccountId(ChatRoom room, Set<String> memberIdSet) {
        return Flux.fromIterable(memberIdSet)
                .flatMap(memberId -> chatRoomMemberRepository.findById(memberId)
                        .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Member not found by '%s', member id = '%s'.".formatted(room.getName(), memberId)))));
    }

    private Mono<Void> validateDeleteMemberAllow(ChatRoom room) {
        return SecurityUtil.getCurrentAccountId()
                .flatMap(accountId -> chatRoomMemberRepository.existsByAccountIdAndRoomId(accountId, room.getId())
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Not allow delete chat room member, room id = '%s'.".formatted(room.getId()))))
                        .then());
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String roomId;
        private Set<String> deleteMemberIdSet;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private List<String> deleteAccountIdList;
    }
}
