package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository {
    Flux<Account> findAll();

    Mono<Account> findById(String id);

    Mono<Account> findByName(String name);

    Mono<Account> create(Account account);

    Mono<Account> update(Account account);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsByName(String name);
}
