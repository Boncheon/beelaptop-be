package com.example.sever.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse implements Serializable {
    String accessToken;
    Long accessTokenTTL;
    String refreshToken;
    Long refreshTokenTTL;
    String userId;
}
