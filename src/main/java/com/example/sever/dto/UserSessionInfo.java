package com.example.sever.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionInfo {
    private String userId;
    private String username;
    private String email;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivityTime;
}