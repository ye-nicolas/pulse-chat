package com.nicolas.pulse.service.usecase.chat;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class RemoveChatRoomMemberByRoomUsecase {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public RemoveChatRoomMemberByRoomUsecase(ChatRoomRepository chatRoomRepository,
                                             ChatRoomMemberRepository chatRoomMemberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
    }

    public Mono<Void> execute(Input input) {
        return getChatRoom(input.getRoomId())
                .flatMap(room -> this.validateMemberIdExistsByRoom(room, input.getDeleteMemberIdList()))
                .thenMany(Flux.fromIterable(input.deleteMemberIdList).flatMap(chatRoomMemberRepository::deleteById))
                .then();
    }

    private Mono<ChatRoom> getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Chat Room not found, room id = '%s'.".formatted(roomId))));
    }

    private Mono<Void> validateMemberIdExistsByRoom(ChatRoom room, Set<String> deleteMemberIdList) {
        return Flux.fromIterable(deleteMemberIdList)
                .flatMap(memberId -> chatRoomMemberRepository.existsByIdAndRoomId(memberId, room.getId())
                        .flatMap(bol -> bol ? Mono.empty() :
                                Mono.error(new TargetNotFoundException("Member not found by '%s', member id = '%s'.".formatted(room.getName(), memberId)))))
                .then();
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String roomId;
        private Set<String> deleteMemberIdList;
    }
}
