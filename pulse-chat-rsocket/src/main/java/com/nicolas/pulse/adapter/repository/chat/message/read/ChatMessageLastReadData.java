package com.nicolas.pulse.adapter.repository.chat.message.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;

import static com.nicolas.pulse.adapter.repository.DbMeta.ChatMessageLastReadData.*;

//UK  messageId + roomId + memberId
@Table(TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageLastReadData implements Persistable<String> {
    @Id
    @Column(COL_ID)
    private String id;
    @Column(COL_LAST_MESSAGE_ID)
    private String lastMessageId;
    @Column(COL_ROOM_ID)
    private String roomId;
    @Column(COL_MEMBER_ID)
    private String memberId;

    @CreatedBy
    @Column(COL_CREATED_BY)
    private String createdBy;

    @LastModifiedBy
    @Column(COL_CREATED_AT)
    private Instant createdAt;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && ObjectUtils.isEmpty(createdAt);
    }
}
