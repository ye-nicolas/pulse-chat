package com.nicolas.pulse.adapter.repository.chat.message.read;

import com.nicolas.pulse.entity.domain.chat.ChatMessageLastRead;
import com.nicolas.pulse.service.repository.ChatMessageReadLastRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public class ChatMessageReadLastRepositoryImp implements ChatMessageReadLastRepository {
    private final ChatMessageReadDataRepositoryPeer peer;

    public ChatMessageReadLastRepositoryImp(ChatMessageReadDataRepositoryPeer peer) {
        this.peer = peer;
    }

    @Override
    public Mono<ChatMessageLastRead> findByLastMessageIdAndRoomIdAndMemberId(String messageId, String roomId, String memberId) {
        return peer.findByLastMessageIdAndRoomIdAndMemberId(messageId, roomId, memberId).map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Transactional
    @Override
    public Mono<ChatMessageLastRead> save(ChatMessageLastRead messageRead) {
        ChatMessageLastReadData chatMessageLastReadData = ChatMessageReadDataMapper.domainToData(messageRead);
        return peer.save(chatMessageLastReadData).map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatMessageLastRead> saveAll(List<ChatMessageLastRead> chatMessageLastReadList) {
        return peer.saveAll(Flux.fromStream(chatMessageLastReadList.stream().map(ChatMessageReadDataMapper::domainToData)))
                .map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Override
    public Mono<Boolean> existsByLastMessageIdAndRoomIdAndMemberId(String messageId, String roomId, String memberId) {
        return peer.existsByLastMessageIdAndRoomIdAndMemberId(messageId, roomId, memberId);
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
