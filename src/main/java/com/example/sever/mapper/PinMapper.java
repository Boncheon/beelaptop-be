package com.example.sever.mapper;

import com.example.sever.dto.request.PinAddRequestDTO;
import com.example.sever.dto.request.PinUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.dto.response.PinDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.Pin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.web.bind.annotation.Mapping;

@Mapper(componentModel = "spring")
public interface PinMapper {

    // ====== ADD ======
    Pin toPin(PinAddRequestDTO request);

    // ====== DISPLAY ======
    PinDisplayReponse toPinDisplayReponse(Pin pin);

    PinDisplayReponse getAlldisplayPin(Pin pin);

    // ====== UPDATE ======
    // Không động vào id, idPin, ngày tạo / sửa
    void updatePin(@MappingTarget Pin pin, PinUpdateRequestDTO request);
}
