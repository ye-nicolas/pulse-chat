package com.nicolas.pulse.adapter.repository.user;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserDataRepositoryPeer extends R2dbcRepository<UserData, UUID> {
    Mono<Boolean> existsByName(String name);
}
