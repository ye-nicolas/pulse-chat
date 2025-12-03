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

//UK  messageId + roomId + memberId
@Table("chat_message_read")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadData  implements Persistable<String> {
    @Id
    @Column("id")
    private String id;
    @Column("message_id")
    private String messageId;
    @Column("room_id")
    private String roomId;
    @Column("memberId")
    private String memberId;

    @CreatedBy
    @Column("created_by")
    private String createdBy;

    @LastModifiedBy
    @Column("created_at")
    private Instant createdAt;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && ObjectUtils.isEmpty(createdAt);
    }
}
