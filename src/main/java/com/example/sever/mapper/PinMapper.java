package com.example.sever.mapper;

import com.example.sever.dto.request.PinAddRequestDTO;
import com.example.sever.dto.request.PinUpdateRequestDTO;
import com.example.sever.dto.response.HeDieuHanhDisplayReponse;
import com.example.sever.dto.response.PinDisplayReponse;
import com.example.sever.entity.HeDieuHanh;
import com.example.sever.entity.Pin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PinMapper {

    Pin toPin(PinAddRequestDTO request);
    PinDisplayReponse toPinDisplayReponse(Pin pin);
    PinDisplayReponse getAlldisplayPin(Pin pin);

    void updatePin(@MappingTarget Pin pin, PinUpdateRequestDTO request);
}
