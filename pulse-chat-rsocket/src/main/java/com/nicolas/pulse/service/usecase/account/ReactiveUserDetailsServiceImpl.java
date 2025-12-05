package com.nicolas.pulse.service.usecase.account;

import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.AccountRoleRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
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
                .flatMap(account -> getPrivilege(account.getId()).zipWith(Mono.just(account)))
                .map(t -> createSecurityAccount(t.getT2(), t.getT1()));
    }

    private Mono<Set<Privilege>> getPrivilege(String accountId) {
        return accountRoleRepository.findAllByAccountId(accountId)
                .flatMap(accountRole -> Flux.fromIterable(accountRole.getRole().getPrivilegeSet()))
                .collect(Collectors.toSet());
    }

    private SecurityAccount createSecurityAccount(Account account, Set<Privilege> privilegeSet) {
        return SecurityAccount.builder()
                .id(account.getId())
                .username(account.getName())
                .password(account.getPassword())
                .state(account.isActive())
                .privilegeSet(privilegeSet)
                .build();
    }
}
