package com.nicolas.pulse.adapter.repository.chat.message;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatMessageDataRepositoryPeer extends R2dbcRepository<ChatMessageData, String> {
    Flux<ChatMessageData> findByRoomIdAndMemberId(String roomId, String memberId);

    Mono<Void> deleteByRoomId(String roomId);
}
