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
                "/api/users/resend-verification",
                "/api/users/register",
                "/api/auth/test-email",
                "/api/users/verify",
                "/api/users/all",
                "/api/auth/forgot-password",
                "/api/users/dashboard-redirect",
                "/api/auth/reset-password",
                "/api/crops/**",
                "/api/events/**",
                "/api/market-prices/**",
                "/api/markets/**",
                "/api/locations/**",
                "/api/products/**",
                "/api/product-demands/**"
    );

}
