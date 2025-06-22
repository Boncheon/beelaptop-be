package com.example.sever.cofig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Tắt CSRF cho API
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Cho phép TẤT CẢ request
                )
                .httpBasic().disable()
                .formLogin().disable(); // Không cần form login

        return http.build();
    }
}
