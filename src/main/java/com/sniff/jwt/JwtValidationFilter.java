package com.sniff.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sniff.utils.HttpResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.sniff.jwt.JwtConstants.*;

@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());

            if(!jwtService.isTokenValid(token)) {
                returnResponse(response, HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
                return;
            }

            if (jwtService.isTokenExpired(token)) {
                returnResponse(response, HttpStatus.UNAUTHORIZED, EXPIRED_TOKEN_MESSAGE);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void returnResponse(HttpServletResponse response,
                                HttpStatus status,
                                String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String responseBody = objectMapper.writeValueAsString(new HttpResponse(message));
        response.getWriter().write(responseBody);
    }
}
