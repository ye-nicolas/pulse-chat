package com.nicolas.pulse.entity.domain.chat;

import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String id;
    private String roomId;
    private String memberId;
    private ChatMessageType type;
    private String content;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    @Builder.Default
    private boolean isDelete = false;
}
