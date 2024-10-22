package com.mekongocop.mekongocopserver.config;

import com.mekongocop.mekongocopserver.util.JwtTokenFilter;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Cấu hình CORS ở đây
        http.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Sử dụng nguồn cấu hình CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v*/login").permitAll()
                        .requestMatchers("/api/v*/register").permitAll()
                        .requestMatchers("/api/v*/otp/**").permitAll()
                        .requestMatchers("/api/v*/user/**").permitAll()
                        .requestMatchers("/api/v*/order").permitAll()
                        .requestMatchers("/api/v*/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v*/seller/**").hasRole("SELLER")
                        .requestMatchers("/api/v*/common/**").hasAnyRole("ADMIN","BUYER", "SELLER")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:61611", "http://localhost:8081")); // Cho phép origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH")); // Cho phép các phương thức
        configuration.setAllowedHeaders(Arrays.asList("*")); // Cho phép tất cả các header
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Cấu hình cho tất cả đường dẫn
        return source;
    }
}
