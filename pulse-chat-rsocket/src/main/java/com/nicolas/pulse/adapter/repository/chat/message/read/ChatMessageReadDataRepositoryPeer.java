package com.nicolas.pulse.adapter.repository.chat.message.read;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ChatMessageReadDataRepositoryPeer extends R2dbcRepository<ChatMessageReadData, String> {
    Mono<ChatMessageReadData> findFirstByRoomIdAndMemberIdOrderByCreatedAtDesc(String roomId, String memberId);

    Mono<Boolean> existsByMessageIdAndRoomIdAndMemberId(String messageId, String roomId, String memberId);

    Mono<Void> deleteByRoomId(String roomId);

    Mono<Void> deleteByMemberId(String memberId);
}
