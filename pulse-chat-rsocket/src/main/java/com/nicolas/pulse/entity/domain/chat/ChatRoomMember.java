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
public class ChatRoomMember {
    private String id;
    private String accountId;
    private ChatRoom chatRoom;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isMuted; // 靜音
    private boolean isPinned; // 置頂
}
