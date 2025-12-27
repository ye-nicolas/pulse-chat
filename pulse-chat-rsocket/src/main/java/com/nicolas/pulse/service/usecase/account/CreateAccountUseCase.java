package com.nicolas.pulse.service.usecase.account;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.service.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CreateAccountUseCase {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public CreateAccountUseCase(PasswordEncoder passwordEncoder,
                                AccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Mono<Void> execute(Input input, Output output) {
        return this.validateNameNotExists(input.getName())
                .then(this.createUser(input))
                .doOnNext(account -> output.setAccountId(account.getId()))
                .then();
    }

    private Mono<Account> createUser(Input input) {
        return accountRepository.save(Account.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .name(input.getName())
                .showName(input.getShowName())
                .password(passwordEncoder.encode(input.getPassword()))
                .isActive(true)
                .remark(input.getRemark())
                .build());
    }

    private Mono<Void> validateNameNotExists(String name) {
        return accountRepository.existsByName(name)
                .flatMap(exists -> exists
                        ? Mono.error(() -> new ConflictException("User name already exists, name = '%s'.".formatted(name)))
                        : Mono.empty());
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String name;
        private String showName;
        private String password;
        private String remark;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private String accountId;
    }
}
