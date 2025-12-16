package com.nicolas.pulse.entity.domain;

import com.nicolas.pulse.entity.enumerate.Privilege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class SecurityAccount implements UserDetails {
    public static final String USER_NAME = "userName";
    public static final String PRIVILEGE = "privilege";
    public static final String ROOM = "room";
    private String id;
    private String username;
    private String password;
    private Set<GrantedAuthority> grantedAuthoritySet;
    @Builder.Default
    private Set<Privilege> privilegeSet = Set.of();
    @Builder.Default
    private Set<String> roomIdSet = Set.of();
    private boolean state;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(grantedAuthoritySet)) {
            grantedAuthoritySet = privilegeSet.stream().map(privilege -> new SimpleGrantedAuthority(privilege.name())).collect(Collectors.toSet());
        }
        return grantedAuthoritySet;
    }

    @Override
    public boolean isEnabled() {
        return state;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Map<String, Object> toMap() {
        return Map.of(
                USER_NAME, username,
                PRIVILEGE, privilegeSet,
                ROOM, roomIdSet
        );
    }
}
