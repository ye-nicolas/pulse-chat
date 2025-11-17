package com.nicolas.pulse.adapter.repository.chat.room.member;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ChatRoomMemberDataRepositoryPeer extends R2dbcRepository<ChatRoomMemberData, String> {
    Mono<Boolean> existsByAccountIdAndRoomId(String accountId, String roomId);

    Mono<Void> deleteByAccountId(String accountId);
}
