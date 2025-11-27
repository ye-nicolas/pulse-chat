package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.entity.domain.RolePrivilege;
import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;


public class RolePrivilegeDataMapper {
    private RolePrivilegeDataMapper() {

    }

    public static RolePrivilege dataToDomain(RolePrivilegeData data) {
        if (data == null) {
            return null;
        }

        return RolePrivilege.builder()
                .id(data.getId())
                .roleId(data.getRoleId())
                .privilege(data.getPrivilege())
                .createdBy(data.getCreatedBy())
                .createdAt(data.getCreatedAt())
                .build();
    }

    public static RolePrivilegeData domainToData(RolePrivilege domain) {
        if (domain == null) {
            return null;
        }

        return RolePrivilegeData.builder()
                .id(domain.getId())
                .roleId(domain.getRoleId())
                .privilege(domain.getPrivilege())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
