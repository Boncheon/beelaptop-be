package com.example.sever.service;

import com.example.sever.dto.request.LapTopCTAddRequestDTO;
import com.example.sever.dto.request.LapTopCTAutoGenRequestDTO;
import com.example.sever.dto.request.LapTopCTUpdateRequestDTO;
import com.example.sever.dto.response.CustomerLaptopChiTietResponse;
import com.example.sever.dto.response.GioHang.ProductCartResponse;
import com.example.sever.dto.response.LaptopChiTietResponseDTO;
import com.example.sever.dto.response.ListLaptopCustomerProjection;
import com.example.sever.dto.response.Search.LaptopSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LapTopCTService {

    /**
     * Lấy danh sách biến thể có phân trang.
     */
    Page<LaptopChiTietResponseDTO> getAll(Pageable pageable);

    /**
     * Lấy tất cả biến thể thuộc 1 Laptop base.
     */
    List<LaptopChiTietResponseDTO> getByLaptop(UUID idLaptop);

    /**
     * Lấy chi tiết một biến thể.
     */
    LaptopChiTietResponseDTO getById(UUID id);

    /**
     * Thêm biến thể cho laptop.
     */
    LaptopChiTietResponseDTO add(UUID idLaptop, LapTopCTAddRequestDTO dto);

    /**
     * Sửa biến thể (update cấu hình, giá, màu sắc…).
     */
    LaptopChiTietResponseDTO update(UUID id, LapTopCTUpdateRequestDTO dto);

    /**
     * Chỉ thay đổi trạng thái biến thể (1 = hoạt động, 0 = ngừng).
     */
    LaptopChiTietResponseDTO updateStatus(UUID id, Integer trangThai);

    List<LaptopChiTietResponseDTO> autoGenVariants(LapTopCTAutoGenRequestDTO req);

    List<CustomerLaptopChiTietResponse> getLapTopCustomer(UUID laptopId);
    String checkQuantityProduct(UUID id , int quantity);
    List<ListLaptopCustomerProjection> listProductLaptop();

    ProductCartResponse getProductForCustomer(UUID id);
    List<LaptopSearchResponse> searchLaptopCustomer(String keyword);
}
