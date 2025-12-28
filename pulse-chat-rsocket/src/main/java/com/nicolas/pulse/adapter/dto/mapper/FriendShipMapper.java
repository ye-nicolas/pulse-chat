package com.nicolas.pulse.adapter.dto.mapper;

import com.nicolas.pulse.adapter.dto.res.FriendShipRes;
import com.nicolas.pulse.entity.domain.FriendShip;

public class FriendShipMapper {
    public static FriendShipRes domainToRes(FriendShip domain){
        if (domain==null){
            return null;
        }
        return FriendShipRes.builder()
                .id(domain.getId())
                .recipientAccount(AccountMapper.domainToRes(domain.getRecipientAccount()))
                .requesterAccount(AccountMapper.domainToRes(domain.getRequesterAccount()))
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
