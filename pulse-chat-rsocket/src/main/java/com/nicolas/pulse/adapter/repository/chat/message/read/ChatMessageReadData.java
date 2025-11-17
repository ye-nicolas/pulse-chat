package com.nicolas.pulse.adapter.repository.chat.message.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

//UK  messageId + roomId + memberId
@Table("chat_message_read")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadData {
    @Id
    @Column("id")
    private String id;
    @Column("message_id")
    private String messageId;
    @Column("room_id")
    private String roomId;
    @Column("memberId")
    private String memberId;
    @Column("created_by")
    private String createdBy;
    @Column("created_at")
    private Instant createdAt;
}
