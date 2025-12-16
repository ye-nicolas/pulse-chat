package com.nicolas.pulse.adapter.repository.chat.message;

import com.nicolas.pulse.entity.domain.chat.ChatMessage;


public class ChatMessageDataMapper {
    private ChatMessageDataMapper() {

    }

    public static ChatMessage dataToDomain(ChatMessageData data) {
        if (data == null) {
            return null;
        }
        return ChatMessage.builder()
                .id(data.getId())
                .roomId(data.getRoomId())
                .memberId(data.getMemberId())
                .type(data.getType())
                .content(data.getContent())
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .isDelete(data.isDelete())
                .build();
    }

    public static ChatMessageData domainToData(ChatMessage domain) {
        if (domain == null) {
            return null;
        }
        return ChatMessageData.builder()
                .id(domain.getId())
                .roomId(domain.getRoomId())
                .memberId(domain.getMemberId())
                .type(domain.getType())
                .content(domain.getContent())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .isDelete(domain.isDelete())
                .build();
    }
}
