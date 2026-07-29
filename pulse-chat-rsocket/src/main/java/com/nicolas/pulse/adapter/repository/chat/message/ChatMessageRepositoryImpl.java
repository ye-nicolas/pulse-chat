package com.nicolas.pulse.adapter.repository.chat.message;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;
import com.nicolas.pulse.service.repository.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ChatMessageRepositoryImpl implements ChatMessageRepository {
    private final ChatMessageDataRepositoryPeer peer;

    public ChatMessageRepositoryImpl(ChatMessageDataRepositoryPeer peer) {
        this.peer = peer;
    }

    @Override
    public Mono<ChatMessage> findById(String id) {
        return peer.findById(id).map(ChatMessageDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatMessage> findAllByRoomId(String roomId, PageRequest pageRequest) {
        return peer.findAllByRoomId(roomId, pageRequest).map(ChatMessageDataMapper::dataToDomain);
    }

    @Transactional
    @Override
    public Mono<ChatMessage> save(ChatMessage chatMessage) {
        ChatMessageData chatMessageData = ChatMessageDataMapper.domainToData(chatMessage);
        return peer.save(chatMessageData).map(ChatMessageDataMapper::dataToDomain);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return peer.existsById(id);
    }

    @Transactional
    @Override
    public Mono<Void> deleteByRoomId(String roomId) {
        return peer.deleteByRoomId(roomId);
    }
}
