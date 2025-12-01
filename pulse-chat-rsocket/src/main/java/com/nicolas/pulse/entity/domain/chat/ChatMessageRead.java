package com.nicolas.pulse.entity.domain.chat;

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
public class ChatMessageRead {
    private String id;
    private String messageId;
    private String roomId;
    private String memberId;
    private String createdBy;
    private Instant createdAt;
}
