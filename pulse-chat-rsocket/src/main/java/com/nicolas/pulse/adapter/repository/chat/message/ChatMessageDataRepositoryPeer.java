package com.nicolas.pulse.adapter.repository.chat.message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatMessageDataRepositoryPeer extends R2dbcRepository<ChatMessageData, String> {
    Flux<ChatMessageData> findAllByRoomId(String roomId, Pageable pageable);

    Mono<Void> deleteByRoomId(String roomId);
}
