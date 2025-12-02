package com.nicolas.pulse.adapter.repository.role;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.entity.enumerate.Privilege;
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
import java.util.Set;

@Table(DbMeta.RoleData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleData implements Persistable<String> {
    @Id
    @Column(DbMeta.RoleData.COLUMN_ID)
    private String id;

    @Column(DbMeta.RoleData.COLUMN_NAME)
    private String name;

    @CreatedBy
    @Column(DbMeta.RoleData.COLUMN_CREATED_BY)
    private String createdBy;

    @LastModifiedBy
    @Column(DbMeta.RoleData.COLUMN_UPDATED_BY)
    private String updatedBy;

    @CreatedDate
    @Column(DbMeta.RoleData.COLUMN_CREATED_AT)
    private Instant createdAt;

    @LastModifiedDate
    @Column(DbMeta.RoleData.COLUMN_UPDATED_AT)
    private Instant updatedAt;

    @Column(DbMeta.RoleData.COLUMN_REMARK)
    private String remark;

    @Transient
    private Set<Privilege> privilegeSet;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && !StringUtils.hasText(updatedBy)
                && ObjectUtils.isEmpty(createdAt)
                && ObjectUtils.isEmpty(updatedAt);
    }
}
