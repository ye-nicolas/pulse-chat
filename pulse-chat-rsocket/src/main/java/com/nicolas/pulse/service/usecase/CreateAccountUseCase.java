package com.nicolas.pulse.service.usecase;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.service.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateAccountUseCase {
    private final AccountRepository accountRepository;

    public CreateAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Mono<Output> execute(Mono<Input> input) {
        return input.flatMap(i -> accountRepository.existsByName(i.getName())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalStateException("User name already exists."));
                            }
                            return Mono.just(i);
                        }))
                .flatMap(this::createUser).map(user -> new Output(user.getId()));
    }

    private Mono<Account> createUser(Input input) {
        String id = UlidCreator.getMonotonicUlid().toString();
        Account account = Account.builder()
                .id(id)
                .name(input.getName())
                .showName(input.getShowName())
                .password(input.getPassword())
                .isActive(false)
                .remark(input.getRemark())
                .createdBy(id)
                .updatedBy(id)
                .build();
        return accountRepository.create(account);
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
    @AllArgsConstructor
    public static class Output {
        private String userId;
    }
}
