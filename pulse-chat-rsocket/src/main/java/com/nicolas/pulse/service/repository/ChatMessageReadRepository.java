package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatMessageRead;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatMessageReadRepository {
    Mono<ChatMessageRead> findFirstByRoomIdAndMemberIdOrderByCreatedAtDesc(String roomId, String memberId);

    Mono<ChatMessageRead> insert(ChatMessageRead messageRead);

    Flux<ChatMessageRead> insert(Flux<ChatMessageRead> chatMessageReadFlux);

    Mono<Void> deleteByRoomId(String roomId);

    Mono<Boolean> existsByMessageIdAndRoomIdAndMemberId(String messageId, String roomId, String memberId);
}
