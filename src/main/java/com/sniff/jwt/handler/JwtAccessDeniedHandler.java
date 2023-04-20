package com.sniff.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sniff.utils.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static com.sniff.jwt.JwtConstants.ACCESS_DENIED_MESSAGE;

@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        String responseBody = objectMapper.writeValueAsString(new HttpResponse(ACCESS_DENIED_MESSAGE));
        response.getWriter().write(responseBody);
    }
}