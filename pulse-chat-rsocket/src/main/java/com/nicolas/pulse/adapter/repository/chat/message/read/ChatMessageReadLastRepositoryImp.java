package com.nicolas.pulse.adapter.repository.chat.message.read;

import com.nicolas.pulse.entity.domain.chat.ChatMessageLastRead;
import com.nicolas.pulse.service.repository.ChatMessageReadLastRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public class ChatMessageReadLastRepositoryImp implements ChatMessageReadLastRepository {
    private final ChatMessageReadDataRepositoryPeer peer;

    public ChatMessageReadLastRepositoryImp(ChatMessageReadDataRepositoryPeer peer) {
        this.peer = peer;
    }

    @Override
    public Flux<ChatMessageLastRead> findAllByRoomId(String roomId) {
        return peer.findAllByRoomId(roomId).map(ChatMessageReadDataMapper::dataToDomain);
    }

    public Mono<ChatMessageLastRead> findByRoomIdAndMemberId(String roomId, String memberId) {
        return peer.findByRoomIdAndMemberId(roomId, memberId).map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Transactional
    @Override
    public Mono<ChatMessageLastRead> save(ChatMessageLastRead messageRead) {
        ChatMessageLastReadData chatMessageLastReadData = ChatMessageReadDataMapper.domainToData(messageRead);
        return peer.save(chatMessageLastReadData).map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Transactional
    @Override
    public Mono<Void> deleteByRoomId(String roomId) {
        return peer.deleteByRoomId(roomId);
    }

    @Transactional
    @Override
    public Mono<Void> deleteByMemberId(String memberId) {
        return peer.deleteByMemberId(memberId);
    }
}
