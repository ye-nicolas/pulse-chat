package com.nicolas.pulse.adapter.repository.accountrole;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.role.RoleData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table(DbMeta.AccountRoleData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRoleData {
    @Id
    @Column(DbMeta.AccountRoleData.COLUMN_ID)
    private String id;
    @Column(DbMeta.AccountRoleData.COLUMN_ACCOUNT_ID)
    private String accountId;
    @Column(DbMeta.AccountRoleData.COLUMN_ROLE_ID)
    private String roleId;
    @Column(DbMeta.AccountRoleData.COLUMN_CREATED_BY)
    private String createdBy;
    @Column(DbMeta.AccountRoleData.COLUMN_CREATED_AT)
    private OffsetDateTime createdAt;

    @Transient
    RoleData roleData;
}
