package com.nicolas.pulse.adapter.repository.roleprivilege;

import com.nicolas.pulse.entity.domain.RolePrivilege;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.service.repository.RolePrivilegeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class RolePrivilegeRepositoryImpl implements RolePrivilegeRepository {
    private final RolePrivilegeDataRepositoryPeer peer;

    public RolePrivilegeRepositoryImpl(RolePrivilegeDataRepositoryPeer peer) {
        this.peer = peer;
    }

    @Override
    public Mono<RolePrivilege> findByRoleIdAndPrivilege(String roleId, Privilege privilege) {
        return peer.findByRoleIdAndPrivilege(roleId, privilege).map(RolePrivilegeDataMapper::dataToDomain);
    }

    @Transactional
    @Override
    public Flux<RolePrivilege> saveAll(List<RolePrivilege> rolePrivilegeFlux) {
        return peer.saveAll(rolePrivilegeFlux.stream().map(RolePrivilegeDataMapper::domainToData).toList())
                .map(RolePrivilegeDataMapper::dataToDomain);
    }

    @Transactional
    @Override
    public Mono<Void> deleteByRoleId(String roleId) {
        return peer.deleteByRoleId(roleId);
    }
}
