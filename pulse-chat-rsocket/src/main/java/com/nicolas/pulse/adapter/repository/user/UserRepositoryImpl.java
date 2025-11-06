package com.nicolas.pulse.adapter.repository.user;

import com.nicolas.pulse.entity.domain.User;
import com.nicolas.pulse.service.repository.UserRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserDataRepositoryPeer peer;

    public UserRepositoryImpl(UserDataRepositoryPeer userDataRepositoryPeer1) {
        this.peer = userDataRepositoryPeer1;
    }

    @Override
    public Flux<User> findAll() {
        return peer.findAll().map(UserDataMapper::dataToDomain);
    }

    @Override
    public Mono<User> findById(UUID id) {
        return peer.findById(id).map(UserDataMapper::dataToDomain);
    }

    @Override
    public Mono<User> findById(String id) {
        return peer.findById(UUID.fromString(id)).map(UserDataMapper::dataToDomain);
    }

    @Override
    public Mono<User> create(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        UserData userData = UserDataMapper.domainToData(user);
        return peer.save(userData).map(UserDataMapper::dataToDomain);
    }

    @Override
    public Mono<User> update(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        UserData userData = UserDataMapper.domainToData(user);
        return peer.save(userData).map(UserDataMapper::dataToDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return peer.deleteById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return peer.deleteById(UUID.fromString(id));
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return peer.existsByName(name);
    }
}
