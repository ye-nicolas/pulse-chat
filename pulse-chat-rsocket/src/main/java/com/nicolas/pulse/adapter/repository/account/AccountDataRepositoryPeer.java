package com.nicolas.pulse.adapter.repository.account;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AccountDataRepositoryPeer extends R2dbcRepository<AccountData, String> {
    Mono<AccountData> findByName(String name);

    Mono<Boolean> existsByName(String name);
}
