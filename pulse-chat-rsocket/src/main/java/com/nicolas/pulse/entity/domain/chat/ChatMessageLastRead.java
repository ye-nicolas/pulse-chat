package com.nicolas.pulse.entity.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageLastRead {
    private String id;
    private String lastMessageId;
    private String roomId;
    private String memberId;
    private String createdBy;
    private Instant createdAt;
}
