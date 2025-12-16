package com.nicolas.pulse.adapter.repository.chat.message.read;

import com.nicolas.pulse.entity.domain.chat.ChatMessageLastRead;


public class ChatMessageReadDataMapper {
    private ChatMessageReadDataMapper() {

    }

    public static ChatMessageLastRead dataToDomain(ChatMessageLastReadData data) {
        if (data == null) {
            return null;
        }
        return ChatMessageLastRead.builder()
                .id(data.getId())
                .lastMessageId(data.getLastMessageId())
                .roomId(data.getRoomId())
                .memberId(data.getMemberId())
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())
                .build();
    }

    public static ChatMessageLastReadData domainToData(ChatMessageLastRead domain) {
        if (domain == null) {
            return null;
        }
        return ChatMessageLastReadData.builder()
                .id(domain.getId())
                .lastMessageId(domain.getLastMessageId())
                .roomId(domain.getRoomId())
                .memberId(domain.getMemberId())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
