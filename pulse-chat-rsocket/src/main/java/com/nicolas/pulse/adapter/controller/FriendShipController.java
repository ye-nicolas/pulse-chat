package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.req.CreateFriendShipReq;
import com.nicolas.pulse.entity.domain.FriendShip;
import com.nicolas.pulse.service.repository.FriendShipRepository;
import com.nicolas.pulse.service.usecase.friendship.CreateFriendShipUseCase;
import com.nicolas.pulse.service.usecase.friendship.UpdateFriendShipStatusToAcceptedUseCase;
import com.nicolas.pulse.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(FriendShipController.FRIEND_SHIP_BASE_URL)
public class FriendShipController {
    public static final String FRIEND_SHIP_BASE_URL = "/friend-ships";
    private final FriendShipRepository friendShipRepository;
    private final CreateFriendShipUseCase createFriendShipUseCase;
    private final UpdateFriendShipStatusToAcceptedUseCase updateFriendShipStatusToAcceptedUseCase;

    public FriendShipController(FriendShipRepository friendShipRepository,
                                CreateFriendShipUseCase createFriendShipUseCase,
                                UpdateFriendShipStatusToAcceptedUseCase updateFriendShipStatusToAcceptedUseCase) {
        this.friendShipRepository = friendShipRepository;
        this.createFriendShipUseCase = createFriendShipUseCase;
        this.updateFriendShipStatusToAcceptedUseCase = updateFriendShipStatusToAcceptedUseCase;
    }

    @GetMapping("/")
    public ResponseEntity<Flux<FriendShip>> findAllByAccount() {
        return ResponseEntity.ok(SecurityUtil.getCurrentAccountId()
                .flatMapMany(friendShipRepository::findAllByAccountId));
    }

    @PostMapping("/")
    public Mono<ResponseEntity<String>> createFriendShip(@Valid @RequestBody Mono<CreateFriendShipReq> reqMono) {
        CreateFriendShipUseCase.Output output = new CreateFriendShipUseCase.Output();
        return reqMono.map(req -> new CreateFriendShipUseCase.Input(req.getRecipientAccountId()))
                .flatMap(input -> createFriendShipUseCase.execute(input, output))
                .then(Mono.fromSupplier(() -> ResponseEntity.ok(output.getFriendShipId())));
    }

    @PatchMapping("/{friendShipId}")
    public Mono<ResponseEntity<Void>> updateFriendShipStatusToAccepted(@PathVariable("friendShipId") String friendShipId) {
        return updateFriendShipStatusToAcceptedUseCase.execute(new UpdateFriendShipStatusToAcceptedUseCase.Input(friendShipId))
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
