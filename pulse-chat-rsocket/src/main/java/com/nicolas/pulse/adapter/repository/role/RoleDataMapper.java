package com.nicolas.pulse.adapter.repository.role;

import com.nicolas.pulse.entity.domain.Role;

public class RoleDataMapper {
    private RoleDataMapper() {

    }

    public static Role dataToDomain(RoleData data) {
        if (data == null) {
            return null;
        }
        return Role.builder()
                .id(data.getId())
                .name(data.getName())
                .remark(data.getRemark())
                .createdBy(data.getCreatedBy())
                .updatedBy(data.getUpdatedBy())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .privilegeSet(data.getPrivilegeSet())
                .build();
    }

    public static RoleData domainToData(Role domain) {
        if (domain == null) {
            return null;
        }
        return RoleData.builder()
                .id(domain.getId())
                .name(domain.getName())
                .remark(domain.getRemark())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
