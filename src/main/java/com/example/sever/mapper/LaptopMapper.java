package com.example.sever.mapper;

import com.example.sever.dto.response.LaptopDisplayResponseDTO;
import com.example.sever.enity.Laptop;
import com.example.sever.enity.LaptopChiTiet;
import com.example.sever.enity.ThuongHieu;
import com.example.sever.repository.LaptopChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Mapper để chuyển đổi giữa các đối tượng Laptop và DTO
 */
@Component
public class LaptopMapper {
    
    @Autowired
    private LaptopChiTietRepository laptopChiTietRepository;
    
    /**
     * Chuyển đổi từ Laptop, LaptopChiTiet, ThuongHieu sang LaptopDisplayResponseDTO
     * 
     * @param laptop Đối tượng Laptop
     * @param laptopChiTiet Đối tượng LaptopChiTiet
     * @param thuongHieu Đối tượng ThuongHieu
     * @param stt Số thứ tự
     * @return Đối tượng LaptopDisplayResponseDTO
     */
    public LaptopDisplayResponseDTO toLaptopDisplayResponseDTO(Laptop laptop, LaptopChiTiet laptopChiTiet, 
                                                              ThuongHieu thuongHieu, Integer stt) {
        if (laptop == null) {
            return null;
        }
        
        // Tính tổng số lượng từ các laptop chi tiết
        Integer tongSoLuong = 0;
        if (laptop.getId() != null) {
            // Sử dụng trực tiếp từ tham số nếu có
            if (laptopChiTiet != null && laptopChiTiet.getSoLuong() != null) {
                tongSoLuong = laptopChiTiet.getSoLuong();
            } else {
                // Hoặc lấy từ repository nếu cần
                List<LaptopChiTiet> chiTiets = laptopChiTietRepository.findByIdLaptop(laptop);
                tongSoLuong = chiTiets.stream()
                        .mapToInt(LaptopChiTiet::getSoLuong)
                        .sum();
            }
        }
        
        return LaptopDisplayResponseDTO.builder()
                .stt(stt)
                .id(laptop.getId())
                .tenSanPham(laptop.getTenSanPham())
                .thuongHieuTen(thuongHieu != null ? thuongHieu.getTen() : null)
                .soLuong(tongSoLuong)
                .trangThai(laptopChiTiet != null ? laptopChiTiet.getTrangThai() : null)
                .build();
    }
    
    /**
     * Chuyển đổi từ danh sách kết quả truy vấn sang danh sách LaptopDisplayResponseDTO
     * 
     * @param results Danh sách kết quả truy vấn
     * @return Danh sách các đối tượng LaptopDisplayResponseDTO
     */
    public List<LaptopDisplayResponseDTO> toLaptopDisplayResponseDTOList(List<Object[]> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<LaptopDisplayResponseDTO> dtos = new ArrayList<>();
        
        IntStream.range(0, results.size()).forEach(i -> {
            Object[] result = results.get(i);
            Laptop laptop = (Laptop) result[0];
            LaptopChiTiet laptopChiTiet = result.length > 1 ? (LaptopChiTiet) result[1] : null;
            ThuongHieu thuongHieu = result.length > 3 ? (ThuongHieu) result[3] : null;
            
            dtos.add(toLaptopDisplayResponseDTO(laptop, laptopChiTiet, thuongHieu, i + 1));
        });
        
        return dtos;
    }
}