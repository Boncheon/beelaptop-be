package com.example.sever.service;

import com.example.sever.dto.request.PhienBanAddRequestDTO;
import com.example.sever.dto.request.PhienBanUpdateRequestDTO;
import com.example.sever.dto.response.PhienBanDisplayReponse;
import com.example.sever.enity.PhienBan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PhienBanService {

    /**
     * Lấy danh sách các phiên bản để hiển thị, có phân trang.
     *
     * @param pageable thông tin phân trang
     * @return danh sách phiên bản hiển thị
     */
    Page<PhienBanDisplayReponse> getAllPhienBanforDisplay(Pageable pageable);

    /**
     * Thêm mới một phiên bản sản phẩm.
     *
     * @param adddto DTO chứa thông tin phiên bản cần thêm
     * @return phiên bản sau khi lưu
     */
    PhienBanDisplayReponse addPhienBan(PhienBanAddRequestDTO  adddto);
    PhienBanDisplayReponse updatePhienBan(PhienBanUpdateRequestDTO dto);
}
