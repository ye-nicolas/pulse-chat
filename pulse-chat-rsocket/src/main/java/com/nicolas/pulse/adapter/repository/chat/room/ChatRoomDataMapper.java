package com.nicolas.pulse.adapter.repository.chat.room;

import com.nicolas.pulse.entity.domain.chat.ChatRoom;


public class ChatRoomDataMapper {
    private ChatRoomDataMapper() {
    }

    public static ChatRoom dataToDomain(ChatRoomData data) {
        if (data == null) {
            return null;
        }
        return ChatRoom.builder()
                .id(data.getId())
                .name(data.getName())
                .createdBy(data.getCreatedBy())
                .updatedBy(data.getUpdatedBy())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .build();
    }

    public static ChatRoomData domainToData(ChatRoom domain) {
        if (domain == null) {
            return null;
        }
        return ChatRoomData.builder()
                .id(domain.getId())
                .name(domain.getName())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
