package com.nicolas.pulse.service.usecase.chat.member;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatMessageReadLastRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import com.nicolas.pulse.service.usecase.sink.ChatRoomManager;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class RemoveChatRoomMemberByRoomUsecase {
    private final ChatRoomManager chatRoomManager;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageReadLastRepository chatMessageReadLastRepository;

    public RemoveChatRoomMemberByRoomUsecase(ChatRoomManager chatRoomManager,
                                             ChatRoomRepository chatRoomRepository,
                                             ChatRoomMemberRepository chatRoomMemberRepository,
                                             ChatMessageReadLastRepository chatMessageReadLastRepository) {
        this.chatRoomManager = chatRoomManager;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.chatMessageReadLastRepository = chatMessageReadLastRepository;
    }

    public Mono<Void> execute(Input input) {
        return getChatRoom(input.getRoomId())
                .flatMap(room -> Mono.when(
                        this.validateDeleteMemberAllow(room),
                        this.validateMemberIsExists(room, input.getDeleteMemberIdList())))
                .then(this.deleteChatRoomMember(input.getDeleteMemberIdList()))
                .then(Mono.fromRunnable(() -> kickOutChatRoomMembers(input.getDeleteMemberIdList(), input.getRoomId())))
                .then();
    }

    private Mono<Void> deleteChatRoomMember(Set<String> deleteMemberIdList) {
        return Flux.fromIterable(deleteMemberIdList)
                .flatMap(memberId -> chatRoomMemberRepository.deleteById(memberId)
                        .then(chatMessageReadLastRepository.deleteByMemberId(memberId)))
                .then();
    }

    private void kickOutChatRoomMembers(Set<String> deleteMemberIdList, String roomId) {
        deleteMemberIdList.forEach(accountId -> chatRoomManager.kickOutAccount(roomId, accountId));
    }

    private Mono<ChatRoom> getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Chat Room not found, room id = '%s'.".formatted(roomId))));
    }

    private Mono<Void> validateMemberIsExists(ChatRoom room, Set<String> deleteMemberIdList) {
        return Flux.fromIterable(deleteMemberIdList)
                .flatMap(memberId -> chatRoomMemberRepository.existsByIdAndRoomId(memberId, room.getId())
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Member not found by '%s', member id = '%s'.".formatted(room.getName(), memberId))))
                        .then())
                .then();
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
        private Set<String> deleteMemberIdList;
    }
}
