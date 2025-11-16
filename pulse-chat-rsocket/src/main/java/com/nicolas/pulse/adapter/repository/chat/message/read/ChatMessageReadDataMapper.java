package com.nicolas.pulse.adapter.repository.chat.message.read;

import com.nicolas.pulse.entity.domain.chat.ChatMessageRead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;


public class ChatMessageReadDataMapper {
    private ChatMessageReadDataMapper() {

    }

    public static ChatMessageRead dataToDomain(ChatMessageReadData data) {
        if (data == null) {
            return null;
        }
        return ChatMessageRead.builder()
                .id(data.getId())
                .messageId(data.getMessageId())
                .roomId(data.getRoomId())
                .memberId(data.getMemberId())
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())
                .build();
    }

    public static ChatMessageReadData domainToData(ChatMessageRead domain) {
        if (domain == null) {
            return null;
        }
        return ChatMessageReadData.builder()
                .id(domain.getId())
                .messageId(domain.getMessageId())
                .roomId(domain.getRoomId())
                .memberId(domain.getMemberId())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
