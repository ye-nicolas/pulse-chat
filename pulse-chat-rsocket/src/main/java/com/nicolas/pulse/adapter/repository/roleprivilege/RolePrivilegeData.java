package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("role_privilege")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilegeData {
    @Id
    @Column("role_id")
    private String roleId;
    @Id
    @Column("privilege")
    private Privilege privilege;
    @Column("created_by")
    private String createdBy;
    @Column("created_at")
    private Instant createdAt;
}
