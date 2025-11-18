package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Flux<Role> findAll();

    Mono<Role> findById(String id);

    Mono<Boolean> existsByName(String name);

    Mono<Role> save(Role role);

    Mono<Void> deleteById(String id);
}
