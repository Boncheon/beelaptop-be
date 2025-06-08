package com.example.sever.controler;

import com.example.sever.dto.response.LaptopDisplayResponseDTO;
import com.example.sever.repository.LapTopRepository;
import com.example.sever.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/laptops")
public class lapTopcontroller {


    @Autowired
    private LaptopService laptopService;

    //lấy tất cả laptop và phân trang

    @GetMapping("/paged")
    public ResponseEntity<Page<LaptopDisplayResponseDTO>> getAllLaptopsForDisplayPaged(@RequestParam(defaultValue = "1") int page,
                                                                                       @RequestParam(defaultValue = "4") int size) {
        int perPage = page - 1;
        if (perPage < 0) perPage = 0;
        Pageable pageable = PageRequest.of(perPage, size);
        Page<LaptopDisplayResponseDTO> laptopPage = laptopService.getAllLaptopsForDisplayPaged(pageable);
        return ResponseEntity.ok(laptopPage);
    }

    /**
     * Lấy laptop theo ID
     *
     * @param id ID của laptop
     * @return Thông tin laptop
     */
    @GetMapping("/{id}")
    public ResponseEntity<LaptopDisplayResponseDTO> getLaptopById(@PathVariable UUID id) {
        LaptopDisplayResponseDTO laptop = laptopService.getLaptopByIdForDisplay(id);
        if (laptop != null) {
            return ResponseEntity.ok(laptop);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //lấy tất cả laptop mặc định khi ko có page truyefn  vào

    @GetMapping
    public ResponseEntity<Page<LaptopDisplayResponseDTO>> getAllLaptopsDefaultPaged() {
        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        Page<LaptopDisplayResponseDTO> laptopPage = laptopService.getAllLaptopsForDisplayPaged(pageable);
        return ResponseEntity.ok(laptopPage);
    }

    //tìm kiêếm theo code và tên phân trang
    @GetMapping("/search")
    public ResponseEntity<?> searchLaptopsAdvanced(
            @RequestParam String keyword,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(required = false) String thuongHieuTen,
            @RequestParam(required = false) String danhMucTen,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Tính lại page (Spring bắt đầu từ 0)
        int perPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(perPage, size);

        Page<LaptopDisplayResponseDTO> result = laptopService.searchLaptopsByFilters(
                keyword, trangThai, thuongHieuTen, danhMucTen, pageable
        );

        if (result.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Không có dữ liệu nào");
            response.put("data", List.of());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        return ResponseEntity.ok(result);
    }


}