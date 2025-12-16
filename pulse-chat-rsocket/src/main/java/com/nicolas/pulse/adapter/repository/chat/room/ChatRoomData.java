package com.nicolas.pulse.adapter.repository.chat.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;

import static com.nicolas.pulse.adapter.repository.DbMeta.ChatRoomData.*;

@Table(value = TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomData implements Persistable<String> {
    @Id
    @Column(COL_ID)
    private String id;

    @Column(COL_NAME)
    private String name;

    @CreatedBy
    @Column(COL_CREATED_BY)
    private String createdBy;

    @LastModifiedBy
    @Column(COL_UPDATED_BY)
    private String updatedBy;

    @CreatedDate
    @Column(COL_CREATED_AT)
    private Instant createdAt;

    @LastModifiedDate
    @Column(COL_UPDATED_AT)
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && !StringUtils.hasText(updatedBy)
                && ObjectUtils.isEmpty(createdAt)
                && ObjectUtils.isEmpty(updatedAt);
    }
}
