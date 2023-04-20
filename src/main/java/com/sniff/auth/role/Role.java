package com.sniff.auth.role;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER;
    private final String roleName = "ROLE_" + name();

    @Override
    public String getAuthority() {
        return roleName;
    }
}
