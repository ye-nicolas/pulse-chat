package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.entity.domain.RolePrivilege;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.service.repository.RolePrivilegeRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Repository
public class RolePrivilegeRepositoryImpl implements RolePrivilegeRepository {
    private final RolePrivilegeDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public RolePrivilegeRepositoryImpl(RolePrivilegeDataRepositoryPeer peer,
                                       R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Mono<RolePrivilege> findByRoleIdAndPrivilege(String roleId, Privilege privilege) {
        return peer.findByRoleIdAndPrivilege(roleId, privilege).map(RolePrivilegeDataMapper::dataToDomain);
    }

    @Override
    public Flux<RolePrivilege> insert(Flux<RolePrivilege> rolePrivilegeFlux) {
        return rolePrivilegeFlux.map(rolePrivilege -> {
                    rolePrivilege.setCreatedAt(OffsetDateTime.now());
                    return RolePrivilegeDataMapper.domainToData(rolePrivilege);
                })
                .window(10)
                .flatMap(batch -> batch.flatMap(r2dbcEntityOperations::insert), 32)
                .map(RolePrivilegeDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteByRoleId(String roleId) {
        return peer.deleteByRoleId(roleId);
    }
}
