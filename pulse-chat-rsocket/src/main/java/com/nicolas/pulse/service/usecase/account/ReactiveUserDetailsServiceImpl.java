package com.nicolas.pulse.service.usecase.account;

import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.nicolas.pulse.service.usecase.account.ReactiveUserDetailsServiceImpl.BEAN_NAME;

@Service(BEAN_NAME)
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {
    public static final String BEAN_NAME = "ReactiveUserDetailsServiceImpl";
    private final AccountRepository accountRepository;

    public ReactiveUserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountRepository.findByName(username)
                .switchIfEmpty(Mono.error(() -> new UsernameNotFoundException("Account not found, name = '%s'".formatted(username))))
                .map(this::createSecurityAccount);
    }

    public Mono<SecurityAccount> findById(String id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Account not found, id = '%s'".formatted(id))))
                .map(this::createSecurityAccount);
    }

    private SecurityAccount createSecurityAccount(Account account) {
        return SecurityAccount.builder()
                .id(account.getId())
                .username(account.getName())
                .password(account.getPassword())
                .state(account.isActive())
                .build();
    }
}
