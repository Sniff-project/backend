package com.sniff.jwt;

import com.sniff.auth.role.Role;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

import static com.sniff.jwt.JwtConstants.*;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String generateToken(Long id, Role role) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(TOKEN_ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(EXPIRATION_TIME, MINUTES))
                .subject(String.valueOf(id))
                .claim(ROLE_CLAIM, role.getAuthority())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean isTokenExpired(String token) {
        return Instant.now()
                .isAfter(Objects.requireNonNull(jwtDecoder.decode(token).getExpiresAt()));
    }

    public boolean isTokenValid(String token) {
        try {
            return StringUtils.isNotEmpty(jwtDecoder.decode(token).getSubject());
        } catch (BadJwtException ex) {
            return false;
        }
    }
}
