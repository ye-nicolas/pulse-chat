package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatMessageLastRead;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatMessageReadLastRepository {
    Mono<ChatMessageLastRead> findByRoomIdAndMemberId(String roomId, String memberId);

    Mono<ChatMessageLastRead> save(ChatMessageLastRead messageRead);

    Flux<ChatMessageLastRead> saveAll(List<ChatMessageLastRead> chatMessageLastReadFluxes);

    Mono<Boolean> existsByLastMessageIdAndRoomIdAndMemberId(String messageId, String roomId, String memberId);

    Mono<Void> deleteByRoomId(String roomId);

    Mono<Void> deleteByMemberId(String memberId);
}
