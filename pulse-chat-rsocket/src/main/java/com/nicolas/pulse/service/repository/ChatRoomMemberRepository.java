package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRoomMemberRepository {

    Mono<ChatRoomMember> findById(String id);

    Flux<ChatRoomMember> findByAccountId(String accountId);

    Flux<ChatRoomMember> findByRoomId(String roomId);

    Mono<ChatRoomMember> save(ChatRoomMember chatRoomMember);

    Mono<Void> deleteById(String id);

    Mono<Void> deleteByAccountId(String accountId);

    Mono<Boolean> existsByAccountIdAndRoomId(String accountId, String roomId);
}
