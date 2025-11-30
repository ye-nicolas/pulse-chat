package com.nicolas.pulse.adapter.repository.account;

import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.service.repository.AccountRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final AccountDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public AccountRepositoryImpl(AccountDataRepositoryPeer peer,
                                 R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Flux<Account> findAll() {
        return peer.findAll().map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Account> findById(String id) {
        return peer.findById(id).map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Account> findByName(String name) {
        return peer.findByName(name).map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Account> create(Account account) {
        OffsetDateTime now = OffsetDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        AccountData accountData = AccountDataMapper.domainToData(account);
        return r2dbcEntityOperations.insert(accountData).map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Account> update(Account account) {
        account.setUpdatedAt(OffsetDateTime.now());
        AccountData accountData = AccountDataMapper.domainToData(account);
        return r2dbcEntityOperations.update(accountData).map(AccountDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return peer.existsByName(name);
    }
}
