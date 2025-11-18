package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(DbMeta.RolePrivilegeData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilegeData {
    @Id
    @Column(DbMeta.RolePrivilegeData.COLUMN_ROLE_ID)
    private String roleId;
    @Id
    @Column(DbMeta.RolePrivilegeData.COLUMN_PRIVILEGE)
    private Privilege privilege;
    @Column(DbMeta.RolePrivilegeData.COLUMN_CREATED_BY)
    private String createdBy;
    @Column(DbMeta.RolePrivilegeData.COLUMN_CREATED_AT)
    private Instant createdAt;
}
