package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.FriendShip;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FriendShipRepository {
    Flux<FriendShip> findAllByAccountId(String accountId);

    Mono<FriendShip> findById(String id);

    Mono<FriendShip> save(FriendShip friendShip);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsByRequesterAccountIdAndRecipientAccountId(String requesterAccountId, String recipientAccountId);
}
