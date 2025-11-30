package com.nicolas.pulse.adapter.repository.friendship;

import com.nicolas.pulse.adapter.repository.DbMeta;
import com.nicolas.pulse.adapter.repository.account.AccountData;
import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
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

@Table(DbMeta.FriendShipData.TABLE_NAME)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShipData {
    @Id
    @Column(DbMeta.FriendShipData.COLUMN_ID)
    private String id;

    @Column(DbMeta.FriendShipData.COLUMN_REQUESTER_ACCOUNT_ID)
    private String requesterAccountId;

    @Column(DbMeta.FriendShipData.COLUMN_RECIPIENT_ACCOUNT_ID)
    private String recipientAccountId;

    @Column(DbMeta.FriendShipData.COLUMN_STATUS)
    private FriendShipStatus status;

    @Column(DbMeta.FriendShipData.COLUMN_CREATED_BY)
    private String createdBy;

    @Column(DbMeta.FriendShipData.COLUMN_UPDATED_BY)
    private String updatedBy;

    @Column(DbMeta.FriendShipData.COLUMN_CREATED_AT)
    private OffsetDateTime createdAt;

    @Column(DbMeta.FriendShipData.COLUMN_UPDATED_AT)
    private OffsetDateTime updatedAt;

    @Transient
    private AccountData requesterAccount;
    @Transient
    private AccountData recipientAccount;
}
