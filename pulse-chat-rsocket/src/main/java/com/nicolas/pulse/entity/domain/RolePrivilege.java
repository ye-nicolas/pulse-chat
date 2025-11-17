package com.nicolas.pulse.entity.domain;

import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilege {
    private String roleId;
    private Privilege privilege;
    private String createdBy;
    private Instant createdAt;
}
