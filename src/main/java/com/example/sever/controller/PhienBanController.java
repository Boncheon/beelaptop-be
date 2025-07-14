package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.PhienBanAddRequestDTO;
import com.example.sever.dto.request.PhienBanBatchCreateDTO;
import com.example.sever.dto.request.PhienBanUpdateRequestDTO;
import com.example.sever.dto.response.PhienBanDisplayReponse;
import com.example.sever.entity.LaptopChiTiet;
import com.example.sever.repository.LaptopChiTietRepository;
import com.example.sever.service.LapTopCTService;
import com.example.sever.service.PhienBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phien-ban")
@RequiredArgsConstructor
public class PhienBanController {

    private final PhienBanService phienBanService;
    private final LapTopCTService lapTopCTService;
    @PostMapping("/them-phienban")
    public ApiResponse<?> addOrBatchPhienBan(@RequestBody PhienBanBatchCreateDTO dto) {
        if (dto.getCombinations() == null || dto.getCombinations().isEmpty()) {
            return ApiResponse.builder()
                    .message("Danh sách combinations không được rỗng")
                    .build();
        }

        // Gọi service để xác minh LaptopChiTiet tồn tại
        lapTopCTService.getById(dto.getIdLaptopChiTiet());

        // Gọi xử lý thêm phiên bản (dùng chung)
        List<PhienBanDisplayReponse> resultList = phienBanService.generatePhienBanBienThe(dto);

        if (resultList.size() == 1) {
            return ApiResponse.<PhienBanDisplayReponse>builder()
                    .message("Thêm phiên bản thành công")
                    .data(resultList.get(0))
                    .build();
        }

        return ApiResponse.<List<PhienBanDisplayReponse>>builder()
                .message("Thêm nhiều phiên bản thành công")
                .data(resultList)
                .build();
    }


    @PutMapping("/sua-phienban")
    public ApiResponse<PhienBanDisplayReponse> updatePhienBan(@RequestBody PhienBanUpdateRequestDTO requestDTO) {
        PhienBanDisplayReponse updateResponse = phienBanService.updatePhienBan(requestDTO);
        return ApiResponse.<PhienBanDisplayReponse>builder()
                .message("Cập nhật phiên bản thành công")
                .data(updateResponse)
                .build();
    }

    @GetMapping
    public ResponseEntity<Page<PhienBanDisplayReponse>> getAllPhienBanforDisplay(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Đảm bảo page không âm
        int currentPage = Math.max(page, 0);

        Pageable pageable = PageRequest.of(currentPage, size);

        Page<PhienBanDisplayReponse> resultPage = phienBanService.getAllPhienBanforDisplay(pageable);

        return ResponseEntity.ok(resultPage);
    }

}
