package com.nicolas.pulse.adapter.repository.chat.message.read;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ChatMessageReadDataRepositoryPeer extends R2dbcRepository<ChatMessageLastReadData, String> {
    Mono<ChatMessageLastReadData> findByLastMessageIdAndRoomIdAndMemberId(String lastMessageId, String roomId, String memberId);

    Mono<Boolean> existsByLastMessageIdAndRoomIdAndMemberId(String lastMessageId, String roomId, String memberId);

    Mono<Void> deleteByRoomId(String roomId);

    Mono<Void> deleteByMemberId(String memberId);
}
