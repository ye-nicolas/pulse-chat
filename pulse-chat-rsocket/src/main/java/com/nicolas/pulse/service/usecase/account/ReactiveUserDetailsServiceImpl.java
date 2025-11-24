package com.nicolas.pulse.service.usecase.account;

import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.service.repository.AccountRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final AccountRepository accountRepository;

    public ReactiveUserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountRepository.findByName(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User name not found, name '%s'".formatted(username))))
                .map(account -> SecurityAccount.builder()
                        .id(account.getId())
                        .username(account.getName())
                        .password(account.getPassword())
                        .state(account.isActive())
                        .privilegeSet(account.getRoleList().stream().flatMap(r -> r.getPrivilegeSet().stream()).collect(Collectors.toSet()))
                        .build());
    }
}
