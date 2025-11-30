package com.nicolas.pulse.entity.domain;

import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendShip {
    private String id;
    private Account requesterAccount;
    private Account recipientAccount;
    private FriendShipStatus status;
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
