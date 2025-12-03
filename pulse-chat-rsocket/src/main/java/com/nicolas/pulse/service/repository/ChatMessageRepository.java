package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatMessageRepository {
    Mono<ChatMessage> findById(String id);

    Flux<ChatMessage> findByRoomIdAndMemberId(String roomId, String memberId);

    Mono<ChatMessage> save(ChatMessage chatMessage);

    Mono<Void> deleteByRoomId(String roomId);
}
