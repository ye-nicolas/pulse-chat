package com.nicolas.pulse.adapter.repository.chat.room;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;


public class ChatRoomDataMapper {
    private ChatRoomDataMapper() {
    }

    public static ChatRoom dataToDomain(ChatRoomData data) {
        return ChatRoom.builder()
                .id(data.getId())
                .name(data.getName())
                .type(data.getType())
                .createdBy(data.getCreatedBy())
                .updatedBy(data.getUpdatedBy())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .build();
    }

    public static ChatRoomData domainToData(ChatRoom domain) {
        return ChatRoomData.builder()
                .id(domain.getId())
                .name(domain.getName())
                .type(domain.getType())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
