package com.nicolas.pulse.adapter.dto.mapper;

import com.nicolas.pulse.adapter.dto.response.RoleRes;
import com.nicolas.pulse.entity.domain.Role;

public class RoleMapper {
    private RoleMapper() {

    }

    public static RoleRes domainToRes(Role domain) {
        if (domain == null) {
            return null;
        }
        return RoleRes.builder()
                .id(domain.getId())
                .name(domain.getName())
                .remark(domain.getRemark())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .privilegeSet(domain.getPrivilegeSet())
                .build();
    }
}
