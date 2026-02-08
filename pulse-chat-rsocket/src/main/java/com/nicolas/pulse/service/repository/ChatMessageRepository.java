package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatMessageRepository {
    Mono<ChatMessage> findById(String id);

    Flux<ChatMessage> findAllByRoomId(String roomId, PageRequest pageRequest);

    Mono<ChatMessage> save(ChatMessage chatMessage);

    Mono<Boolean> existsById(String id);

    Mono<Void> deleteByRoomId(String roomId);
}
