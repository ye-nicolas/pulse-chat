package com.nicolas.pulse.adapter.repository.role;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface RoleDataRepositoryPeer extends R2dbcRepository<RoleData, String> {
   Mono<Boolean> existsByName(String name);
}
