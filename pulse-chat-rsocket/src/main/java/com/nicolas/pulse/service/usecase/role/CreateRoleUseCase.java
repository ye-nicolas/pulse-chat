package com.nicolas.pulse.service.usecase.role;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nicolas.pulse.entity.domain.Role;
import com.nicolas.pulse.entity.domain.RolePrivilege;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.entity.exception.ConflictException;
import com.nicolas.pulse.service.repository.RolePrivilegeRepository;
import com.nicolas.pulse.service.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class CreateRoleUseCase {
    private final RoleRepository roleRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;

    public CreateRoleUseCase(RoleRepository roleRepository,
                             RolePrivilegeRepository rolePrivilegeRepository) {
        this.roleRepository = roleRepository;
        this.rolePrivilegeRepository = rolePrivilegeRepository;
    }

    public Mono<Output> execute(Mono<Input> input) {
        return input.flatMap(this::validateNameNotExists)
                .flatMap(this::createRole)
                .flatMap(this::createRolePrivilege)
                .map(role -> new Output(role.getId()));
    }

    private Mono<Role> createRole(Input input) {
        return roleRepository.save(Role.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .name(input.getRoleName())
                .privilegeSet(input.getPrivileges())
                .remark(input.getRemark())
                .build());
    }

    private Mono<Role> createRolePrivilege(Role role) {
        return rolePrivilegeRepository.insert(Flux.fromIterable(role.getPrivilegeSet())
                        .map(privilege -> RolePrivilege.builder()
                                .id(UlidCreator.getMonotonicUlid().toString())
                                .roleId(role.getId())
                                .privilege(privilege)
                                .build()))
                .then(Mono.just(role));
    }

    private Mono<Input> validateNameNotExists(Input input) {
        return roleRepository.existsByName(input.getRoleName())
                .flatMap(exists -> exists
                        ? Mono.error(new ConflictException("Role name already exists, name = '%s'.".formatted(input.getRoleName())))
                        : Mono.just(input));
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String roleName;
        private Set<Privilege> privileges;
        private String remark;
    }

    @Data
    @AllArgsConstructor
    public static class Output {
        private String roleId;
    }
}
