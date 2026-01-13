package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.LaptopAddRequestDTO;
import com.example.sever.dto.request.LaptopUpdateRequestDTO;
import com.example.sever.dto.response.LapTopDisplayReponse;
import com.example.sever.dto.response.LaptopResponseDTO;
import com.example.sever.service.LaptopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/laptop")
@RequiredArgsConstructor
public class LaptopController {

    private final LaptopService laptopService;

    /**
     * 📄 Lấy danh sách Laptop (Base) có phân trang
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ResponseEntity<ApiResponse<Page<LapTopDisplayReponse>>> getAllLaptopForDisplay(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        int currentPage = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(currentPage, size);

        Page<LapTopDisplayReponse> laptopPage = laptopService.getAllLapTopForDisplay(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<LapTopDisplayReponse>>builder()
                        .message("Lấy danh sách Laptop thành công")
                        .data(laptopPage)
                        .build()
        );
    }

    /**
     * ➕ Thêm mới Laptop (Base)
     */
    @PostMapping("/them-laptop")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LaptopResponseDTO>> addLaptop(@RequestBody LaptopAddRequestDTO dto) {
        LaptopResponseDTO response = laptopService.addLaptop(dto);
        return ResponseEntity.ok(
                ApiResponse.<LaptopResponseDTO>builder()
                        .message("Thêm Laptop thành công")
                        .data(response)
                        .build()
        );
    }

    /**
     * ✏️ Cập nhật Laptop (Base)
     */
    @PostMapping("/sua-laptop/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LaptopResponseDTO>> updateLaptop(
            @PathVariable("id") UUID id,
            @RequestBody LaptopUpdateRequestDTO dto
    ) {
        LaptopResponseDTO response = laptopService.updateLaptop(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<LaptopResponseDTO>builder()
                        .message("Cập nhật Laptop thành công")
                        .data(response)
                        .build()
        );
    }
    /**
     * 🔍 Xem chi tiết Laptop + danh sách LaptopChiTiet (bước 2)
     */
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ApiResponse<LapTopDisplayReponse> getLaTopById(@PathVariable("id") UUID id) {
        LapTopDisplayReponse lt = laptopService.getDetailedLapTop(id);
        return ApiResponse.<LapTopDisplayReponse>builder()
                .message("Lấy chi tiết LT thành công")
                .data(lt)
                .build();
    }

//    @PostMapping("/sua-he-dieu-hanh")
//    public ApiResponse<LaptopResponseDTO> updateLaptop(@RequestBody LaptopAddRequestDTO dto) {
//        LaptopResponseDTO  updated = laptopService.updateLaptop(dto);
//        return ApiResponse.<LaptopResponseDTO>builder()
//                .message("cap nhap thanh cong")
//                .data(updated)
//                .build();
//    }
}
