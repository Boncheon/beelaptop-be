package com.example.sever.mapper;

import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.dto.response.DoHoaDisplayReponse;
import com.example.sever.dto.response.MauSacDisplayReponse;
import com.example.sever.dto.response.PhienBanDisplayReponse;
import com.example.sever.dto.response.RamDIsplayReponse;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.entity.Cpu;
import com.example.sever.entity.DoHoa;
import com.example.sever.entity.MauSac;
import com.example.sever.entity.PhienBan;
import com.example.sever.entity.Ram;
import com.example.sever.entity.Rom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PhienBanMapper {

    @Mappings({
            @Mapping(source = "idRam.dungLuongRam", target = "tenRam"),
            @Mapping(source = "idSsd.dungLuongSsd", target = "tenRom"),
            @Mapping(source = "idCpu.ten", target = "tenCpu"),
            @Mapping(source = "idDohoa.tenDayDu", target = "tenDoHoa"),
            @Mapping(source = "idMauSac.ten", target = "tenMauSac")
    })
    PhienBanDisplayReponse getAlldisplayPhienBan(PhienBan entity);

//    PhienBanDisplayReponse updatePhienBan(PhienBanUpdateRequestDTO dto);
}
