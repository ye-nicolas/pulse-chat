package com.nicolas.pulse.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class SecurityAccount implements UserDetails {
    public static final String USER_NAME = "userName";
    @Builder.Default
    private Set<GrantedAuthority> grantedAuthoritySet = Set.of();
    private String id;
    private String username;
    private String password;
    private boolean state;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
        return Map.of(USER_NAME, username);
    }
}
