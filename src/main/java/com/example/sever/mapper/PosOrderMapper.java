package com.example.sever.mapper;

import com.example.sever.dto.Pos.*;
import com.example.sever.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PosOrderMapper {


    // ===== Order -> PosOrderDetailDTO =====
    @Mapping(target = "id", source = "id")
    @Mapping(target = "idOrder", source = "idOrder")
    @Mapping(target = "maDonHang", source = "maDonHang")
    @Mapping(target = "loaiDon", source = "loaiDon")
    @Mapping(target = "tenKhachHang", source = "tenKhachHang")
    @Mapping(target = "sdtKhachHang", source = "sdtKhachHang")
    @Mapping(target = "giaTriChuaGiam", source = "giaTriChuaGiam")
    @Mapping(target = "giaTriGiamGia", source = "giaTriGiamGia")
    @Mapping(target = "tongTienThuHo", source = "tongTienThuHo")
    @Mapping(target = "trangThai", source = "trangThai")

    // 🔥 thêm mapping mới
    @Mapping(target = "trangThaiThanhToan", source = "trangThaiThanhToan")
    @Mapping(target = "ngayTao", source = "ngayTao")
    @Mapping(target = "ngayCapNhat", source = "ngayCapNhat")

    // 🔥 email khách: lấy từ tài khoản nếu có (nếu bạn đặt tên field khác thì sửa lại getEmail() cho đúng)
    @Mapping(target = "emailKhachHang",
            expression = "java(order.getIdTaiKhoan() != null ? order.getIdTaiKhoan().getEmail() : null)")

    // 🔥 mapping nhân viên
    @Mapping(target = "idNhanVien",
            expression = "java(order.getIdNhanVien() != null ? order.getIdNhanVien().getId() : null)")
    @Mapping(target = "maNhanVien",
            expression = "java(order.getIdNhanVien() != null ? order.getIdNhanVien().getIdTaiKhoan() : null)")
    @Mapping(target = "tenNhanVien",
            expression = "java(order.getIdNhanVien() != null ? order.getIdNhanVien().getTen() : null)")
    @Mapping(target = "sdtNhanVien",
            expression = "java(order.getIdNhanVien() != null ? order.getIdNhanVien().getSoDienThoai() : null)")
    @Mapping(target = "emailNhanVien",
            expression = "java(order.getIdNhanVien() != null ? order.getIdNhanVien().getEmail() : null)")

    // ⚠ tenNguoiNhan & sdtNguoiNhan:
    // hiện tại mình để MapStruct không map (mặc định null),
    // FE sẽ fallback sang tenKhachHang / sdtKhachHang. Nếu sau này
    // bạn có field trong entity DiaChi thì mình map tiếp.

    @Mapping(target = "items", source = "orderCTS")
    @Mapping(target = "payments", source = "hinhThucThanhToanChiTiets")
    @Mapping(target = "vouchers", source = "giamGiaHoaDons")
    PosOrderDetailDTO toPosOrderDetail(Order order);


    // ========== ITEMS ==========
    @Mapping(target = "orderCtId", source = "id")
    @Mapping(target = "seriId", source = "idSeri.id")
    @Mapping(target = "maSeri", source = "idSeri.idSeri")
    @Mapping(target = "laptopCtId", source = "idSeri.idLapTopCt.id")
    @Mapping(target = "laptopId", source = "idSeri.idLapTopCt.idLaptop.id")
    @Mapping(target = "tenSanPham",
            expression = "java(orderCT.getIdSeri().getIdLapTopCt().getIdLaptop().getTenSanPham())")
    @Mapping(target = "cauHinh",
            expression = "java(buildCauHinh(orderCT.getIdSeri().getIdLapTopCt()))")
    @Mapping(target = "giaBan", source = "giaBan")
    PosOrderItemDTO toPosOrderItem(OrderCT orderCT);

    List<PosOrderItemDTO> toPosOrderItemList(Set<OrderCT> list);

    // ========== PAYMENTS ==========
    @Mapping(target = "id", source = "id")
    @Mapping(target = "idHinhThucThanhToan", source = "idHinhThucThanhToan.id")
    @Mapping(target = "tenHinhThuc", source = "idHinhThucThanhToan.tenHinhThuc")
    @Mapping(target = "soTien", source = "soTienThanhToan")  // số tiền thực thu
    @Mapping(target = "khachDua", source = "khachDua")       // khách đưa
    @Mapping(target = "tienTraLai", source = "traLai")       // tiền thừa trả
    PosPaymentDTO toPosPayment(HinhThucThanhToanChiTiet entity);

    List<PosPaymentDTO> toPosPaymentList(Set<HinhThucThanhToanChiTiet> list);

    // ========== VOUCHERS ==========
    @Mapping(target = "idPhieuGiamGia", source = "idPhieuGiamGia.idPhieugiamgia")
    @Mapping(target = "ten", source = "idPhieuGiamGia.ten")
    @Mapping(target = "kieuGiamGia",
            expression = "java( entity.getIdPhieuGiamGia().getKieuGiamGia() != null ? " +
                    "entity.getIdPhieuGiamGia().getKieuGiamGia().name() : null)")
    // 🔥 Số tiền thực giảm = soTienTruocGiam - soTienSauGiam
    @Mapping(target = "giaTriGiam",
            expression = "java( com.example.sever.mapper.PosOrderMapper.computeGiaTriGiam(entity) )")
    @Mapping(target = "giaTriMin", source = "idPhieuGiamGia.giaTriMin")
    @Mapping(target = "giaTriMax", source = "idPhieuGiamGia.giaTriMax")
    @Mapping(target = "trangThai", source = "idPhieuGiamGia.trangThai")
    PosVoucherDTO toPosVoucher(GiamGiaHoaDon entity);

    List<PosVoucherDTO> toPosVoucherList(Set<GiamGiaHoaDon> list);

    // Helper cấu hình laptop
    default String buildCauHinh(LaptopChiTiet ct) {
        if (ct == null) return null;
        StringBuilder sb = new StringBuilder();

        if (ct.getIdCpu() != null) {
            sb.append(ct.getIdCpu().getTen());
        }
        if (ct.getIdRam() != null) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append("RAM ").append(ct.getIdRam().getDungLuongRam());
        }
        if (ct.getIdSsd() != null) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append("SSD ").append(ct.getIdSsd().getDungLuongSsd());
        }
        if (ct.getIdDohoa() != null) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append("VGA ").append(ct.getIdDohoa().getTenDayDu());
        }
        if (ct.getIdMauSac() != null) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append("Màu ").append(ct.getIdMauSac().getTen());
        }
        return sb.toString();
    }

    // 🔥 Hàm dùng trong expression để tính số tiền giảm
    static BigDecimal computeGiaTriGiam(GiamGiaHoaDon e) {
        if (e == null) return BigDecimal.ZERO;
        BigDecimal truoc = e.getSoTienTruocGiam();
        BigDecimal sau = e.getSoTienSauGiam();
        if (truoc == null || sau == null) return BigDecimal.ZERO;
        return truoc.subtract(sau);
    }
}
