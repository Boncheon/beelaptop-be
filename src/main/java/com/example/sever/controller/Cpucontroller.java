package com.example.sever.controller;

import com.example.sever.dto.ApiResponse;
import com.example.sever.dto.request.CpuAddRequestDTO;
import com.example.sever.dto.request.CpuUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.entity.Cpu;
import com.example.sever.service.CpuService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@CrossOrigin("*")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/cpu")
@AllArgsConstructor
public class Cpucontroller {

    CpuService cpuService;

    @GetMapping()
    public ResponseEntity<Page<CpuDisplayReponse>> getAllramforDisplay(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "20") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<CpuDisplayReponse> cpuPage = cpuService.getAllCpuforDisplay(pageable);
        return ResponseEntity.ok(cpuPage);
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<CpuDisplayReponse> getCpuById(@PathVariable("id") UUID id) {
        CpuDisplayReponse cpu = cpuService.getDetailedCpu(id);
        return ApiResponse.<CpuDisplayReponse>builder()
                .message("Lấy chi tiết CPU thành công")
                .data(cpu)
                .build();
    }
    @PostMapping("/them-cpu")
    public ApiResponse<Cpu> addCpu(@RequestBody CpuAddRequestDTO cpuAddRequestDTO) {
        Cpu add = cpuService.addCpu(cpuAddRequestDTO);
        return ApiResponse.<Cpu>builder()
                .message("them thanh cong")
                .data(add)
                .build();
    }
    @PostMapping("/sua-cpu")
    public ApiResponse<Cpu> updateCpu(@RequestBody CpuUpdateRequestDTO cpuUpdateRequestDTO) {
//        cpuUpdateRequestDTO.setIdCpu(id.toString());
        Cpu  updated = cpuService.updateCpu(cpuUpdateRequestDTO);
        return ApiResponse.<Cpu>builder()
                .message("cap nhap thanh cong")
                .data(updated)
                .build();
    }
    @PostMapping("/status")
    public ApiResponse<Cpu> Status(@RequestBody StatusRequestDTO statusRequestDTO) {
        Cpu updatedStatus = cpuService.updateStatus(statusRequestDTO);
        return ApiResponse.<Cpu>builder()
                .message("thay doi trang thai thanh cong")
                .data(updatedStatus)
                .build();
    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<CpuDisplayReponse>>> filterCpu(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            Pageable pageable) {

        Page<CpuDisplayReponse> result = cpuService.getCpuByFilter(trangThai,keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<CpuDisplayReponse>>builder()
                        .message("Lọc thành công")
                        .data(result)
                        .build()
        );
    }
}