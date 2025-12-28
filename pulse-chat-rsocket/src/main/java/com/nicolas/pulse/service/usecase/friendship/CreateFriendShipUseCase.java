package com.nicolas.pulse.service.usecase.friendship;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.domain.FriendShip;
import com.nicolas.pulse.entity.enumerate.FriendShipStatus;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.FriendShipRepository;
import com.nicolas.pulse.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CreateFriendShipUseCase {
    private final AccountRepository accountRepository;
    private final FriendShipRepository friendShipRepository;

    public CreateFriendShipUseCase(AccountRepository accountRepository,
                                   FriendShipRepository friendShipRepository) {
        this.accountRepository = accountRepository;
        this.friendShipRepository = friendShipRepository;
    }

    @Transactional
    public Mono<Void> execute(Input input, Output output) {
        return this.validateFriendShipExists(input.getRecipientAccountId())
                .then(Mono.zip(SecurityUtil.getCurrentAccountId().flatMap(this::findAccount), findAccount(input.getRecipientAccountId())))
                .flatMap(tuple -> this.createFriendShip(tuple.getT1(), tuple.getT2()))
                .doOnNext(friendShipMono -> output.setFriendShipId(friendShipMono.getId()))
                .then();
    }

    @Transactional
    private Mono<FriendShip> createFriendShip(Account requesterAccount, Account recipientAccount) {
        return friendShipRepository.save(FriendShip.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .requesterAccount(requesterAccount)
                .recipientAccount(recipientAccount)
                .status(FriendShipStatus.PENDING)
                .build());
    }

    private Mono<Void> validateFriendShipExists(String recipientAccountId) {
        return SecurityUtil.getCurrentAccountId()
                .delayUntil(currentAccountId -> currentAccountId.equals(recipientAccountId)
                        ? Mono.error(() -> new ConflictException("Requester and Recipient are same."))
                        : Mono.empty())
                .flatMap(currentAccountId ->
                        friendShipRepository.existsByRequesterAccountIdAndRecipientAccountId(currentAccountId, recipientAccountId)
                                .flatMap(exists -> exists
                                        ? Mono.error(() -> new ConflictException("Friend ship already exists, requesterAccountId = '%s' and recipientAccountId = '%s'.".formatted(currentAccountId, recipientAccountId)))
                                        : Mono.empty())
                );
    }

    private Mono<Account> findAccount(String accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Account not found, id = '%s'.".formatted(accountId))));
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private String recipientAccountId;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private String friendShipId;
    }
}
