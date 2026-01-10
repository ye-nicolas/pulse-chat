package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.mapper.AccountMapper;
import com.nicolas.pulse.adapter.dto.res.AccountRes;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.util.SecurityUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.nicolas.pulse.adapter.controller.AccountController.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
public class AccountController {
    public static final String BASE_URL = "/accounts";
    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping(path = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AccountRes>> findById(@PathVariable("accountId") String accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Account not found, id = '%s'.".formatted(accountId))))
                .delayUntil(account -> SecurityUtil.getCurrentAccountId()
                        .flatMap(currentAccountId -> currentAccountId.equals(account.getId())
                                ? Mono.empty()
                                : Mono.error(() -> new AccessDeniedException("Cant get other account."))))
                .map(AccountMapper::domainToRes)
                .map(ResponseEntity::ok);
    }
}
