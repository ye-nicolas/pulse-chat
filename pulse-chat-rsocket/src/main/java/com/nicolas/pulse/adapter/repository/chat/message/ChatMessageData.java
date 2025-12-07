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

import static com.nicolas.pulse.adapter.repository.DbMeta.ChatMessageData.*;

@Table(TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageData implements Persistable<String> {
    @Id
    @Column(COL_ID)
    private String id;
    @Column(COL_ROOM_ID)
    private String roomId;
    @Column(COL_MEMBER_ID)
    private String memberId;
    @Column(COL_TYPE)
    private ChatMessageType type;
    @Column(COL_CONTENT)
    private String content;

    @Builder.Default
    @Column(COL_IS_DELETE)
    private boolean isDelete = false;

    @Column(COL_DELETED_AT)
    private Instant deletedAt;

    @CreatedBy
    @Column(COL_CREATED_BY)
    private String createdBy;

    @CreatedDate
    @Column(COL_CREATED_AT)
    private Instant createdAt;

    @LastModifiedDate
    @Column(COL_UPDATED_AT)
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && ObjectUtils.isEmpty(createdAt)
                && ObjectUtils.isEmpty(updatedAt);
    }
}
