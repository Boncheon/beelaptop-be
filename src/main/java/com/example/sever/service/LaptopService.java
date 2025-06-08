package com.example.sever.service;

import com.example.sever.dto.response.LaptopDisplayResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.Map;

/**
 * Interface định nghĩa các phương thức service cho Laptop
 */
public interface LaptopService {

    /**
     * Lấy tất cả laptop với thông tin hiển thị
     *
     * @return Danh sách các đối tượng LaptopDisplayResponseDTO
     */

    Page<LaptopDisplayResponseDTO> getAllLaptopsForDisplayPaged(Pageable pageable);




    /**
     * Lấy laptop theo ID để hiển thị
     *
     * @param id ID của laptop
     * @return Đối tượng LaptopDisplayResponseDTO hoặc null nếu không tìm thấy
     */
    LaptopDisplayResponseDTO getLaptopByIdForDisplay(UUID id);
    Page<LaptopDisplayResponseDTO> searchLaptopsByFilters(
            String keyword, Boolean trangThai, String thuongHieuTen, String danhMucTen, Pageable pageable);

}
