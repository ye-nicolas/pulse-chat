package com.nicolas.pulse.adapter.dto.res;

import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShipRes {
    private String id;
    private AccountRes requesterAccount;
    private AccountRes recipientAccount;
    private FriendShipStatus status;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
