package com.sniff.auth.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthVerifyService {
    public boolean isAuthenticated() {
        return getIdFromSubject() != 0L;
    }

    public long getIdFromSubject() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Jwt jwt) {
            return Long.parseLong(jwt.getSubject());
        }
        return 0L;
    }
}
