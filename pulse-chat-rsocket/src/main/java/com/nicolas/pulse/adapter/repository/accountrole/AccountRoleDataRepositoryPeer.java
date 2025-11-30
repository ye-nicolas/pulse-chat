package com.nicolas.pulse.adapter.repository.accountrole;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AccountRoleDataRepositoryPeer extends R2dbcRepository<AccountRoleData, String> {
    Mono<Boolean> existsByAccountIdAndRoleId(String accountId, String roleId);

    Mono<Void> deleteByAccountId(String accountId);

    Mono<Void> deleteByRoleId(String roleId);
}
