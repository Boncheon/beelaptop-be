package com.example.sever.controler;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.PinAddRequestDTO;
import com.example.sever.dto.request.PinUpdateRequestDTO;
import com.example.sever.dto.response.PinDisplayReponse;
import com.example.sever.enity.Pin;
import com.example.sever.service.PinService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pin")
@AllArgsConstructor
public class Pincontroller {

    PinService pinService;

    @GetMapping()
    public ResponseEntity<Page<PinDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<PinDisplayReponse> pinPage = pinService.getAllPinforDisplay(pageable);
        return ResponseEntity.ok(pinPage);
    }

    @PostMapping("/them-pin")
    public ApiResponse<Pin> addPin(@RequestBody PinAddRequestDTO pinAddRequestDTO) {
        Pin add = pinService.addPin(pinAddRequestDTO);
        return ApiResponse.<Pin>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-pin")
    public ApiResponse<Pin> updatePin(@RequestBody PinUpdateRequestDTO pinUpdateRequestDTO) {
        Pin  updated = pinService.updatePin(pinUpdateRequestDTO);
        return ApiResponse.<Pin>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
}
