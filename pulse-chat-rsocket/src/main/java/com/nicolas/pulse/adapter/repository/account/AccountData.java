package com.nicolas.pulse.adapter.repository.account;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.role.RoleData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.List;


@Table(value = DbMeta.AccountData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountData {
    @Id
    @Column(DbMeta.AccountData.COLUMN_ID)
    private String id;

    @Column(DbMeta.AccountData.COLUMN_NAME)
    private String name;

    @Column(DbMeta.AccountData.COLUMN_SHOW_NAME)
    private String showName;

    @Column(DbMeta.AccountData.COLUMN_PASSWORD)
    private String password;

    @Column(DbMeta.AccountData.COLUMN_IS_ACTIVE)
    private boolean isActive;

    @Column(DbMeta.AccountData.COLUMN_LAST_LOGIN_AT)
    private OffsetDateTime lastLoginAt;

    @CreatedBy
    @Column(DbMeta.AccountData.COLUMN_CREATED_BY)
    private String createdBy;

    @LastModifiedBy
    @Column(DbMeta.AccountData.COLUMN_UPDATED_BY)
    private String updatedBy;

    @CreatedDate
    @Column(DbMeta.AccountData.COLUMN_CREATED_AT)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(DbMeta.AccountData.COLUMN_UPDATED_AT)
    private OffsetDateTime updatedAt;

    @Column(DbMeta.AccountData.COLUMN_REMARK)
    private String remark;
}
