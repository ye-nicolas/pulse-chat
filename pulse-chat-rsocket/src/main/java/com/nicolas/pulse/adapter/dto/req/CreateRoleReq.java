package com.nicolas.pulse.adapter.dto.req;

import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleReq {
    private String roleName;
    private Set<Privilege> privileges;
    private String remark;
}
