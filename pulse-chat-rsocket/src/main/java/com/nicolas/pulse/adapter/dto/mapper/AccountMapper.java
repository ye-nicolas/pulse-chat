package com.nicolas.pulse.adapter.dto.mapper;

import com.nicolas.pulse.adapter.dto.response.AccountRes;
import com.nicolas.pulse.entity.domain.Account;

import java.time.ZoneId;

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
                .lastLoginAt(domain.getLastLoginAt().atZone(ZoneId.systemDefault()))
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt().atZone(ZoneId.systemDefault()))
                .updatedAt(domain.getUpdatedAt().atZone(ZoneId.systemDefault()))
                .remark(domain.getRemark())
                .build();
    }
}
