package com.example.sever.service;

import com.example.sever.dto.*;
import com.example.sever.dto.req.AuthenticationRequest;
import com.example.sever.dto.req.ForgotPasswordRequest;
import com.example.sever.dto.req.ResetPasswordRequest;
import com.example.sever.dto.res.LoginResponse;
import com.example.sever.dto.res.RefreshTokenResponse;
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