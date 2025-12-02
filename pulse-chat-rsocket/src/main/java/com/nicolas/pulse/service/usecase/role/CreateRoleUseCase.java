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
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
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

    public Mono<Void> execute(Input input, Output output) {
        return validateNameNotExists(input.getRoleName())
                .then(this.createRole(input))
                .doOnNext(role -> output.setRoleId(role.getId()))
                .flatMap(this::createRolePrivilege);
    }

    private Mono<Void> validateNameNotExists(String name) {
        return roleRepository.existsByName(name)
                .flatMap(exists -> exists
                        ? Mono.error(new ConflictException("Role name already exists, name = '%s'.".formatted(name)))
                        : Mono.empty());
    }

    private Mono<Role> createRole(Input input) {
        return roleRepository.save(Role.builder()
                .id(UlidCreator.getMonotonicUlid().toString())
                .name(input.getRoleName())
                .privilegeSet(input.getPrivileges())
                .remark(input.getRemark())
                .build());
    }

    private Mono<Void> createRolePrivilege(Role role) {
        return rolePrivilegeRepository.saveAll(role.getPrivilegeSet().stream()
                        .map(privilege -> RolePrivilege.builder()
                                .id(UlidCreator.getMonotonicUlid().toString())
                                .roleId(role.getId())
                                .privilege(privilege)
                                .build())
                        .toList())
                .then();
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
    @NoArgsConstructor
    public static class Output {
        private String roleId;
    }
}
