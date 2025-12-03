package com.nicolas.pulse.service.usecase.account;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.domain.AccountRole;
import com.nicolas.pulse.entity.domain.Role;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.AccountRoleRepository;
import com.nicolas.pulse.service.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class CreateAccountUseCase {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    public CreateAccountUseCase(PasswordEncoder passwordEncoder,
                                AccountRepository accountRepository,
                                RoleRepository roleRepository,
                                AccountRoleRepository accountRoleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.accountRoleRepository = accountRoleRepository;
    }

    public Mono<Void> execute(Input input, Output output) {
        return Mono.when(
                        this.validateNameNotExists(input.getName()),
                        this.validateAllRolesExist(input.getRoleIdSet())
                ).then(this.createUser(input))
                .doOnSuccess(account -> output.setAccountId(account.getId()))
                .flatMap(account -> this.createAccountRole(account, input.getRoleIdSet()))
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
                        ? Mono.error(new ConflictException("User name already exists, name = '%s'.".formatted(name)))
                        : Mono.empty());
    }

    private Mono<Void> validateAllRolesExist(Set<String> roleIdSet) {
        return Flux.fromIterable(roleIdSet)
                .flatMap(roleRepository::existsById)
                .all(Boolean::booleanValue)
                .flatMap(exists -> exists
                        ? Mono.empty()
                        : Mono.error(new TargetNotFoundException("Some roles do not exist.")));
    }

    private Mono<Void> createAccountRole(Account account, Set<String> roleIdSet) {
        return accountRoleRepository.saveAll(roleIdSet.stream()
                        .map(id -> AccountRole.builder()
                                .id(UlidCreator.getMonotonicUlid().toString())
                                .accountId(account.getId())
                                .role(Role.builder().id(id).build())
                                .createdBy(account.getId())
                                .build())
                        .toList())
                .then();
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String name;
        private String showName;
        private String password;
        private Set<String> roleIdSet;
        private String remark;
    }

    @Data
    @NoArgsConstructor
    public static class Output {
        private String accountId;
    }
}
