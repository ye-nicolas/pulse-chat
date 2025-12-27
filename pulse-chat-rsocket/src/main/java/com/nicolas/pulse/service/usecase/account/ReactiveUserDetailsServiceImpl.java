package com.nicolas.pulse.service.usecase.account;

import com.nicolas.pulse.entity.domain.Account;
import com.nicolas.pulse.entity.domain.SecurityAccount;
import com.nicolas.pulse.entity.domain.chat.ChatRoom;
import com.nicolas.pulse.entity.domain.chat.ChatRoomMember;
import com.nicolas.pulse.entity.enumerate.Privilege;
import com.nicolas.pulse.entity.exception.TargetNotFoundException;
import com.nicolas.pulse.service.repository.AccountRepository;
import com.nicolas.pulse.service.repository.AccountRoleRepository;
import com.nicolas.pulse.service.repository.ChatRoomMemberRepository;
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
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public ReactiveUserDetailsServiceImpl(AccountRepository accountRepository,
                                          AccountRoleRepository accountRoleRepository,
                                          ChatRoomMemberRepository chatRoomMemberRepository) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountRepository.findByName(username)
                .switchIfEmpty(Mono.error(() -> new UsernameNotFoundException("Account not found, name = '%s'".formatted(username))))
                .flatMap(this::process);
    }

    public Mono<SecurityAccount> findById(String id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new TargetNotFoundException("Account not found, id = '%s'".formatted(id))))
                .flatMap(this::process);
    }

    private Mono<SecurityAccount> process(Account account) {
        return Mono.zip(Mono.just(account), getPrivilege(account.getId()), getRoomId(account.getId()))
                .map(tuple -> createSecurityAccount(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    private Mono<Set<Privilege>> getPrivilege(String accountId) {
        return accountRoleRepository.findAllByAccountId(accountId)
                .flatMap(accountRole -> Flux.fromIterable(accountRole.getRole().getPrivilegeSet()))
                .collect(Collectors.toSet());
    }

    private SecurityAccount createSecurityAccount(Account account, Set<Privilege> privilegeSet, Set<String> roomIdSet) {
        return SecurityAccount.builder()
                .id(account.getId())
                .username(account.getName())
                .password(account.getPassword())
                .state(account.isActive())
                .privilegeSet(privilegeSet)
                .roomIdSet(roomIdSet)
                .build();
    }

    private Mono<Set<String>> getRoomId(String accountId) {
        return chatRoomMemberRepository.findAllByAccountId(accountId)
                .map(ChatRoomMember::getChatRoom)
                .map(ChatRoom::getId)
                .collect(Collectors.toSet());
    }
}
