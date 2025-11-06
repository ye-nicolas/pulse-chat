package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.mapper.UserMapper;
import com.nicolas.pulse.adapter.dto.response.UserRes;
import com.nicolas.pulse.service.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<UserRes>> findAll() {
        return ResponseEntity.ok(userRepository.findAll().map(UserMapper::domainToRes));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Mono<UserRes>> findById(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(userRepository.findById(userId).map(UserMapper::domainToRes));
    }
}
