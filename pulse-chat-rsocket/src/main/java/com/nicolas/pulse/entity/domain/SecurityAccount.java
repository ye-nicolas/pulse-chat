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
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class SecurityAccount implements UserDetails {
    private String id;
    private String username;
    private String password;
    private Set<Privilege> privilegeList;
    private Set<GrantedAuthority> grantedAuthoritySet;
    private boolean state;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(grantedAuthoritySet)) {
            grantedAuthoritySet = privilegeList.stream().map(privilege -> new SimpleGrantedAuthority(privilege.name())).collect(Collectors.toSet());
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
        return password;
    }
}
