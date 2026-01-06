package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.response.ThongKeTongQuanResponseDTO;
import com.example.sever.dto.response.ThongKeTrangThaiResponseDTO;
import com.example.sever.dto.response.ThongKeTruyCapResponseDTO;
import com.example.sever.repository.OrderRepository;
import com.example.sever.service.ThongKeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController

@RequestMapping("/api/thong-ke")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThongKeController {

    ThongKeService thongKeService;
    OrderRepository orderRepository;

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

    @GetMapping("/trang-thai")
    public ResponseEntity<ApiResponse<ThongKeTrangThaiResponseDTO>> getThongKeTrangThai(){
        try {
            ThongKeTrangThaiResponseDTO result = thongKeService.thongKeTheoTrangThai();
            return ResponseEntity.ok(
                    ApiResponse.<ThongKeTrangThaiResponseDTO>builder()
                            .code(HttpStatus.OK.value())
                            .message("Lấy thống kê theo trạng thái thành công")
                            .data(result)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ThongKeTrangThaiResponseDTO>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi khi lấy thống kê theo trạng thái: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/truy-cap")
    public ResponseEntity<ApiResponse<ThongKeTruyCapResponseDTO>> getThongKeTruyCap() {
        try {
            ThongKeTruyCapResponseDTO result = thongKeService.thongKeTruyCap();
            return ResponseEntity.ok(
                    ApiResponse.<ThongKeTruyCapResponseDTO>builder()
                            .code(HttpStatus.OK.value())
                            .message("Lấy thống kê truy cập thành công")
                            .data(result)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<ThongKeTruyCapResponseDTO>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi khi lấy thống kê truy cập: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Lấy top 10 laptop bán chạy nhất theo năm
     * @param year Năm cần thống kê
     * @return Danh sách top 10 laptop
     */
    @GetMapping("/top-laptop/nam")
    public ResponseEntity<ApiResponse<List<TopLaptopDTO>>> getTopLaptopTheoNam(
            @RequestParam(required = false) Integer year
    ) {
        try {
            int targetYear = (year != null) ? year : LocalDate.now().getYear();
            
            List<Object[]> results = orderRepository.topLaptopBanChayNhat(targetYear);
            
            List<TopLaptopDTO> topLaptops = results.stream()
                    .map(row -> TopLaptopDTO.builder()
                            .idLaptop(row[0] != null ? UUID.fromString(row[0].toString()) : null)
                            .tenSanPham(row[1] != null ? row[1].toString() : "")
                            .soLuongBan(row[2] != null ? ((Number) row[2]).longValue() : 0L)
                            .build())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.<List<TopLaptopDTO>>builder()
                            .code(HttpStatus.OK.value())
                            .message("Lấy top 10 laptop bán chạy nhất theo năm " + targetYear + " thành công")
                            .data(topLaptops)
                            .build()
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy top laptop theo năm: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<TopLaptopDTO>>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi khi lấy top laptop theo năm: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Lấy top 10 laptop bán chạy nhất theo tháng
     * @param year Năm cần thống kê
     * @param month Tháng cần thống kê
     * @return Danh sách top 10 laptop
     */
    @GetMapping("/top-laptop/thang")
    public ResponseEntity<ApiResponse<List<TopLaptopDTO>>> getTopLaptopTheoThang(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        try {
            LocalDate now = LocalDate.now();
            int targetYear = (year != null) ? year : now.getYear();
            int targetMonth = (month != null) ? month : now.getMonthValue();
            
            if (targetMonth < 1 || targetMonth > 12) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<List<TopLaptopDTO>>builder()
                                .code(HttpStatus.BAD_REQUEST.value())
                                .message("Tháng không hợp lệ. Vui lòng nhập từ 1-12")
                                .build());
            }

            List<Object[]> results = orderRepository.topLaptopBanChayNhatTheoThang(targetYear, targetMonth);
            
            List<TopLaptopDTO> topLaptops = results.stream()
                    .map(row -> TopLaptopDTO.builder()
                            .idLaptop(row[0] != null ? UUID.fromString(row[0].toString()) : null)
                            .tenSanPham(row[1] != null ? row[1].toString() : "")
                            .hinhAnh(row[2] != null ? row[2].toString() : null)
                            .soLuongBan(row[3] != null ? ((Number) row[3]).longValue() : 0L)
                            .tongTienThuHo(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO)
                            .build())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.<List<TopLaptopDTO>>builder()
                            .code(HttpStatus.OK.value())
                            .message("Lấy top 10 laptop bán chạy nhất tháng " + targetMonth + "/" + targetYear + " thành công")
                            .data(topLaptops)
                            .build()
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy top laptop theo tháng: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<TopLaptopDTO>>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi khi lấy top laptop theo tháng: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Lấy top 10 laptop bán chạy nhất theo ngày
     * @param ngay Ngày cần thống kê (format: yyyy-MM-dd)
     * @return Danh sách top 10 laptop
     */
    @GetMapping("/top-laptop/ngay")
    public ResponseEntity<ApiResponse<List<TopLaptopDTO>>> getTopLaptopTheoNgay(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay
    ) {
        try {
            LocalDate targetDate = (ngay != null) ? ngay : LocalDate.now();
            
            List<Object[]> results = orderRepository.topLaptopBanChayNhatTheoNgay(targetDate);
            
            List<TopLaptopDTO> topLaptops = results.stream()
                    .map(row -> TopLaptopDTO.builder()
                            .idLaptop(row[0] != null ? UUID.fromString(row[0].toString()) : null)
                            .tenSanPham(row[1] != null ? row[1].toString() : "")
                            .tongTienThuHo(row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO)
                            .soLuongBan(row[3] != null ? ((Number) row[3]).longValue() : 0L)
                            .build())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.<List<TopLaptopDTO>>builder()
                            .code(HttpStatus.OK.value())
                            .message("Lấy top 10 laptop bán chạy nhất ngày " + targetDate + " thành công")
                            .data(topLaptops)
                            .build()
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy top laptop theo ngày: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<TopLaptopDTO>>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lỗi khi lấy top laptop theo ngày: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Lấy tất cả top laptop (không filter theo thời gian)
     * @return Danh sách top 10 laptop
     */

    /**
     * DTO cho Top Laptop
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TopLaptopDTO {
        private UUID idLaptop;
        private String tenSanPham;
        private String hinhAnh;
        private Long soLuongBan;
        private BigDecimal tongTienThuHo;
    }
}

