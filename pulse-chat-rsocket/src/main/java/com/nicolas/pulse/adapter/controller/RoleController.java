package com.nicolas.pulse.adapter.controller;

import com.nicolas.pulse.adapter.dto.mapper.RoleMapper;
import com.nicolas.pulse.adapter.dto.req.CreateRoleReq;
import com.nicolas.pulse.adapter.dto.response.RoleRes;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.RoleRepository;
import com.nicolas.pulse.service.usecase.role.CreateRoleUseCase;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleRepository roleRepository;
    private final CreateRoleUseCase createRoleUseCase;

    public RoleController(RoleRepository roleRepository,
                          CreateRoleUseCase createRoleUseCase) {
        this.roleRepository = roleRepository;
        this.createRoleUseCase = createRoleUseCase;
    }


    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Flux<RoleRes>> findAll() {
        return ResponseEntity.ok(roleRepository.findAll().map(RoleMapper::domainToRes));
    }

    @GetMapping("/{roleId}")
    public Mono<ResponseEntity<RoleRes>> findById(@PathVariable("roleId") String roleId) {
        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new TargetNotFoundException("Role not found, id = '%s'.".formatted(roleId))))
                .map(RoleMapper::domainToRes)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/")
    public Mono<ResponseEntity<String>> createRole(@Valid @RequestBody Mono<CreateRoleReq> req) {
        return req.map(r -> CreateRoleUseCase.Input.builder()
                        .roleName(r.getRoleName())
                        .privileges(r.getPrivileges())
                        .remark(r.getRemark())
                        .build())
                .transform(createRoleUseCase::execute)
                .map(output -> ResponseEntity.ok().body(output.getRoleId()));
    }
}
