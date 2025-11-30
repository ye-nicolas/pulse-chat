package com.nicolas.pulse.service.usecase.account;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.AccountRoleRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;

    public ReactiveUserDetailsServiceImpl(AccountRepository accountRepository,
                                          AccountRoleRepository accountRoleRepository) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountRepository.findByName(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User name not found, name = '%s'".formatted(username))))
                .flatMap(account -> accountRoleRepository.findAllByAccountId(account.getId())
                        .flatMap(accountRole -> Flux.fromIterable(accountRole.getRole().getPrivilegeSet()))
                        .collect(Collectors.toSet())
                        .zipWith(Mono.just(account)))
                .map(tuple2 -> SecurityAccount.builder()
                        .id(tuple2.getT2().getId())
                        .username(tuple2.getT2().getName())
                        .password(tuple2.getT2().getPassword())
                        .state(tuple2.getT2().isActive())
                        .privilegeSet(tuple2.getT1())
                        .build());
    }
}
