package com.nicolas.pulse.adapter.repository.accountrole;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.role.RoleData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.OffsetDateTime;

@Table(DbMeta.AccountRoleData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRoleData implements Persistable<String> {
    @Id
    @Column(DbMeta.AccountRoleData.COL_ID)
    private String id;
    @Column(DbMeta.AccountRoleData.COL_ACCOUNT_ID)
    private String accountId;
    @Column(DbMeta.AccountRoleData.COL_ROLE_ID)
    private String roleId;

    @CreatedBy
    @Column(DbMeta.AccountRoleData.COL_CREATED_BY)
    private String createdBy;

    @CreatedDate
    @Column(DbMeta.AccountRoleData.COL_CREATED_AT)
    private Instant createdAt;

    @Transient
    RoleData roleData;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && ObjectUtils.isEmpty(createdAt);
    }
}
