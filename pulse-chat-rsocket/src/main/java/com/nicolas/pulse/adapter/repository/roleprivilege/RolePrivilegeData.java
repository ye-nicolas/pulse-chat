package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Table(DbMeta.RolePrivilegeData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilegeData implements Persistable<String> {
    @Id
    @Column(DbMeta.RolePrivilegeData.COL_ID)
    private String id;
    @Column(DbMeta.RolePrivilegeData.COL_ROLE_ID)
    private String roleId;
    @Column(DbMeta.RolePrivilegeData.COL_PRIVILEGE)
    private Privilege privilege;

    @CreatedBy
    @Column(DbMeta.RolePrivilegeData.COL_CREATED_BY)
    private String createdBy;

    @CreatedDate
    @Column(DbMeta.RolePrivilegeData.COL_CREATED_AT)
    private Instant createdAt;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && ObjectUtils.isEmpty(createdAt);
    }
}
