package com.sniff.jwt;

import com.sniff.auth.role.Role;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

    public boolean isTokenValid(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return StringUtils.isNotEmpty(jwt.getSubject()) && !isTokenExpired(jwt);
        } catch (BadJwtException ex) {
            return false;
        }
    }

    private boolean isTokenExpired(Jwt jwt){
        Instant jwtExpiresAt = jwt.getExpiresAt();
        return jwtExpiresAt == null || Instant.now().isAfter(jwtExpiresAt);
    }
}
