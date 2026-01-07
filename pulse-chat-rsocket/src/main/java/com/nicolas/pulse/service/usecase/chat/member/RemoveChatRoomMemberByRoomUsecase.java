package com.nicolas.pulse.service.usecase.chat.member;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.event.DeleteMemberEvent;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageReadLastRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
public class RemoveChatRoomMemberByRoomUsecase {
    private final ChatRoomManager chatRoomManager;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageReadLastRepository chatMessageReadLastRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RemoveChatRoomMemberByRoomUsecase(ChatRoomManager chatRoomManager,
                                             ChatRoomRepository chatRoomRepository,
                                             ChatRoomMemberRepository chatRoomMemberRepository,
                                             ChatMessageReadLastRepository chatMessageReadLastRepository,
                                             ApplicationEventPublisher applicationEventPublisher) {
        this.chatRoomManager = chatRoomManager;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageReadLastRepository = chatMessageReadLastRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public Mono<Void> execute(Input input) {
        return getChatRoom(input.getRoomId())
                .delayUntil(this::validateDeleteMemberAllow)
                .flatMap(room -> this.getChatRoomMemberAccountId(room, input.getDeleteMemberIdSet())
                        .map(ChatRoomMember::getAccountId)
                        .collectList())
                .filter(list -> !list.isEmpty())
                .flatMap(accountIdList -> this.deleteChatRoomMember(input.getDeleteMemberIdSet()).thenReturn(accountIdList))
                .doOnSuccess(accountIdList -> applicationEventPublisher.publishEvent(new DeleteMemberEvent(input.getRoomId(), new HashSet<>(accountIdList))))
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
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Chat Room not found, room id = '%s'.".formatted(roomId))));
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
                        .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Account '%s' is not a member of ChatRoom '%s'.".formatted(accountId, room.getId()))))
                        .then());
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String roomId;
        private Set<String> deleteMemberIdSet;
    }
}
