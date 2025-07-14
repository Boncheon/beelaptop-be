package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.AnhAddRequestDTO;
import com.example.sever.dto.request.AnhUpdateRequestDTO;
import com.example.sever.dto.response.AnhDisplayReponse;
import com.example.sever.service.AnhService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.UUID;

@CrossOrigin("*")
@RequestMapping("/upload")
@RestController
@RequiredArgsConstructor
public class UploadFileController {
    private final AnhService anhService;

    @PostMapping()
    public ApiResponse<AnhDisplayReponse> uploadImage(@ModelAttribute AnhAddRequestDTO request) throws IOException {
        AnhDisplayReponse response = anhService.uploadImageAndSave(request);
        return ApiResponse.<AnhDisplayReponse>builder()
                .message("Tải ảnh thành công")
                .data(response)
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<AnhDisplayReponse> updateImage(
            @PathVariable UUID id,
            @ModelAttribute AnhUpdateRequestDTO request) throws IOException {

        AnhDisplayReponse updated = anhService.updateImage(id, request);
        return ApiResponse.<AnhDisplayReponse>builder()
                .message("Cập nhật ảnh thành công")
                .data(updated)
                .build();
    }


}
