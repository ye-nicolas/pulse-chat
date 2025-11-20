package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.mapper.AccountMapper;
import com.nicolas.pulse.adapter.dto.req.CreateAccountReq;
import com.nicolas.pulse.adapter.dto.response.AccountRes;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.usecase.CreateAccountUseCase;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class AccountController {
    private final AccountRepository accountRepository;
    private final CreateAccountUseCase createAccountUseCase;

    public AccountController(AccountRepository accountRepository,
                             CreateAccountUseCase createAccountUseCase) {
        this.accountRepository = accountRepository;
        this.createAccountUseCase = createAccountUseCase;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<AccountRes>> findAll() {
        return ResponseEntity.ok(accountRepository.findAll().map(AccountMapper::domainToRes));
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<AccountRes>> findById(@PathVariable("userId") String userId) {
        return accountRepository.findById(userId)
                .switchIfEmpty(Mono.error(new TargetNotFoundException("User not found, id = '%s'.".formatted(userId))))
                .map(AccountMapper::domainToRes)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/")
    public Mono<ResponseEntity<String>> createUser(@Valid @RequestBody Mono<CreateAccountReq> req) {
        return req.map(r -> CreateAccountUseCase.Input.builder()
                        .name(r.getName())
                        .showName(r.getShowName())
                        .password(r.getPassword())
                        .remark(r.getRemark())
                        .roleIdSet(r.getRoleIdSet())
                        .build())
                .transform(createAccountUseCase::execute)
                .map(output -> ResponseEntity.ok().body(output.getUserId()));
    }
}
