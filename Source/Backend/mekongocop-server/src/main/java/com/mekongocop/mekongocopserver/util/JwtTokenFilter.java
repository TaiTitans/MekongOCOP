package com.mekongocop.mekongocopserver.util;

import com.mekongocop.mekongocopserver.entity.Role;
import com.mekongocop.mekongocopserver.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/user/refresh-token",
            "/api/v1/login",
            "/api/v1/register",
            "/api/v1/otp/**"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtTokenProvider.getTokenFromRequest(request);
            if (token != null) {
                if (jwtTokenProvider.isTokenExpired(token)) {
                    logger.warn("JWT token has expired, ignoring token for request");
                } else {
                    logger.warn("Valid JWT token found, setting authentication context");
                    setAuthenticationContext(token, request, response);
                }
            } else {
                logger.warn("No valid JWT token found");
            }
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT token: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing JWT token", e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private void setAuthenticationContext(String token, HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = getUserDetails(token);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(String token) {
        Claims claims = jwtTokenProvider.parseClaims(token);
        String username = claims.getSubject();
        Set<String> roles = new HashSet<>(claims.get("roles", List.class));
        User user = new User();
        user.setUsername(username);
        roles.forEach(role -> user.setRoles(new Role(role)));
        return user;
    }
}
