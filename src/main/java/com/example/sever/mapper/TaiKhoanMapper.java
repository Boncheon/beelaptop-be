package com.example.sever.mapper;

import com.example.sever.dto.response.RoleDisplayReponse;
import com.example.sever.dto.response.TaiKhoanDisplayReponse;
import com.example.sever.entity.Role;
import com.example.sever.entity.TaiKhoan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaiKhoanMapper {

    @Mapping(source = "idRole", target = "role")
    TaiKhoanDisplayReponse getAlldisplayTaiKhoan(TaiKhoan entity);

    RoleDisplayReponse toRoleDisplay(Role role);
//    TaiKhoanDisplayReponse updateTaiKhoan(TaiKhoanUpdateRequestDTO dto);
}
