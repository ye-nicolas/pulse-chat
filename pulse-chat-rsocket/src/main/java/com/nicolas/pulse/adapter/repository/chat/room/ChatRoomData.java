package com.nicolas.pulse.adapter.repository.chat.room;

import com.nicolas.pulse.entity.enumerate.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

import static com.nicolas.pulse.adapter.repository.DbMeta.ChatRoomData.*;

@Table(value = TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomData {
    @Id
    @Column(COLUMN_ID)
    private String id;

    @Column(COLUMN_NAME)
    private String name;

    @Column(COLUMN_TYPE)
    private ChatRoomType type;

    @Column(COLUMN_CREATED_BY)
    private String createdBy;

    @Column(COLUMN_UPDATED_BY)
    private String updatedBy;

    @Column(COLUMN_CREATED_AT)
    private OffsetDateTime createdAt;

    @Column(COLUMN_UPDATED_AT)
    private OffsetDateTime updatedAt;
}
