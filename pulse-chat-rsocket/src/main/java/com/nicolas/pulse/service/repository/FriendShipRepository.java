package com.nicolas.pulse.service.repository;

import com.nicolas.pulse.entity.domain.FriendShip;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FriendShipRepository {
    Flux<FriendShip> findAllByAccountIdAndStatus(String accountId, FriendShipStatus status);

    Mono<FriendShip> findById(String id);

    Mono<FriendShip> insert(FriendShip friendShip);

    Mono<FriendShip> update(FriendShip friendShip);

    Mono<Void> deleteById(String id);

    Mono<Boolean> existsByRequesterAccountIdAndRecipientAccountId(String requesterAccountId, String recipientAccountId);
}
