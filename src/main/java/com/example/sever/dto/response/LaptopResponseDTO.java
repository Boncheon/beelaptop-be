package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO dùng để trả về dữ liệu chi tiết của laptop cho client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaptopResponseDTO {
    
    private UUID id; // ID của laptop
    private String tenSanPham; // Tên sản phẩm
    private BigDecimal giaNhap; // Giá nhập
    private LocalDateTime ngayTao; // Ngày tạo
    private LocalDateTime ngaySua; // Ngày sửa
    private String moTa; // Mô tả
    
    // Thông tin chi tiết
    private UUID laptopChiTietId; // ID của chi tiết laptop
    private Integer soLuong; // Số lượng
    private Boolean trangThai; // Trạng thái
    
    // Thông tin thương hiệu
    private UUID thuongHieuId; // ID của thương hiệu
    private String tenThuongHieu; // Tên thương hiệu
    
    // Thông tin ảnh
    private String anhURL; // URL của ảnh
    
    // Thông tin cấu hình
    private String tenCPU; // Tên CPU
    private String dungLuongRAM; // Dung lượng RAM
    private String dungLuongROM; // Dung lượng ROM
    private String cardDoHoa; // Card đồ họa
    private String kichThuocManHinh; // Kích thước màn hình
    private String mauSac; // Màu sắc
}