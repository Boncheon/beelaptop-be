package com.example.sever.service;

import com.example.sever.dto.LogoutInfo;
import com.example.sever.dto.UserSessionInfo;
import com.example.sever.dto.response.TaiKhoanTruyCapDTO;
import com.example.sever.dto.response.ThongKeTruyCapResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserSessionTracker {

    // Map lưu thông tin user đang online: userId -> SessionInfo
    private final Map<String, UserSessionInfo> activeSessions = new ConcurrentHashMap<>();

    // Map lưu thông tin user đã logout: userId -> LogoutInfo
    private final Map<String, LogoutInfo> offlineSessions = new ConcurrentHashMap<>();

    /**
     * Track user activity khi có request với JWT hợp lệ
     * Gọi từ JwtTokenValidator
     */
    public void trackActivity(String userId, String username, String email) {
        LocalDateTime now = LocalDateTime.now();
        
        activeSessions.compute(userId, (key, existing) -> {
            if (existing == null) {
                // User mới login
                log.info("User {} ({}) vừa truy cập server", username, userId);
                return new UserSessionInfo(userId, username, email, now, now);
            } else {
                // Update last activity time
                existing.setLastActivityTime(now);
                return existing;
            }
        });

        // Xóa khỏi offline nếu user đã login lại
        offlineSessions.remove(userId);
    }

    /**
     * Track user logout
     * Gọi từ AuthenticationService.logout()
     */
    public void trackLogout(String userId) {
        UserSessionInfo session = activeSessions.remove(userId);
        
        if (session != null) {
            LocalDateTime now = LocalDateTime.now();
            LogoutInfo logoutInfo = new LogoutInfo(
                session.getUserId(),
                session.getUsername(),
                session.getEmail(),
                now
            );
            offlineSessions.put(userId, logoutInfo);
            log.info("User {} ({}) đã logout khỏi server", session.getUsername(), userId);
        }
    }

    /**
     * Lấy thống kê truy cập
     */
    public ThongKeTruyCapResponseDTO getStatistics() {
        List<TaiKhoanTruyCapDTO> onlineAccounts = new ArrayList<>();
        List<TaiKhoanTruyCapDTO> offlineAccounts = new ArrayList<>();

        // Lấy danh sách online users
        for (UserSessionInfo session : activeSessions.values()) {
            TaiKhoanTruyCapDTO dto = TaiKhoanTruyCapDTO.builder()
                    .userId(session.getUserId())
                    .username(session.getUsername())
                    .email(session.getEmail())
                    .lastActivityTime(session.getLastActivityTime())
                    .isOnline(true)
                    .build();
            onlineAccounts.add(dto);
        }

        // Lấy danh sách offline users
        LocalDateTime now = LocalDateTime.now();
        for (LogoutInfo logoutInfo : offlineSessions.values()) {
            long offlineMinutes = Duration.between(logoutInfo.getLogoutTime(), now).toMinutes();
            
            TaiKhoanTruyCapDTO dto = TaiKhoanTruyCapDTO.builder()
                    .userId(logoutInfo.getUserId())
                    .username(logoutInfo.getUsername())
                    .email(logoutInfo.getEmail())
                    .logoutTime(logoutInfo.getLogoutTime())
                    .offlineMinutes(offlineMinutes)
                    .isOnline(false)
                    .build();
            offlineAccounts.add(dto);
        }

        return ThongKeTruyCapResponseDTO.builder()
                .onlineAccounts(onlineAccounts)
                .offlineAccounts(offlineAccounts)
                .totalOnline(onlineAccounts.size())
                .totalOffline(offlineAccounts.size())
                .build();
    }

    /**
     * Clear all sessions (dùng cho testing hoặc admin)
     */
    public void clearAllSessions() {
        activeSessions.clear();
        offlineSessions.clear();
        log.info("Đã xóa tất cả session tracking data");
    }

    /**
     * Get total active sessions count
     */
    public int getActiveSessionsCount() {
        return activeSessions.size();
    }

    /**
     * Get total offline sessions count
     */
    public int getOfflineSessionsCount() {
        return offlineSessions.size();
    }
}
