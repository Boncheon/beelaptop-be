package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.SeriAddRequestDTO;
import com.example.sever.dto.request.SeriUpdateRequestDTO;
import com.example.sever.dto.response.SeriDisplayReponse;
import com.example.sever.service.SeriService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/seri")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class Sericontroller {

    private final SeriService seriService;

    /**
     * 📌 Lấy danh sách Seri theo biến thể LaptopChiTiet
     */
    @GetMapping("/by-laptop-ct/{idLaptopCt}")
    public ResponseEntity<ApiResponse<List<SeriDisplayReponse>>> getByLaptopCt(
            @PathVariable UUID idLaptopCt) {

        List<SeriDisplayReponse> data = seriService.getByLaptopCt(idLaptopCt);

        return ResponseEntity.ok(
                ApiResponse.<List<SeriDisplayReponse>>builder()
                        .message("Lấy danh sách seri thành công")
                        .data(data)
                        .build()
        );
    }

    /**
     * 📌 Thêm nhiều Seri cho một biến thể LaptopChiTiet
     * Body: SeriAddRequestDTO { idLaptopCt, list[ { idSeri, trangThai }, ... ] }
     */
    @PostMapping("/them-list")
    public ResponseEntity<ApiResponse<Void>> addListSeri(
            @RequestBody SeriAddRequestDTO dto) {

        seriService.addListSeri(dto);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Thêm seri thành công")
                        .build()
        );
    }

    /**
     * 📌 Cập nhật 1 Seri
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Void>> update(@RequestBody SeriUpdateRequestDTO dto) {
        seriService.updateSeri(dto);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Cập nhật seri thành công")
                        .build()
        );
    }

    /**
     * 📌 Lấy toàn bộ Seri trong hệ thống
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SeriDisplayReponse>>> getAll() {
        List<SeriDisplayReponse> data = seriService.getAll();
        return ResponseEntity.ok(
                ApiResponse.<List<SeriDisplayReponse>>builder()
                        .message("Lấy danh sách tất cả seri thành công")
                        .data(data)
                        .build()
        );
    }

    /**
     * 📌 Lấy chi tiết 1 Seri theo id
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<SeriDisplayReponse>> getDetail(@PathVariable UUID id) {
        SeriDisplayReponse data = seriService.getDetail(id);
        return ResponseEntity.ok(
                ApiResponse.<SeriDisplayReponse>builder()
                        .message("Lấy chi tiết seri thành công")
                        .data(data)
                        .build()
        );
    }
}
