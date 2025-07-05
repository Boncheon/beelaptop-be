package com.example.sever.controller;

import com.example.sever.common.Meta;
import com.example.sever.dto.*;

import com.example.sever.dto.request.AuthenticationRequest;
import com.example.sever.dto.request.ForgotPasswordRequest;
import com.example.sever.dto.request.ResetPasswordRequest;
import com.example.sever.dto.request.UserCreationRequest;
import com.example.sever.dto.response.LoginResponse;
import com.example.sever.dto.response.RefreshTokenResponse;
import com.example.sever.dto.response.UserDetailResponse;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.mapper.UserMapper;
import com.example.sever.service.AuthenticationService;
import com.example.sever.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthenticationService authenticationService;
    UserMapper userMapper;
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDetailResponse>> signUp(@RequestBody @Valid UserCreationRequest request) {
        log.info("Signup attempt for tenDangNhap: {}, email: {}", request.getTenDangNhap(), request.getEmail());
        UserDetailResponse user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.<UserDetailResponse>builder()
                .code(201)
                .message("Đăng ký thành công! Bạn có thể đăng nhập bằng tên đăng nhập hoặc email.")
                .data(user)
                .build());
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<UserDetailResponse>> signIn(@RequestBody @Valid AuthenticationRequest request, HttpServletResponse response) {
        log.info("Signin attempt with identifier: {}", request.getTenDangNhap());
        LoginResponse loginResponse = authenticationService.login(request, response);
        TaiKhoan user = userService.findUserById(loginResponse.getUserId());
        UserDetailResponse userResponse = userMapper.toUserDetailResponse(user);
        loginResponse.setUserId(null);

        Meta<LoginResponse> meta = Meta.<LoginResponse>builder().tokenInfo(loginResponse).build();

        return ResponseEntity.ok(ApiResponse.<UserDetailResponse>builder()
                .message("Đăng nhập thành công!")
                .meta(meta)
                .data(userResponse)
                .build());
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<Void>> signOut(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, HttpServletResponse response)
            throws ParseException {
        log.info("Signout attempt with token: {}", token);
        authenticationService.logout(token, response);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Đăng xuất thành công!")
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@CookieValue(name = "ARTICLE_SERVICE") String cookieValue, HttpServletResponse response)
            throws ParseException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> tokenData = objectMapper.readValue(cookieValue.replace("%22", "\"").replace("%2C", ","), Map.class);
        String refreshToken = tokenData.get("refreshToken");

        log.info("Refresh token attempt with cookie: {}", cookieValue);
        RefreshTokenResponse data = authenticationService.refreshToken(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.<RefreshTokenResponse>builder()
                .message("Refresh token thành công!")
                .data(data)
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Mã xác thực đã được gửi về email của bạn!")
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("Reset password request for email: {}", request.getEmail());
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Đổi mật khẩu thành công!")
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getMyInfo() {
        log.info("Retrieving current user info");
        UserDetailResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.<UserDetailResponse>builder()
                .message("Tài khoản được tìm thấy!")
                .data(user)
                .build());
    }

    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponse<LoginResponse>> googleCallback(
            Authentication authentication,
            HttpServletResponse response) {
        if (!(authentication instanceof OAuth2AuthenticationToken token)) {
            log.error("Invalid authentication type for Google callback");
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }

        String email = token.getPrincipal().getAttribute("email");
        if (email == null) {
            log.error("Google OAuth2 response missing email");
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }

        log.info("Google callback for email: {}", email);
        LoginResponse loginResponse = authenticationService.loginWithGoogle(email, response);
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .message("Đăng nhập bằng Google thành công!")
                .data(loginResponse)
                .build());
    }
    @PostMapping("/test-google-login")
    public ResponseEntity<ApiResponse<LoginResponse>> testGoogleLogin(
            @RequestBody Map<String, String> request,
            HttpServletResponse response) {
        String email = request.get("email");
        if (email == null) {
            log.error("Test Google login missing email");
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }
        log.info("Test Google login for email: {}", email);
        LoginResponse loginResponse = authenticationService.loginWithGoogle(email, response);
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .code(200)
                .message("Đăng nhập bằng Google thành công!")
                .data(loginResponse)
                .build());
    }
}