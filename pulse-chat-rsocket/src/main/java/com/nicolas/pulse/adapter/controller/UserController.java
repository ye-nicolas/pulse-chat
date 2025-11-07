package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.mapper.UserMapper;
import com.nicolas.pulse.adapter.dto.req.CreateUserReq;
import com.nicolas.pulse.adapter.dto.response.UserRes;
import com.nicolas.pulse.service.repository.UserRepository;
import com.nicolas.pulse.service.usecase.CreateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final CreateUserUseCase createUserUseCase;

    public UserController(UserRepository userRepository,
                          CreateUserUseCase createUserUseCase) {
        this.userRepository = userRepository;
        this.createUserUseCase = createUserUseCase;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<UserRes>> findAll() {
        return ResponseEntity.ok(userRepository.findAll().map(UserMapper::domainToRes));
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserRes>> findById(@PathVariable("userId") String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found, id = '%s'.".formatted(userId))))
                .map(UserMapper::domainToRes).map(ResponseEntity::ok);
    }

    @PostMapping("/")
    public Mono<ResponseEntity<String>> createUser(@Valid @RequestBody Mono<CreateUserReq> req) {
        return req.map(r -> CreateUserUseCase.Input.builder()
                        .name(r.getName())
                        .showName(r.getShowName())
                        .password(r.getPassword())
                        .remark(r.getRemark())
                        .build())
                .transform(createUserUseCase::execute)
                .map(output -> ResponseEntity.ok().body(output.getUserId()));
    }
}
