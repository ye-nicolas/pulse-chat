package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository {
    Flux<ChatRoom> findAll();

    Mono<ChatRoom> findById(String id);

    Mono<ChatRoom> create(ChatRoom account);

    Mono<ChatRoom> update(ChatRoom account);

    Mono<Void> deleteById(String id);
}
