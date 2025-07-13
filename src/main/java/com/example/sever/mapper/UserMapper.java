package com.example.sever.mapper;



import com.example.sever.dto.request.UserCreationRequest;
import com.example.sever.dto.response.UserDetailResponse;
import com.example.sever.entity.DiaChi;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.repository.DiaChiRepository;
import org.mapstruct.Context;
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

//    @Mapping(target = "tenChucVu", source = "idRole.tenChucVu")
//    @Mapping(target = "trangThai", source = "trangThai")
//    UserDetailResponse toUserDetailResponse(TaiKhoan taiKhoan);

    default UserDetailResponse toUserDetailResponse(TaiKhoan taiKhoan, @Context DiaChiRepository diaChiRepository) {
        UserDetailResponse response = new UserDetailResponse();
        response.setId(taiKhoan.getId() != null ? taiKhoan.getId().toString() : null);
        response.setIdTaiKhoan(taiKhoan.getIdTaiKhoan());
        response.setTen(taiKhoan.getTen());
        response.setEmail(taiKhoan.getEmail());
        response.setSoDienThoai(taiKhoan.getSoDienThoai());
        response.setGioiTinh(taiKhoan.getGioiTinh());
        response.setNgaySinh(taiKhoan.getNgaySinh());
        response.setAnh(taiKhoan.getAnh());
        response.setTenChucVu(taiKhoan.getIdRole() != null ? taiKhoan.getIdRole().getTenChucVu() : null);
        response.setTrangThai(taiKhoan.getTrangThai());
        response.setCreatedAt(taiKhoan.getCreatedAt());
        response.setUpdatedAt(taiKhoan.getUpdatedAt());

        // Lấy địa chỉ từ DiaChi
        DiaChi address = diaChiRepository.findByIdTaiKhoan(taiKhoan).orElse(null);
        if (address != null) {
            response.setQuocGia(address.getQuocGia());
            response.setTinhThanh(address.getTinhThanh());
            response.setQuanHuyen(address.getQuanHuyen());
            response.setPhuongXa(address.getPhuongXa());
            response.setDiaChiChiTiet(address.getDiaChiChiTiet());
        }

        return response;
    }

}