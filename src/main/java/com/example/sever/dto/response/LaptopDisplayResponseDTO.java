package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO dùng để hiển thị thông tin laptop
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaptopDisplayResponseDTO {
    
    private Integer stt; // Số thứ tự
    private String anh; // Đường dẫn ảnh
    private UUID id; // ID của laptop
    
    private String tenSanPham; // Tên sản phẩm
    
    private String thuongHieuTen; // Tên thương hiệu
    
    private Integer soLuong; // Tổng số lượng từ laptop chi tiết
    
    private Boolean trangThai; // Trạng thái (true: đang bán, false: ngưng bán)
}