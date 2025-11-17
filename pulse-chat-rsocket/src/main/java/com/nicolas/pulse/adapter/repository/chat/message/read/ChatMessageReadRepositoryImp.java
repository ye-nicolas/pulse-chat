package com.nicolas.pulse.adapter.repository.chat.message.read;

import com.nicolas.pulse.entity.domain.chat.ChatMessageRead;
import com.nicolas.pulse.service.repository.ChatMessageReadRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;


@Repository
public class ChatMessageReadRepositoryImp implements ChatMessageReadRepository {
    private final ChatMessageReadDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public ChatMessageReadRepositoryImp(ChatMessageReadDataRepositoryPeer peer, R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Mono<ChatMessageRead> findFirstByRoomIdAndMemberIdOrderByCreatedAtDesc(String roomId, String memberId) {
        return peer.findFirstByRoomIdAndMemberIdOrderByCreatedAtDesc(roomId, memberId).map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Override
    public Mono<ChatMessageRead> insert(ChatMessageRead messageRead) {
        messageRead.setCreatedAt(Instant.now());
        ChatMessageReadData chatMessageReadData = ChatMessageReadDataMapper.domainToData(messageRead);
        return r2dbcEntityOperations.insert(chatMessageReadData).map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatMessageRead> insert(Flux<ChatMessageRead> chatMessageReadFlux) {
        return chatMessageReadFlux
                .map(data -> {
                    data.setCreatedAt(Instant.now());
                    return data;
                })
                .window(100)
                .flatMap(batch ->
                        batch.flatMap(r2dbcEntityOperations::insert, 32)
                );
    }

    @Override
    public Mono<Void> deleteByRoomId(String roomId) {
        return peer.deleteByRoomId(roomId);
    }

    @Override
    public Mono<Boolean> existsByMessageIdAndRoomIdAndMemberId(String messageId, String roomId, String memberId) {
        return peer.existsByMessageIdAndRoomIdAndMemberId(messageId, roomId, memberId);
    }
}
