package com.nicolas.pulse.service.usecase;

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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class CreateAccountUseCase {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;

    public CreateAccountUseCase(AccountRepository accountRepository,
                                RoleRepository roleRepository,
                                AccountRoleRepository accountRoleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.accountRoleRepository = accountRoleRepository;
    }


    public Mono<Output> execute(Mono<Input> input) {
        return input.flatMap(this::validateNameNotExists)
                .flatMap(this::validateAllRolesExist)
                .flatMap(this::createUser)
                .flatMap(this::createAccountRole)
                .map(user -> new Output(user.getId()));
    }

    private Mono<Input> validateNameNotExists(Input input) {
        return accountRepository.existsByName(input.getName())
                .flatMap(exists -> exists
                        ? Mono.error(new ConflictException("User name already exists, name = '%s'.".formatted(input.getName())))
                        : Mono.just(input));
    }

    private Mono<Input> validateAllRolesExist(Input input) {
        return Flux.fromIterable(input.getRoleIdSet())
                .flatMap(roleRepository::existsById)
                .all(Boolean::booleanValue)
                .flatMap(exists -> exists
                        ? Mono.just(input)
                        : Mono.error(new TargetNotFoundException("Some roles do not exist.")));
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
                .roleList(input.roleIdSet.stream().map(roleId -> Role.builder().id(roleId).build()).toList())
                .build();
        return accountRepository.create(account);
    }

    private Mono<Account> createAccountRole(Account account) {
        return accountRoleRepository.saveAll(Flux.fromIterable(account.getRoleList())
                        .map(role -> AccountRole.builder()
                                .id(UlidCreator.getMonotonicUlid().toString())
                                .accountId(account.getId())
                                .roleId(role.getId())
                                .createdBy(account.getId())
                                .build()))
                .then(Mono.just(account));
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
    @AllArgsConstructor
    public static class Output {
        private String userId;
    }
}
