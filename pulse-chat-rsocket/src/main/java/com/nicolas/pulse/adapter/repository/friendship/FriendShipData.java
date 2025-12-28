package com.nicolas.pulse.adapter.repository.friendship;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.account.AccountData;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
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

@Table(DbMeta.FriendShipData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShipData implements Persistable<String> {
    @Id
    @Column(DbMeta.FriendShipData.COL_ID)
    private String id;

    @Column(DbMeta.FriendShipData.COL_REQUESTER_ACCOUNT_ID)
    private String requesterAccountId;

    @Column(DbMeta.FriendShipData.COL_RECIPIENT_ACCOUNT_ID)
    private String recipientAccountId;

    @Column(DbMeta.FriendShipData.COL_STATUS)
    private FriendShipStatus status;

    @CreatedBy
    @Column(DbMeta.FriendShipData.COL_CREATED_BY)
    private String createdBy;

    @LastModifiedBy
    @Column(DbMeta.FriendShipData.COL_UPDATED_BY)
    private String updatedBy;

    @CreatedDate
    @Column(DbMeta.FriendShipData.COL_CREATED_AT)
    private Instant createdAt;

    @LastModifiedDate
    @Column(DbMeta.FriendShipData.COL_UPDATED_AT)
    private Instant updatedAt;

    @Transient
    private AccountData requesterAccount;
    @Transient
    private AccountData recipientAccount;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(createdBy)
                && !StringUtils.hasText(updatedBy)
                && ObjectUtils.isEmpty(createdAt)
                && ObjectUtils.isEmpty(updatedAt);
    }
}
