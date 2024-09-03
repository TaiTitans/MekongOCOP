package com.mekongocop.mekongocopserver.util;

public class TokenExtractor {
    public static String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
