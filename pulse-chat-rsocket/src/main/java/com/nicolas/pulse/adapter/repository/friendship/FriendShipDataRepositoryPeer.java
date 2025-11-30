package com.nicolas.pulse.adapter.repository.friendship;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface FriendShipDataRepositoryPeer extends R2dbcRepository<FriendShipData, String> {
    Mono<Boolean> existsByRequesterAccountIdAndRecipientAccountId(String requesterAccountId, String recipientAccountId);
}
