package com.nicolas.pulse.adapter.repository.chat.room;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ChatRoomRepositoryImpl implements ChatRoomRepository{
    private final ChatRoomDataRepositoryPeer peer;

    public ChatRoomRepositoryImpl(ChatRoomDataRepositoryPeer peer) {
        this.peer = peer;
    }

    @Override
    public Flux<ChatRoom> findAll() {
        return peer.findAll().map(ChatRoomDataMapper::dataToDomain);
    }

    @Override
    public Mono< ChatRoom> findById(String id) {
        return peer.findById(id).map(ChatRoomDataMapper::dataToDomain);
    }

    @Override
    public Mono< ChatRoom> save(ChatRoom account) {
        ChatRoomData accountData = ChatRoomDataMapper.domainToData(account);
        return peer.save(accountData).map(ChatRoomDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }
}
