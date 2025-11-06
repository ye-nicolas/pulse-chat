package com.nicolas.pulse.adapter.dto.mapper;

import com.nicolas.pulse.adapter.dto.response.UserRes;
import com.nicolas.pulse.entity.domain.User;

public class UserMapper {
    public static UserRes domainToRes(User domain) {
        if (domain == null) {
            return null;
        }
        return UserRes.builder()
                .id(domain.getId())
                .name(domain.getName())
                .showName(domain.getShowName())
                .isActive(domain.isActive())
                .lastLoginAt(domain.getLastLoginAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .remark(domain.getRemark())
                .build();
    }
}
