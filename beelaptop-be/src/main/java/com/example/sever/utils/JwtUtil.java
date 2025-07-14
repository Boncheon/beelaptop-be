package com.example.sever.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtUtil {
    @Value("${jwt.secret}")
    String secretKey;

    @Value("${jwt.expiration}")
    Long validDuration;

    @Value("${jwt.expiration.refresh}")
    Long refreshableDuration;
}