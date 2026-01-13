package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.SeriAddRequestDTO;
import com.example.sever.dto.request.SeriUpdateRequestDTO;
import com.example.sever.dto.response.SeriDisplayReponse;

import com.example.sever.repository.SeriRepository;
import com.example.sever.service.SeriService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/seri")
@AllArgsConstructor
public class Sericontroller {

    private final SeriService seriService;
    private final SeriRepository seriRepository;

    /**
     * 📌 Lấy danh sách Seri theo biến thể LaptopChiTiet
     */
    @GetMapping("/by-laptop-ct/{idLaptopCt}")
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
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
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ResponseEntity<ApiResponse<SeriDisplayReponse>> getDetail(@PathVariable UUID id) {
        SeriDisplayReponse data = seriService.getDetail(id);
        return ResponseEntity.ok(
                ApiResponse.<SeriDisplayReponse>builder()
                        .message("Lấy chi tiết seri thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/by-id-seri/{idSeri}")
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public ResponseEntity<ApiResponse<SeriDisplayReponse>> findByIdSeri(
            @PathVariable String idSeri) {

        SeriDisplayReponse data = seriService.findByIdSeri(idSeri);

        return ResponseEntity.ok(
                ApiResponse.<SeriDisplayReponse>builder()
                        .message("Tìm seri thành công")
                        .data(data)
                        .build()
        );
    }
    //    @PostMapping(value = "/import-excel/{idLaptopCt}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public SeriImportResultDTO importExcel(
//            @PathVariable UUID idLaptopCt,
//            @RequestPart("file") MultipartFile file
//    ) {
//        return seriService.importExcel(idLaptopCt, file);
//    }
    @GetMapping("/exists")
    @PreAuthorize("hasAnyRole('ADMIN','NHAN_VIEN')")
    public boolean exists(@RequestParam("idSeri") String idSeri) {
        String norm = (idSeri == null) ? "" : idSeri.trim().toUpperCase();
        if (norm.isEmpty()) return false;
        return seriRepository.existsByIdSeri(norm);
    }
}