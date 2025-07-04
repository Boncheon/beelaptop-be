package com.example.sever.service;

import com.example.sever.entity.Role;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.repository.RoleRepository;
import com.example.sever.repository.TaiKhoanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    TaiKhoanRepository taiKhoanRepository;
    RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null) {
            log.error("Google OAuth2 response missing email: {}", attributes);
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }

        log.debug("Processing Google OAuth2 user: email={}", email);

        TaiKhoan user = taiKhoanRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Registering new TaiKhoan for Google user: {}", email);
                    Role role = roleRepository.findByTenChucVu("KHACH_HANG")
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

                    TaiKhoan newUser = TaiKhoan.builder()
                            .id(UUID.randomUUID())
                            .idTaiKhoan("TK" + UUID.randomUUID().toString().substring(0, 18))
                            .email(email)
                            .ten(name != null ? name : "Google User")
                            .tenDangNhap("google_" + UUID.randomUUID().toString().substring(0, 18))
                            .matKhau("")
                            .idRole(role)
                            .trangThai(1)
                            .build();
                    return taiKhoanRepository.save(newUser);
                });

        log.info("Google user {} processed successfully", user.getTenDangNhap());
        return (OAuth2User) new org.springframework.security.core.userdetails.User(
                user.getTenDangNhap(),
                "",
                user.getAuthorities()
        );
    }
}