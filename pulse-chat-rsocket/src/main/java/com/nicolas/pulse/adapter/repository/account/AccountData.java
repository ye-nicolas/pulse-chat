package com.nicolas.pulse.adapter.repository.account;

import com.nicolas.pulse.adapter.repository.DbMeta;
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


@Table(value = DbMeta.AccountData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountData implements Persistable<String> {
    @Id
    @Column(DbMeta.AccountData.COL_ID)
    private String id;

    @Column(DbMeta.AccountData.COL_NAME)
    private String name;

    @Column(DbMeta.AccountData.COL_SHOW_NAME)
    private String showName;

    @Column(DbMeta.AccountData.COL_PASSWORD)
    private String password;

    @Column(DbMeta.AccountData.COL_IS_ACTIVE)
    private boolean isActive;

    @Column(DbMeta.AccountData.COL_LAST_LOGIN_AT)
    private Instant lastLoginAt;

    @CreatedBy
    @Column(DbMeta.AccountData.COL_CREATED_BY)
    private String createdBy;

    @LastModifiedBy
    @Column(DbMeta.AccountData.COL_UPDATED_BY)
    private String updatedBy;

    @CreatedDate
    @Column(DbMeta.AccountData.COL_CREATED_AT)
    private Instant createdAt;

    @LastModifiedDate
    @Column(DbMeta.AccountData.COL_UPDATED_AT)
    private Instant updatedAt;

    @Column(DbMeta.AccountData.COL_REMARK)
    private String remark;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && !StringUtils.hasText(updatedBy)
                && ObjectUtils.isEmpty(createdAt)
                && ObjectUtils.isEmpty(updatedAt);
    }
}
