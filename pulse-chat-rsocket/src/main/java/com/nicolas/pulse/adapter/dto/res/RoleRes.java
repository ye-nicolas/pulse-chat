package com.nicolas.pulse.adapter.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleRes {
    private String id;
    private String name;
    private Set<Privilege> privilegeSet;
    private String remark;
    private String createdBy;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    private Instant updatedAt;
}
