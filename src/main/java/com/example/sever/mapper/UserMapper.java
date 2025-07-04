package com.example.sever.mapper;


import com.example.sever.dto.req.UserCreationRequest;
import com.example.sever.dto.res.UserDetailResponse;
import com.example.sever.entity.TaiKhoan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "trangThai", ignore = true)
    @Mapping(target = "idRole", ignore = true)
    @Mapping(target = "anh", ignore = true)
    @Mapping(target = "idTaiKhoan", ignore = true)
    @Mapping(source = "matKhau", target = "matKhau")
    TaiKhoan toTaiKhoan(UserCreationRequest request);

    @Mapping(target = "tenChucVu", source = "idRole.tenChucVu")
    UserDetailResponse toUserDetailResponse(TaiKhoan taiKhoan);
}