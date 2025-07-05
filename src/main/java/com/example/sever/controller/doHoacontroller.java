package com.example.sever.controller;


import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.DoHoaAddRequestDTO;
import com.example.sever.dto.request.DoHoaUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.DoHoaDisplayReponse;
import com.example.sever.entity.DoHoa;
import com.example.sever.service.DoHoaService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/do-hoa")
@AllArgsConstructor
public class doHoacontroller {
    DoHoaService doHoaService;
    @GetMapping()
    public ResponseEntity<Page<DoHoaDisplayReponse>> getAllLaptopsForDisplayPaged(@RequestParam(defaultValue = "1") int page,
                                                                                  @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<DoHoaDisplayReponse> doHoapage = doHoaService.getAllDoHoaforDisplayPage(pageable);
        return ResponseEntity.ok(doHoapage);
    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<DoHoaDisplayReponse>>> filterDoHoa(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            Pageable pageable) {

        Page<DoHoaDisplayReponse> result = doHoaService.getDoHoaByFilter(trangThai,keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<DoHoaDisplayReponse>>builder()
                        .message("Lọc thành công")
                        .data(result)
                        .build()
        );
    }

    @PostMapping("/them-do-hoa")
    public ApiResponse<DoHoa> addDoHoa(@RequestBody DoHoaAddRequestDTO doHoaRequestDTO) {
        DoHoa add = doHoaService.addDohoa(doHoaRequestDTO);
        return ApiResponse.<DoHoa>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-do-hoa")
    public ApiResponse<DoHoa> updateDohoa(@RequestBody DoHoaUpdateRequestDTO doHoaUpdateRequestDTO) {
        DoHoa updated = doHoaService.updateDohoa(doHoaUpdateRequestDTO);
        return ApiResponse.<DoHoa>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
    @PostMapping("/status")
    public ApiResponse<DoHoa> Status(@RequestBody StatusRequestDTO doHoaStatusRequestDTO) {
        DoHoa updatedStatus = doHoaService.updateStatus(doHoaStatusRequestDTO);
        return ApiResponse.<DoHoa>builder()
                .message("thay doi trang thai thanh cong")
                .data(updatedStatus)
                .build();
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<DoHoaDisplayReponse> getDoHoaById(@PathVariable("id") UUID id) {
        DoHoaDisplayReponse dohoa = doHoaService.getDetailedDoHoa(id);
        return ApiResponse.<DoHoaDisplayReponse>builder()
                .message("Lấy chi tiết CPU thành công")
                .data(dohoa)
                .build();
    }
}
