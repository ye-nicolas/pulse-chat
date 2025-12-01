package com.nicolas.pulse.adapter.repository.chat.message;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ChatMessageRepositoryImpl implements ChatMessageRepository {
    private final ChatMessageDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public ChatMessageRepositoryImpl(ChatMessageDataRepositoryPeer peer,
                                     R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Mono<ChatMessage> findById(String id) {
        return peer.findById(id).map(ChatMessageDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatMessage> findByRoomIdAndMemberId(String roomId, String memberId) {
        return peer.findByRoomIdAndMemberId(roomId, memberId)
                .map(ChatMessageDataMapper::dataToDomain);
    }

    @Override
    public Mono<ChatMessage> create(ChatMessage chatMessage) {
        ChatMessageData chatMessageData = ChatMessageDataMapper.domainToData(chatMessage);
        return r2dbcEntityOperations.insert(chatMessageData).map(ChatMessageDataMapper::dataToDomain);
    }

    @Override
    public Mono<ChatMessage> update(ChatMessage chatMessage) {
        ChatMessageData chatMessageData = ChatMessageDataMapper.domainToData(chatMessage);
        return r2dbcEntityOperations.insert(chatMessageData).map(ChatMessageDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteByRoomId(String roomId) {
        return peer.deleteByRoomId(roomId);
    }
}
