package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaiKhoanTruyCapDTO {
    private String userId;
    private String username;
    private String email;
    private LocalDateTime lastActivityTime;  // Cho online user
    private LocalDateTime logoutTime;        // Cho offline user
    private Long offlineMinutes;             // Số phút đã offline
    private boolean isOnline;
}
