package com.sniff.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sniff.jwt.JwtValidationFilter;
import com.sniff.jwt.JwtService;
import com.sniff.jwt.handler.JwtAccessDeniedHandler;
import com.sniff.jwt.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
           .httpBasic(withDefaults())
                .csrf().disable()
                .cors().configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
                    corsConfiguration.addAllowedMethod(PATCH);
                    corsConfiguration.addAllowedMethod(PUT);
                    corsConfiguration.addAllowedMethod(DELETE);
                    return corsConfiguration;
                })
                .and()
                .headers().frameOptions().disable()
                .and()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(POST, "/api/v1/auth/signup").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .exceptionHandling(c -> c
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(new JwtAccessDeniedHandler(objectMapper))
                )
                .addFilterBefore(new JwtValidationFilter(jwtService, objectMapper),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
