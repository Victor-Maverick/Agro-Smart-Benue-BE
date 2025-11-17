package dev.gagnon.bfpcapi.security.utils;

import java.util.List;

public class SecurityUtils {

    private SecurityUtils() {}

    public static final String JWT_PREFIX = "Bearer ";

    public static final List<String>
            PUBLIC_ENDPOINTS = List.of(
                "/api/auth/login",
                "/api/auth/logout",
                "/api/auth/**",
                "/api/users/**",
                "/api/crops/**",
                "/api/events/**",
                "/api/market-prices/**",
                "/api/markets/**",
                "/api/locations/**",
                "/api/products/**",
                "/api/crop-tips/**",
                "/api/reviews/**"
    );

}
