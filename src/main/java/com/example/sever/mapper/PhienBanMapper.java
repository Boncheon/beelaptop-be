package com.example.sever.mapper;

import com.example.sever.dto.response.*;
import com.example.sever.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
