package com.nicolas.pulse.adapter.repository.friendship;

import com.nicolas.pulse.adapter.repository.account.AccountDataMapper;
import com.nicolas.pulse.entity.domain.FriendShip;


public class FriendShipDataMapper {
    private FriendShipDataMapper() {
    }

    public static FriendShip dataToDomain(FriendShipData data) {
        if (data == null) {
            return null;
        }
        return FriendShip.builder()
                .id(data.getId())
                .requesterAccount(AccountDataMapper.dataToDomain(data.getRequesterAccount()))
                .recipientAccount(AccountDataMapper.dataToDomain(data.getRecipientAccount()))
                .status(data.getStatus())
                .createdBy(data.getCreatedBy())
                .updatedBy(data.getUpdatedBy())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .build();
    }

    public static FriendShipData domainToData(FriendShip data) {
        if (data == null) {
            return null;
        }
        return FriendShipData.builder()
                .id(data.getId())
                .requesterAccountId(data.getRequesterAccount().getId())
                .recipientAccountId(data.getRecipientAccount().getId())
                .requesterAccount(AccountDataMapper.domainToData(data.getRequesterAccount()))
                .recipientAccount(AccountDataMapper.domainToData(data.getRecipientAccount()))
                .status(data.getStatus())
                .createdBy(data.getCreatedBy())
                .updatedBy(data.getUpdatedBy())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .build();
    }
}

