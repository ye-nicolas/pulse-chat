package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.AccountRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountRoleRepository {
    Flux<AccountRole> findAllByAccountId(String accountId);

    Flux<AccountRole> findAllByRoleId(String roleId);

    Flux<AccountRole> saveAll(List<AccountRole> accountRoleFlux);

    Mono<Boolean> existsByAccountIdAndRoleId(String accountId, String roleId);

    Mono<Void> deleteById(String id);

    Mono<Void> deleteByAccountId(String accountId);

    Mono<Void> deleteByRoleId(String roleId);
}
