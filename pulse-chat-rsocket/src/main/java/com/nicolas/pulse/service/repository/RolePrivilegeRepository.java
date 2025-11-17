package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.RolePrivilege;
import com.nicolas.pulse.entity.enumerate.Privilege;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolePrivilegeRepository {
    Mono<RolePrivilege> findByRoleIdAndPrivilege(String roleId, Privilege privilege);

    Flux<RolePrivilege> insert(Flux<RolePrivilege> rolePrivilegeFlux);

    Mono<Void> deleteByRoleId(String roleId);
}
