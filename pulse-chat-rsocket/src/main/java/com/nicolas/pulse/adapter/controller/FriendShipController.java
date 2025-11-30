package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.req.CreateFriendShipReq;
import com.nicolas.pulse.service.usecase.friendship.CreateFriendShipUseCase;
import com.nicolas.pulse.service.usecase.friendship.UpdateFriendShipStatusToAcceptedUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(FriendShipController.FRIEND_SHIP_BASE_URL)
public class FriendShipController {
    public static final String FRIEND_SHIP_BASE_URL = "/friend-ship";
    private final CreateFriendShipUseCase createFriendShipUseCase;
    private final UpdateFriendShipStatusToAcceptedUseCase updateFriendShipStatusToAcceptedUseCase;

    public FriendShipController(CreateFriendShipUseCase createFriendShipUseCase,
                                UpdateFriendShipStatusToAcceptedUseCase updateFriendShipStatusToAcceptedUseCase) {
        this.createFriendShipUseCase = createFriendShipUseCase;
        this.updateFriendShipStatusToAcceptedUseCase = updateFriendShipStatusToAcceptedUseCase;
    }

    @PostMapping("/")
    public Mono<ResponseEntity<String>> createFriendShip(@Valid @RequestBody Mono<CreateFriendShipReq> dto) {
        CreateFriendShipUseCase.Output output = new CreateFriendShipUseCase.Output();
        return dto.map(req -> new CreateFriendShipUseCase.Input(req.getRequesterAccountId(), req.getRecipientAccountId()))
                .flatMap(input -> createFriendShipUseCase.execute(input, output))
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok(output.getFriendShipId()))));
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<String>> createFriendShip(@PathVariable("id") String id) {
        return Mono.defer(() -> Mono.just(new UpdateFriendShipStatusToAcceptedUseCase.Input(id)))
                .flatMap(updateFriendShipStatusToAcceptedUseCase::execute)
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok(id))));
    }
}
