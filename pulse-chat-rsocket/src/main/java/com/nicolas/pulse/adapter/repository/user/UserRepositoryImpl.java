package com.nicolas.pulse.adapter.repository.user;

import com.nicolas.pulse.entity.domain.User;
import com.nicolas.pulse.service.repository.UserRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserDataRepositoryPeer peer;
    private final R2dbcEntityOperations r2dbcEntityOperations;

    public UserRepositoryImpl(UserDataRepositoryPeer peer,
                              R2dbcEntityOperations r2dbcEntityOperations) {
        this.peer = peer;
        this.r2dbcEntityOperations = r2dbcEntityOperations;
    }

    @Override
    public Flux<User> findAll() {
        return peer.findAll().map(UserDataMapper::dataToDomain);
    }

    @Override
    public Mono<User> findById(String id) {
        return peer.findById(id).map(UserDataMapper::dataToDomain);
    }

    @Override
    public Mono<User> create(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        UserData userData = UserDataMapper.domainToData(user);
        return r2dbcEntityOperations.insert(userData).map(UserDataMapper::dataToDomain);
    }

    @Override
    public Mono<User> update(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        UserData userData = UserDataMapper.domainToData(user);
        return r2dbcEntityOperations.update(userData).map(UserDataMapper::dataToDomain);
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
