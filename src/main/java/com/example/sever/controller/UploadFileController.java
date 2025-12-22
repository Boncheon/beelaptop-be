package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.AnhAddRequestDTO;
import com.example.sever.dto.request.AnhUpdateRequestDTO;
import com.example.sever.dto.response.AnhDisplayReponse;
import com.example.sever.service.AnhService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/anh")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UploadFileController {

    private final AnhService anhService;

    // Lấy list ảnh của 1 SPCT (LaptopChiTiet)
    @GetMapping("/by-laptop-ct/{idLaptopCt}")
    public ResponseEntity<List<AnhDisplayReponse>> getByLaptopCt(
            @PathVariable UUID idLaptopCt
    ) {
        return ResponseEntity.ok(anhService.getByLaptopChiTiet(idLaptopCt));
    }

    // Upload ảnh mới cho 1 SPCT
    @PostMapping("/upload")
    public ResponseEntity<AnhDisplayReponse> upload(
            @RequestParam("idLaptopChiTiet") UUID idLaptopChiTiet,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        AnhAddRequestDTO dto = new AnhAddRequestDTO();
        dto.setIdLaptopChiTiet(idLaptopChiTiet);
        dto.setFile(file);

        AnhDisplayReponse res = anhService.uploadImageAndSave(dto);
        return ResponseEntity.ok(res);
    }

    // Cập nhật lại file ảnh
    @PutMapping("/{id}")
    public ResponseEntity<AnhDisplayReponse> update(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "idAnh", required = false) String idAnh
    ) throws IOException {

        AnhUpdateRequestDTO dto = new AnhUpdateRequestDTO();
        dto.setFile(file);
        dto.setIdAnh(idAnh);

        AnhDisplayReponse res = anhService.updateImage(id, dto);
        return ResponseEntity.ok(res);
    }

    // Xoá 1 ảnh
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable UUID id) {
//        anhService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}
