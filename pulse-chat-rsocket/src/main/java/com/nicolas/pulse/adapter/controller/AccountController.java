package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.mapper.AccountMapper;
import com.nicolas.pulse.adapter.dto.res.AccountRes;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
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
