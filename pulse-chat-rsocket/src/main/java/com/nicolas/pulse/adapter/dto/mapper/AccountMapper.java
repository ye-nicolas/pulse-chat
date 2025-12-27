package com.nicolas.pulse.adapter.dto.mapper;

import com.nicolas.pulse.adapter.dto.res.AccountRes;
import com.nicolas.pulse.entity.domain.Account;

public class AccountMapper {
    public static AccountRes domainToRes(Account domain) {
        if (domain == null) {
            return null;
        }
        return AccountRes.builder()
                .id(domain.getId())
                .name(domain.getName())
                .showName(domain.getShowName())
                .isActive(domain.isActive())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .remark(domain.getRemark())
                .build();
    }
}
