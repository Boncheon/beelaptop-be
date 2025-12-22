package com.example.sever.mapper;

import com.example.sever.dto.request.KichThuocAddRequestDTO;
import com.example.sever.dto.request.KichThuocUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.dto.response.KichThuocDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.KichThuoc;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface KichThuocMapper {

    // Add
    KichThuoc toKichThuoc(KichThuocAddRequestDTO request);

    // Detail
    KichThuocDisplayReponse toKichThuocDisplayReponse(KichThuoc kichthuoc);

    // List
    KichThuocDisplayReponse getAlldisplayKichThuoc(KichThuoc kichthuoc);

    // Update (ignore field = null, giữ nguyên giá trị cũ)
    void updateKichThuoc(@MappingTarget KichThuoc kichthuoc,
                         KichThuocUpdateRequestDTO request);

    // Update trạng thái (nếu bạn có API đổi trạng thái riêng)
    void updateStatusKichThuoc(@MappingTarget KichThuoc kichthuoc,
                               StatusRequestDTO request);
}
