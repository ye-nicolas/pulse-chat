package com.nicolas.pulse.adapter.repository.chat.room;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.service.repository.ChatRoomRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Repository
public class ChatRoomRepositoryImpl implements ChatRoomRepository{
    private final ChatRoomDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public ChatRoomRepositoryImpl(ChatRoomDataRepositoryPeer peer,
                                  R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
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
    public Mono< ChatRoom> create(ChatRoom account) {
        OffsetDateTime now = OffsetDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        ChatRoomData accountData = ChatRoomDataMapper.domainToData(account);
        return r2dbcEntityOperations.insert(accountData).map(ChatRoomDataMapper::dataToDomain);
    }

    @Override
    public Mono< ChatRoom> update(ChatRoom account) {
        account.setUpdatedAt(OffsetDateTime.now());
        ChatRoomData accountData = ChatRoomDataMapper.domainToData(account);
        return r2dbcEntityOperations.update(accountData).map(ChatRoomDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }
}
