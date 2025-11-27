package com.nicolas.pulse.adapter.repository.chat.message;

import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("chat_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageData {
    @Id
    @Column("id")
    private String id;
    @Column("room_id")
    private String roomId;
    @Column("member_id")
    private String memberId;
    @Column("type")
    private ChatMessageType type;
    @Column("content")
    private String content;
    @Column("created_by")
    private String createdBy;
    @Column("created_at")
    private OffsetDateTime createdAt;
    @Column("updated_at")
    private OffsetDateTime updatedAt;
    @Column("delete_at")
    private OffsetDateTime deletedAt;
    @Column("is_delete")
    @Builder.Default
    private boolean isDelete = false;
}
