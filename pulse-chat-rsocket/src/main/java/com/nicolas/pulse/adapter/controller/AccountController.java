package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.mapper.AccountMapper;
import com.nicolas.pulse.adapter.dto.req.CreateAccountReq;
import com.nicolas.pulse.adapter.dto.response.AccountRes;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.usecase.account.CreateAccountUseCase;
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

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<AccountRes>> findAll() {
        return ResponseEntity.ok(accountRepository.findAll().map(AccountMapper::domainToRes));
    }

    @GetMapping("/{accountId}")
    public Mono<ResponseEntity<AccountRes>> findById(@PathVariable("accountId") String accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Account not found, id = '%s'.".formatted(accountId))))
                .map(AccountMapper::domainToRes)
                .map(ResponseEntity::ok);
    }
}
