package com.mekongocop.apigateway.util;

import io.jsonwebtoken.Claims;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomReactiveJwtDecoder implements ReactiveJwtDecoder {

    private final JwtTokenProvider jwtTokenProvider;

    public CustomReactiveJwtDecoder(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Jwt> decode(String token) throws JwtException {
        return Mono.fromCallable(() -> {
            Jwt jwt = jwtTokenProvider.validateAndParseToken(token);
            if (jwt != null) {
                return jwt;
            } else {
                throw new JwtException("Invalid token");
            }
        });
    }
}