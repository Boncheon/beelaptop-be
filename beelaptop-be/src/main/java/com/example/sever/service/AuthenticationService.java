package com.example.sever.service;

import com.example.sever.dto.request.AuthenticationRequest;
import com.example.sever.dto.request.ForgotPasswordRequest;
import com.example.sever.dto.request.ResetPasswordRequest;
import com.example.sever.dto.response.LoginResponse;
import com.example.sever.dto.response.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.text.ParseException;

public interface AuthenticationService {
    LoginResponse login(AuthenticationRequest request, HttpServletResponse response);
    LoginResponse loginWithGoogle(String email, HttpServletResponse response);
    void logout(String accessToken, HttpServletResponse response) throws ParseException;
    RefreshTokenResponse refreshToken(String refreshToken, HttpServletResponse response) throws ParseException;
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}