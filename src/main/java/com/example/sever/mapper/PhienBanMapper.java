package com.example.sever.mapper;

import com.example.sever.dto.request.PhienBanAddRequestDTO;
import com.example.sever.dto.request.PhienBanUpdateRequestDTO;
import com.example.sever.dto.request.StatusRequestDTO;
import com.example.sever.dto.response.CpuDisplayReponse;
import com.example.sever.dto.response.DoHoaDisplayReponse;
import com.example.sever.dto.response.MauSacDisplayReponse;
import com.example.sever.dto.response.PhienBanDisplayReponse;
import com.example.sever.dto.response.RamDIsplayReponse;
import com.example.sever.dto.response.RomDisplayReponse;
import com.example.sever.enity.Cpu;
import com.example.sever.enity.DoHoa;
import com.example.sever.enity.MauSac;
import com.example.sever.enity.PhienBan;
import com.example.sever.enity.Ram;
import com.example.sever.enity.Rom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PhienBanMapper {

    @Mapping(source = "idRam", target = "ram")
    @Mapping(source = "idSsd", target = "rom")
    @Mapping(source = "idCpu", target = "cpu")
    @Mapping(source = "idDohoa", target = "doHoa")
    @Mapping(source = "idMauSac", target = "mauSac")
    PhienBanDisplayReponse getAlldisplayPhienBan(PhienBan entity);

    RamDIsplayReponse toRamDisplay(Ram ram);
    RomDisplayReponse toRomDisplay(Rom rom);
    CpuDisplayReponse toCpuDisplay(Cpu cpu);
    DoHoaDisplayReponse toDoHoaDisplay(DoHoa doHoa);
    MauSacDisplayReponse toMauSacDisplay(MauSac mauSac);

//    PhienBanDisplayReponse updatePhienBan(PhienBanUpdateRequestDTO dto);
}
