package com.nicolas.pulse.adapter.repository.accountrole;

import com.nicolas.pulse.adapter.repository.role.RoleDataMapper;
import com.nicolas.pulse.entity.domain.AccountRole;


public class AccountRoleDataMapper {
    private AccountRoleDataMapper() {

    }

    public static AccountRole dataToDomain(AccountRoleData data) {
        if (data == null) {
            return null;
        }
        return AccountRole.builder()
                .id(data.getId())
                .accountId(data.getAccountId())
                .role(RoleDataMapper.dataToDomain(data.getRoleData()))
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())

                .build();
    }

    public static AccountRoleData domainToData(AccountRole domain) {
        if (domain == null) {
            return null;
        }
        return AccountRoleData.builder()
                .id(domain.getId())
                .accountId(domain.getAccountId())
                .roleId(domain.getRole().getId())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .roleData(RoleDataMapper.domainToData(domain.getRole()))
                .build();
    }
}
