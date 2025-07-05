package com.example.sever.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO dùng để nhận dữ liệu từ client khi tạo hoặc cập nhật laptop
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LaptopRequestDTO {
    
    private UUID id; // ID của laptop (null khi tạo mới)
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
    private String tenSanPham; // Tên sản phẩm
    
    @NotNull(message = "Giá nhập không được để trống")
    private BigDecimal giaNhap; // Giá nhập
    
    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String moTa; // Mô tả
    
    private UUID thuongHieuId; // ID của thương hiệu
    
    private Integer soLuong; // Số lượng
    
    private String trangThai; // Trạng thái
    
    private String anhURL; // URL của ảnh


}