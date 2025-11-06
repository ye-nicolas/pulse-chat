package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {
    Flux<User> findAll();

    Mono<User> findById(UUID id);

    Mono<User> findById(String id);

    Mono<User> create(User user);

    Mono<User> update(User user);

    Mono<Void> deleteById(UUID id);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsByName(String name);
}
