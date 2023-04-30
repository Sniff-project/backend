package com.sniff.auth.service;

import com.sniff.auth.exception.DeniedAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthVerifyService {
    public boolean isAuthenticated() {
        return getIdFromSubject() != 0L;
    }

    public Long getIdFromSubject() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Jwt jwt) {
            return Long.parseLong(jwt.getSubject());
        }
        return 0L;
    }

    public void verifyAccess(Long requiredId) {
        if (!isPersonalProfile(requiredId))
            throw new DeniedAccessException("You have no access to this resource");
    }

    public boolean isPersonalProfile(Long requiredId){
        return Objects.equals(getIdFromSubject(), requiredId);
    }
}
