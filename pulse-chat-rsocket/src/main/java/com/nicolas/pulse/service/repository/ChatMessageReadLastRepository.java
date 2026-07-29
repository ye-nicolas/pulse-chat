package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatMessageLastRead;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatMessageReadLastRepository {
    Flux<ChatMessageLastRead> findAllByRoomId(String roomId);

    Mono<ChatMessageLastRead> findByRoomIdAndMemberId(String roomId, String memberId);

    Mono<ChatMessageLastRead> save(ChatMessageLastRead messageRead);

    Mono<Void> deleteByRoomId(String roomId);

    Mono<Void> deleteByMemberId(String memberId);
}
