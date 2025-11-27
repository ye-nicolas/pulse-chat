package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.entity.enumerate.Privilege;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface RolePrivilegeDataRepositoryPeer extends R2dbcRepository<RolePrivilegeData, String> {
    Mono<RolePrivilegeData> findByRoleIdAndPrivilege(String roleId, Privilege privilege);

    Mono<Void> deleteByRoleId(String roleId);
}
