package com.example.sever.service;

import com.example.sever.dto.request.AuthenticationRequest;
import com.example.sever.dto.request.ForgotPasswordRequest;
import com.example.sever.dto.request.ResetPasswordRequest;
import com.example.sever.dto.response.LoginResponse;
import com.example.sever.dto.response.RefreshTokenResponse;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.repository.TaiKhoanRepository;
import com.example.sever.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImplementation implements AuthenticationService {

    TaiKhoanRepository taiKhoanRepository;
    TokenProvider tokenProvider;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    MailService mailService;
    PasswordEncoder passwordEncoder;

    private static final String KEY_COOKIE = "ARTICLE_SERVICE";

    @Override
    public LoginResponse login(AuthenticationRequest request, HttpServletResponse response) {
        log.debug("Login attempt with identifier: {}", request.getTenDangNhap());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getTenDangNhap(), request.getMatKhau()));
        } catch (BadCredentialsException e) {
            log.error("Invalid password for identifier: {}", request.getTenDangNhap());
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        } catch (InternalAuthenticationServiceException e) {
            if (e.getCause() instanceof AppException appEx && appEx.getErrorCode() == ErrorCode.USER_NOT_EXIST) {
                log.error("User not found for identifier: {}", request.getTenDangNhap());
                throw appEx;
            }
            log.error("Authentication service error for identifier: {}. Error: {}", request.getTenDangNhap(), e.getMessage());
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error during authentication for identifier: {}. Error: {}", request.getTenDangNhap(), e.getMessage());
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        TaiKhoan user = taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap())
                .or(() -> taiKhoanRepository.findByEmail(request.getTenDangNhap()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if (!user.isEnabled()) {
            log.error("Account is disabled for user: {}", user.getTenDangNhap());
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        taiKhoanRepository.save(user);
        log.info("User {} logged in successfully", user.getTenDangNhap());

        Cookie cookie = setCookie(accessToken, refreshToken);
        response.addCookie(cookie);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .accessTokenTTL(jwtUtil.getValidDuration())
                .refreshToken(refreshToken)
                .refreshTokenTTL(jwtUtil.getRefreshableDuration())
                .userId(user.getId().toString())
                .build();
    }

    @Override
    public LoginResponse loginWithGoogle(String email, HttpServletResponse response) {
        log.debug("Google login attempt with email: {}", email);
        TaiKhoan user = taiKhoanRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("No user found with email: {}. Should have been created.", email);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        if (!user.isEnabled()) {
            log.error("Account is disabled for user: {}", user.getTenDangNhap());
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        taiKhoanRepository.save(user);
        log.info("Google user {} logged in successfully", user.getTenDangNhap());

        Cookie cookie = setCookie(accessToken, refreshToken);
        response.addCookie(cookie);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .accessTokenTTL(jwtUtil.getValidDuration())
                .refreshToken(refreshToken)
                .refreshTokenTTL(jwtUtil.getRefreshableDuration())
                .userId(user.getId().toString())
                .build();
    }

    @Override
    public void logout(String accessToken, HttpServletResponse response) throws ParseException {
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            log.error("Invalid or missing token for logout");
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        accessToken = accessToken.replace("Bearer ", "");
        String username = tokenProvider.verifyAndExtractUsername(accessToken);
        TaiKhoan user = taiKhoanRepository.findByTenDangNhap(username)
                .or(() -> taiKhoanRepository.findByEmail(username))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        long accessTokenExpired = tokenProvider.verifyAndExtractTokenExpired(accessToken);
        long currentTime = System.currentTimeMillis();

        if (currentTime < accessTokenExpired) {
            user.setRefreshToken(null);
            taiKhoanRepository.save(user);
            deleteCookie(response);
            SecurityContextHolder.clearContext();
            log.info("User {} logged out successfully", username);
        } else {
            log.error("Token expired for user: {}", username);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken, HttpServletResponse response) throws ParseException {
        if (refreshToken == null || refreshToken.isBlank()) {
            log.error("Invalid or missing refresh token");
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String username = tokenProvider.verifyAndExtractUsername(refreshToken);
        TaiKhoan user = taiKhoanRepository.findByTenDangNhap(username)
                .or(() -> taiKhoanRepository.findByEmail(username))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!Objects.equals(user.getRefreshToken(), refreshToken) || user.getRefreshToken() == null) {
            log.error("Refresh token mismatch for user: {}", username);
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        var signJWT = tokenProvider.verifyToken(refreshToken);
        if (signJWT == null) {
            log.error("Invalid refresh token for user: {}", username);
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);

        Cookie cookie = setCookie(accessToken, newRefreshToken);
        response.addCookie(cookie);

        user.setRefreshToken(newRefreshToken);
        taiKhoanRepository.save(user);
        log.info("Refresh token generated for user: {}", username);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        TaiKhoan user = taiKhoanRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String resetCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        Date expiry = new Date(System.currentTimeMillis() + 15 * 60 * 1000);

        user.setResetToken(resetCode);
        user.setResetTokenExpiry(expiry);
        taiKhoanRepository.save(user);
        log.info("Reset token generated for user: {}", user.getTenDangNhap());

        String emailContent = "Mã xác thực đặt lại mật khẩu của bạn là: " + resetCode +
                "\n\nLưu ý: Mã sẽ hết hạn sau 15 phút.";
        mailService.sendResetPasswordEmail(user.getEmail(), emailContent);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        TaiKhoan user = taiKhoanRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getResetToken() == null || !user.getResetToken().equals(request.getResetToken())) {
            log.error("Invalid reset token for user: {}", user.getTenDangNhap());
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().before(new Date())) {
            log.error("Reset token expired for user: {}", user.getTenDangNhap());
            throw new AppException(ErrorCode.RESET_TOKEN_EXPIRED);
        }

        user.setMatKhau(passwordEncoder.encode(request.getNewMatKhau()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        taiKhoanRepository.save(user);
        log.info("Password reset successfully for user: {}", user.getTenDangNhap());
    }

    private Cookie setCookie(String accessToken, String refreshToken) {
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("accessToken", accessToken);
        tokenData.put("refreshToken", refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(tokenData);
        } catch (JsonProcessingException e) {
            log.error("Failed to process JSON for cookie: {}", e.getMessage());
            throw new AppException(ErrorCode.JSON_PROCESSING_ERROR);
        }

        String formattedJsonData = jsonData.replace("\"", "%22").replace(",", "%2C");

        Cookie cookie = new Cookie(KEY_COOKIE, formattedJsonData);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(jwtUtil.getRefreshableDuration().intValue() / 1000);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain("localhost");
        log.debug("Cookie set: {}", formattedJsonData);
        return cookie;
    }

    private void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(KEY_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain("localhost");
        response.addCookie(cookie);
        log.debug("Cookie deleted");
    }
}