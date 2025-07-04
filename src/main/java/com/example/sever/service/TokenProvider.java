package com.example.sever.service;

import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.utils.JwtUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenProvider {

    static final String USERNAME_CLAIM = "tenDangNhap";
    static final String ISSUER = "sever";

    JwtUtil jwtUtil;

    public String generateAccessToken(TaiKhoan user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer(ISSUER)
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(jwtUtil.getValidDuration(), java.time.temporal.ChronoUnit.SECONDS)))
                .claim(USERNAME_CLAIM, user.getTenDangNhap())
                .claim("tenChucVu", user.getIdRole().getTenChucVu())
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwtUtil.getSecretKey()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String generateRefreshToken(TaiKhoan user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer(ISSUER)
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(jwtUtil.getRefreshableDuration(), java.time.temporal.ChronoUnit.SECONDS)))
                .claim(USERNAME_CLAIM, user.getTenDangNhap())
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwtUtil.getSecretKey()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public SignedJWT verifyToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.replace("Bearer ", "");
            }
            JWSVerifier verifier = new MACVerifier(jwtUtil.getSecretKey());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean verified = signedJWT.verify(verifier);

            if (!(verified && expiration.after(Date.from(Instant.now())))) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            return signedJWT;
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    public String verifyAndExtractUsername(String token) throws ParseException {
        Object usernameClaim = this.verifyToken(token).getJWTClaimsSet().getClaim(USERNAME_CLAIM);
        if (usernameClaim == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return usernameClaim.toString();
    }

    public long verifyAndExtractTokenExpired(String token) throws ParseException {
        Date expiredClaim = this.verifyToken(token).getJWTClaimsSet().getExpirationTime();
        if (expiredClaim == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return expiredClaim.getTime();
    }
}