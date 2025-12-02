package com.nicolas.pulse.service.usecase.friendship;

import com.nicolas.pulse.entity.domain.FriendShip;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.FriendShipRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UpdateFriendShipStatusToAcceptedUseCase {
    private final FriendShipRepository friendShipRepository;

    public UpdateFriendShipStatusToAcceptedUseCase(FriendShipRepository friendShipRepository) {
        this.friendShipRepository = friendShipRepository;
    }

    public Mono<Void> execute(Input input) {
        return friendShipRepository.findById(input.getFriendShipId())
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Friend ship not found, id = '%s'.".formatted(input.getFriendShipId()))))
                .flatMap(f -> this.validateStatusIsPending(f).then(Mono.defer(() -> Mono.just(f))))
                .flatMap(this::updateStatus);
    }

    private Mono<Void> validateStatusIsPending(FriendShip friendShip) {
        if (friendShip.getStatus() != FriendShipStatus.PENDING) {
            return Mono.error(new ConflictException("Friendship status is '%s', cannot be accepted. Only PENDING status can be updated.".formatted(friendShip.getStatus()))
            );
        }
        return Mono.empty();
    }

    public Mono<Void> updateStatus(FriendShip friendShip) {
        friendShip.setStatus(FriendShipStatus.ACCEPTED);
        return friendShipRepository.save(friendShip)
                .then();
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String friendShipId;
    }
}
