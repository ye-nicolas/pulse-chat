package com.nicolas.pulse.adapter.repository.accountrole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("account_role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRoleData {
    @Id
    @Column("id")
    private String id;
    @Column("account_id")
    private String accountId;
    @Column("role_id")
    private String roleId;
    @Column("created_by")
    private String createdBy;
    @Column("created_at")
    private OffsetDateTime createdAt;
}
