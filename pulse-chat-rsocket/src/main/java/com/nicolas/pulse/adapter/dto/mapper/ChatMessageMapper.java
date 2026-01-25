package com.nicolas.pulse.adapter.dto.mapper;

import com.nicolas.pulse.adapter.dto.res.ChatMessageRes;
import com.nicolas.pulse.entity.domain.chat.ChatMessage;

public class ChatMessageMapper {
    public static ChatMessageRes domainToRes(ChatMessage domain) {
        if (domain == null) {
            return null;
        }
        return ChatMessageRes.builder()
                .id(domain.getId())
                .roomId(domain.getRoomId())
                .type(domain.getType())
                .content(domain.isDelete() ? "" : domain.getContent())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .isDelete(domain.isDelete())
                .build();
    }
}
