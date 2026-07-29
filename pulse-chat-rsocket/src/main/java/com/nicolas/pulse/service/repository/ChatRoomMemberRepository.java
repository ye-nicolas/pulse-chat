package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatRoomMemberRepository {
    Mono<ChatRoomMember> findById(String id);

    Mono<ChatRoomMember> findByAccountAndRoomId(String accountId, String roomId);

    Flux<ChatRoomMember> findAllByAccountId(String accountId);

    Flux<ChatRoomMember> findAllByRoomId(String roomId);

    Mono<ChatRoomMember> save(ChatRoomMember chatRoomMember);

    Flux<ChatRoomMember> saveAll(List<ChatRoomMember> chatRoomMemberList);

    Mono<Void> deleteById(String id);

    Mono<Void> deleteByRoomId(String roomId);

    Mono<Void> deleteByAccountId(String accountId);

    Mono<Boolean> existsByAccountIdAndRoomId(String accountId, String roomId);
}
