package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilegeId {
    private String roleId;
    private Privilege privilege;
}
