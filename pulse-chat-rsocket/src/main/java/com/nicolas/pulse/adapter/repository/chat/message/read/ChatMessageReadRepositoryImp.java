package com.nicolas.pulse.adapter.repository.chat.message.read;

import com.nicolas.pulse.entity.domain.chat.ChatMessageRead;
import com.nicolas.pulse.service.repository.ChatMessageReadRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public class ChatMessageReadRepositoryImp implements ChatMessageReadRepository {
    private final ChatMessageReadDataRepositoryPeer peer;

    public ChatMessageReadRepositoryImp(ChatMessageReadDataRepositoryPeer peer) {
        this.peer = peer;
    }

    @Override
    public Mono<ChatMessageRead> findFirstByRoomIdAndMemberIdOrderByCreatedAtDesc(String roomId, String memberId) {
        return peer.findFirstByRoomIdAndMemberIdOrderByCreatedAtDesc(roomId, memberId).map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Override
    public Mono<ChatMessageRead> save(ChatMessageRead messageRead) {
        ChatMessageReadData chatMessageReadData = ChatMessageReadDataMapper.domainToData(messageRead);
        return peer.save(chatMessageReadData).map(ChatMessageReadDataMapper::dataToDomain);
    }

    @Override
    public Flux<ChatMessageRead> saveAll(List<ChatMessageRead> chatMessageReadList) {
        return peer.saveAll(Flux.fromStream(chatMessageReadList.stream().map(ChatMessageReadDataMapper::domainToData)))
                .map(ChatMessageReadDataMapper::dataToDomain);
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
