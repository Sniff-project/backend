package com.sniff.jwt;

public class JwtConstants {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME = 60; // 60 minutes
    public static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String FORBIDDEN_MESSAGE = "You need to sign in to access this page";
    public static final String TOKEN_ISSUER = "Sniff";
    public static final String ROLE_CLAIM = "role";
    public static final String NAME_CLAIM = "name";
}
