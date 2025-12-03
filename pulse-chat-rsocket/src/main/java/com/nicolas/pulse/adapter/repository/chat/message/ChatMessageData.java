package com.nicolas.pulse.adapter.repository.chat.message;

import com.nicolas.pulse.entity.enumerate.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Table("chat_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageData implements Persistable<String> {
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

    @Builder.Default
    @Column("is_delete")
    private boolean isDelete = false;

    @Column("delete_at")
    private Instant deletedAt;

    @CreatedBy
    @Column("created_by")
    private String createdBy;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && ObjectUtils.isEmpty(createdAt)
                && ObjectUtils.isEmpty(updatedAt);
    }
}
