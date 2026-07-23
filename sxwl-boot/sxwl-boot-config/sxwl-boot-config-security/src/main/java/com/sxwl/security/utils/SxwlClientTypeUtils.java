package com.sxwl.security.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

public final class SxwlClientTypeUtils {

    public static final String ADMIN = "admin";
    public static final String FRONT = "front";
    public static final String HEADER = "X-Client-Type";

    private static final Set<String> ALLOWED = Set.of(ADMIN, FRONT);

    private SxwlClientTypeUtils() {
        throw new UnsupportedOperationException("SxwlClientTypeUtils cannot be instantiated");
    }

    public static String resolve(HttpServletRequest request) {
        if (request == null) {
            return ADMIN;
        }
        return normalize(request.getHeader(HEADER));
    }

    public static String normalize(String clientType) {
        if (clientType == null || clientType.isBlank()) {
            return ADMIN;
        }
        String normalized = clientType.trim().toLowerCase();
        if (!ALLOWED.contains(normalized)) {
            return ADMIN;
        }
        return normalized;
    }
}
