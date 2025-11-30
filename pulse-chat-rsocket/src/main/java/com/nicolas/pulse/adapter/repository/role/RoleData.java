package com.nicolas.pulse.adapter.repository.role;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Set;

@Table(DbMeta.RoleData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleData {
    @Id
    @Column(DbMeta.RoleData.COLUMN_ID)
    private String id;
    @Column(DbMeta.RoleData.COLUMN_NAME)
    private String name;
    @Column(DbMeta.RoleData.COLUMN_CREATED_BY)
    private String createdBy;
    @Column(DbMeta.RoleData.COLUMN_UPDATED_BY)
    private String updatedBy;
    @Column(DbMeta.RoleData.COLUMN_CREATED_AT)
    private OffsetDateTime createdAt;
    @Column(DbMeta.RoleData.COLUMN_UPDATED_AT)
    private OffsetDateTime updatedAt;
    @Column(DbMeta.RoleData.COLUMN_REMARK)
    private String remark;

    @Transient
    private Set<Privilege> privilegeSet;
}
