package com.example.sever.service;

import com.example.sever.dto.request.LapTopCTAddRequestDTO;
import com.example.sever.dto.request.LapTopCTUpdateRequestDTO;
import com.example.sever.dto.response.LapTopCTDisplayReponse;
import com.example.sever.entity.LaptopChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LapTopCTService {

    /**
     * Lấy danh sách các phiên bản để hiển thị, có phân trang.
     *
     * @param pageable thông tin phân trang
     * @return danh sách phiên bản hiển thị
     */
    Page<LapTopCTDisplayReponse> getAllLapTopCTforDisplay(Pageable pageable);

    /**
     * Thêm mới một phiên bản sản phẩm.
     *
     * @param adddto DTO chứa thông tin phiên bản cần thêm
     * @return phiên bản sau khi lưu
     */

    LapTopCTDisplayReponse addLapTopCT(LapTopCTAddRequestDTO  adddto);
    LapTopCTDisplayReponse updateLapTopCT(LapTopCTUpdateRequestDTO dto);
    LapTopCTDisplayReponse getById (UUID id);
}
