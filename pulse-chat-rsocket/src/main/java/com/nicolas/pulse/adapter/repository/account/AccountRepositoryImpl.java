package com.nicolas.pulse.adapter.repository.account;

import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.service.repository.AccountRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final AccountDataRepositoryPeer peer;

    public AccountRepositoryImpl(AccountDataRepositoryPeer peer) {
        this.peer = peer;
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

    @Transactional
    @Override
    public Mono<Account> save(Account account) {
        AccountData accountData = AccountDataMapper.domainToData(account);
        return peer.save(accountData).map(AccountDataMapper::dataToDomain);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return peer.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return peer.existsByName(name);
    }
}
