package com.nicolas.pulse.adapter.repository.chat.room.member;

import com.nicolas.pulse.adapter.repository.chat.room.ChatRoomData;
import com.nicolas.pulse.entity.enumerate.ChatRoomMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

import static com.nicolas.pulse.adapter.repository.DbMeta.ChatRoomMemberData.*;

@Table(value = TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMemberData {
    @Id
    @Column(COLUMN_ID)
    private String id;
    @Column(COLUMN_ACCOUNT_ID)
    private String accountId;
    @Column(COLUMN_ROOM_ID)
    private String roomId;
    @Column(COLUMN_ROLE)
    private ChatRoomMemberRole role;

    @CreatedBy
    @Column(COLUMN_CREATED_BY)
    private String createdBy;

    @LastModifiedBy
    @Column(COLUMN_UPDATED_BY)
    private String updatedBy;

    @CreatedDate
    @Column(COLUMN_CREATED_AT)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(COLUMN_UPDATED_AT)
    private OffsetDateTime updatedAt;

    @Column(COLUMN_LAST_READ_MESSAGE_ID)
    private String lastReadMessageId;
    @Column(COLUMN_IS_MUTED)
    private boolean isMuted;
    @Column(COLUMN_IS_PINNED)
    private boolean isPinned;

    @Transient
    private ChatRoomData roomData;
}
