package com.nicolas.pulse.adapter.repository.accountrole;

import com.nicolas.pulse.entity.domain.AccountRole;
import com.nicolas.pulse.service.repository.AccountRoleRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public class AccountRoleRepositoryImpl implements AccountRoleRepository {
    private final AccountRoleDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public AccountRoleRepositoryImpl(AccountRoleDataRepositoryPeer peer,
                                     R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Flux<AccountRole> findAllByAccountId(String accountId) {
        return peer.findAllByAccountId(accountId).map(AccountRoleDataMapper::dataToDomain);
    }

    @Override
    public Flux<AccountRole> findAllByRoleId(String roleId) {
        return peer.findAllByRoleId(roleId).map(AccountRoleDataMapper::dataToDomain);
    }

    @Override
    public Flux<AccountRole> saveAll(Flux<AccountRole> accountRoleFlux) {
        return accountRoleFlux.map(accountRole -> {
                    accountRole.setCreatedAt(Instant.now());
                    return AccountRoleDataMapper.domainToData(accountRole);
                })
                .window(10)
                .flatMap(batch -> batch.flatMap(r2dbcEntityOperations::insert), 10)
                .map(AccountRoleDataMapper::dataToDomain);
    }

    @Override
    public Mono<Boolean> existsByAccountIdAndRoleId(String accountId, String roleId) {
        return peer.existsByAccountIdAndRoleId(accountId, roleId);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }

    @Override
    public Mono<Void> deleteByAccountId(String accountId) {
        return peer.deleteByAccountId(accountId);
    }

    @Override
    public Mono<Void> deleteByRoleId(String roleId) {
        return peer.deleteByRoleId(roleId);
    }
}
