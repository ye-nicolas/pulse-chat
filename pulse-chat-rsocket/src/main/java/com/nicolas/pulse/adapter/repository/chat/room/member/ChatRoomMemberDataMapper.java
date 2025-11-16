package com.nicolas.pulse.adapter.repository.chat.room.member;

import com.nicolas.pulse.adapter.repository.chat.room.ChatRoomDataMapper;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;


public class ChatRoomMemberDataMapper {
    private ChatRoomMemberDataMapper() {
    }

    public static ChatRoomMember dataToDomain(ChatRoomMemberData data) {
        return ChatRoomMember.builder()
                .id(data.getId())
                .accountId(data.getAccountId())
                .chatRoom(ChatRoomDataMapper.dataToDomain(data.getRoomData()))
                .createdBy(data.getCreatedBy())
                .updatedBy(data.getUpdatedBy())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .isMuted(data.isMuted())
                .isPinned(data.isPinned())
                .build();
    }

    public static ChatRoomMemberData domainToData(ChatRoomMember domain) {
        return ChatRoomMemberData.builder()
                .id(domain.getId())
                .accountId(domain.getAccountId())
                .roomId(domain.getChatRoom().getId())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .isMuted(domain.isMuted())
                .isPinned(domain.isPinned())
                .build();
    }
}
