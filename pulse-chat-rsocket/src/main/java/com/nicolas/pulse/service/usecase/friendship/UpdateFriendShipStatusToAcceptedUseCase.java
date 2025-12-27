package com.nicolas.pulse.service.usecase.friendship;

import com.nicolas.pulse.entity.domain.FriendShip;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.FriendShipRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.access.AccessDeniedException;
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
                .delayUntil(friendShip -> Mono.when(
                        this.validateStatusIsPending(friendShip),
                        this.validateIsRecipient(friendShip)))
                .flatMap(this::updateStatus);
    }

    private Mono<Void> validateStatusIsPending(FriendShip friendShip) {
        if (friendShip.getStatus() != FriendShipStatus.PENDING) {
            return Mono.error(new ConflictException("Friendship status is '%s', cannot be accepted. Only PENDING status can be updated.".formatted(friendShip.getStatus()))
            );
        }
        return Mono.empty();
    }

    private Mono<Void> validateIsRecipient(FriendShip friendShip) {
        return SecurityUtil.getCurrentAccountId()
                .filter(accountId -> accountId.equals(friendShip.getRecipientAccount().getId()))
                .switchIfEmpty(Mono.error(new AccessDeniedException("Current user is not the recipient of friendship '%s'.".formatted(friendShip.getId()))))
                .then();
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
