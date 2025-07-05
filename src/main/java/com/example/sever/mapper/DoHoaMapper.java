package com.example.sever.mapper;

import com.example.sever.dto.request.DoHoaAddRequestDTO;
import com.example.sever.dto.request.DoHoaUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.DoHoaDisplayReponse;
import com.example.sever.entity.DoHoa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DoHoaMapper {

    DoHoa toDoHoa(DoHoaAddRequestDTO request);

    DoHoaDisplayReponse toDoHoaDisplayReponse(DoHoa doHoa);

    void updateDoHoa(@MappingTarget DoHoa doHoa, DoHoaUpdateRequestDTO request);
    void updateStatusDoHoa(@MappingTarget DoHoa doHoa, StatusRequestDTO request);
}
