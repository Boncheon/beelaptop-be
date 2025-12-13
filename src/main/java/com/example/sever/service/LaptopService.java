package com.example.sever.service;

import com.example.sever.dto.request.LaptopAddRequestDTO;
import com.example.sever.dto.request.LaptopUpdateRequestDTO;
import com.example.sever.dto.response.CustomerLaptopProjection;
import com.example.sever.dto.response.LapTopDisplayReponse;
import com.example.sever.dto.response.LaptopResponseDTO;
import com.example.sever.dto.response.Search.BrandSearchResponse;
import com.example.sever.dto.response.Search.LaptopSearchBrandProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Interface định nghĩa các phương thức service cho Laptop (base - bước 1)
 */
public interface LaptopService {

    /**
     * Lấy danh sách Laptop cơ bản (hiển thị trong trang quản trị)
     */
    Page<LapTopDisplayReponse> getAllLapTopForDisplay(Pageable pageable);

    /**
     * Thêm mới Laptop (Base)
     */
    LaptopResponseDTO addLaptop(LaptopAddRequestDTO dto);

    /**
     * Cập nhật thông tin Laptop (Base)
     */
    LaptopResponseDTO updateLaptop(UUID id, LaptopUpdateRequestDTO dto);

    /**
     * Lấy chi tiết Laptop + danh sách các biến thể (LaptopChiTiet)
     */
    LapTopDisplayReponse getDetailedLapTop(UUID id);


    //--------/

    List<CustomerLaptopProjection> getCustomerLaptop();
    List<CustomerLaptopProjection> getLatestLaptops();
    List<LaptopSearchBrandProjection> getSearchBrand(UUID idBrand);
    List<BrandSearchResponse> getAllBrand();
}
