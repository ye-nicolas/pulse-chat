package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatMessageRead;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatMessageReadRepository {
    Mono<ChatMessageRead> findFirstByRoomIdAndMemberIdOrderByCreatedAtDesc(String roomId, String memberId);

    Mono<ChatMessageRead> save(ChatMessageRead messageRead);

    Flux<ChatMessageRead> saveAll(List<ChatMessageRead> chatMessageReadFlux);

    Mono<Boolean> existsByMessageIdAndRoomIdAndMemberId(String messageId, String roomId, String memberId);

    Mono<Void> deleteByRoomId(String roomId);

    Mono<Void> deleteByMemberId(String memberId);
}
