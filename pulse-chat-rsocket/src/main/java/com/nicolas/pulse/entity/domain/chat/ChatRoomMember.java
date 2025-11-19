package com.nicolas.pulse.entity.domain.chat;

import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.enumerate.ChatRoomMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMember {
    private String id;
    private String accountId;
    private ChatRoom chatRoom;
    private ChatRoomMemberRole role;
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String lastReadMessageId;
    private boolean isMuted;
    private boolean isPinned;
}
