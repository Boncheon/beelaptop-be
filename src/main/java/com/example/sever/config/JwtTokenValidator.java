package com.example.sever.config;

import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.service.CustomUserDetailService;
import com.example.sever.service.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenValidator extends OncePerRequestFilter {

    TokenProvider tokenProvider;
    CustomUserDetailService customUserDetailService;

    static final String COOKIE_NAME = "ARTICLE_SERVICE";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // ✅ Bỏ qua một số endpoint public / không cần auth
        if (uri.startsWith("/auth/signin")
                || uri.startsWith("/auth/signup")
                || uri.startsWith("/auth/forgot-password")
                || uri.startsWith("/auth/reset-password")
                || uri.startsWith("/auth/refresh")
                || uri.startsWith("/auth/google")
                || uri.startsWith("/oauth2")
                || uri.startsWith("/public")
                || uri.startsWith("/uploads")
                || uri.startsWith("/api/pos/payment/vnpay-return")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Nếu SecurityContext đã có auth rồi thì bỏ qua
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        // 1️⃣ Ưu tiên lấy từ Authorization header (Bearer ...)
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        // 2️⃣ Nếu không có header → thử lấy từ cookie ARTICLE_SERVICE
        if (token == null) {
            token = extractAccessTokenFromCookie(request).orElse(null);
        }

        // 3️⃣ Nếu vẫn không có token → cho qua (endpoint có thể là public)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = tokenProvider.verifyAndExtractUsername(token);
            if (username == null) {
                filterChain.doFilter(request, response);
                return;
            }

            TaiKhoan user = (TaiKhoan) customUserDetailService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (ParseException e) {
            log.error("Token parse error: {}", e.getMessage());
            // Không quẳng exception, cho qua để controller / exception handler xử lý tiếp
        } catch (AppException e) {
            log.error("Token invalid: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        try {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String raw = cookie.getValue();
                    if (raw == null || raw.isBlank()) continue;

                    // decode giống bên AuthController.refresh
                    String json = raw.replace("%22", "\"").replace("%2C", ",");

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = mapper.readValue(json, Map.class);
                    String accessToken = map.get("accessToken");
                    if (accessToken != null && !accessToken.isBlank()) {
                        return Optional.of(accessToken);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting accessToken from cookie: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
