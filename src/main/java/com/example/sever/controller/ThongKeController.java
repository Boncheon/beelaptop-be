package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.response.ThongKeTongQuanResponseDTO;
import com.example.sever.service.ThongKeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/thong-ke")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThongKeController {

    ThongKeService thongKeService;

    // So sánh 12 tháng của 2 năm
    @GetMapping("/nam")
    public ResponseEntity<?> thongKeTheoNam(
            @RequestParam int nam1,
            @RequestParam int nam2
    ) {
        return ResponseEntity.ok(thongKeService.thongKe12Thang(nam1, nam2));
    }

    // So sánh theo ngày của 2 tháng
    @GetMapping("/thang")
    public ResponseEntity<?> thongKeTheoNgay(
            @RequestParam int nam1,
            @RequestParam int thang1,
            @RequestParam int nam2,
            @RequestParam int thang2
    ) {
        return ResponseEntity.ok(
                thongKeService.thongKeTheoNgay(nam1, thang1, nam2, thang2)
        );
    }
    @GetMapping("/ngay")
    public ResponseEntity<?> soSanhHaiNgay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay2
    ) {
        return ResponseEntity.ok(
                thongKeService.soSanhHaiNgay(ngay1, ngay2)
        );
    }
    @GetMapping("/tong-quan")
    public ResponseEntity<ApiResponse<ThongKeTongQuanResponseDTO>> getThongKeQuan(){
        try {
            ThongKeTongQuanResponseDTO result = thongKeService.thongKeTongQuan();
            return ResponseEntity.ok(
                    ApiResponse.<ThongKeTongQuanResponseDTO>builder()
                            .code(HttpStatus.OK.value())
                            .message("Lấy thống kê tổng quan thành công")
                            .data(result)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ThongKeTongQuanResponseDTO>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi khi lấy thống kê tổng quan: " + e.getMessage())
                            .build());
        }
    }
}


