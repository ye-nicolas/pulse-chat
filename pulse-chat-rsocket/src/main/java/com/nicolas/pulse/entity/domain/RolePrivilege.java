package com.nicolas.pulse.entity.domain;

import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilege {
    private String id;
    private String roleId;
    private Privilege privilege;
    private String createdBy;
    private OffsetDateTime createdAt;
}
