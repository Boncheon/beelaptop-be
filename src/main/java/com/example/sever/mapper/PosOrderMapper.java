package com.example.sever.mapper;

import com.example.sever.dto.Pos.*;
import com.example.sever.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
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
    @Mapping(target = "phiVanChuyen", source = "phiVanChuyen")

    @Mapping(target = "trangThai", source = "trangThai")
    @Mapping(target = "trangThaiThanhToan", source = "trangThaiThanhToan")
    @Mapping(target = "ngayTao", source = "ngayTao")
    @Mapping(target = "ngayCapNhat", source = "ngayCapNhat")
    @Mapping(target = "ghiChu", source = "ghiChu")

    // email khách
    @Mapping(target = "emailKhachHang",
            expression = "java(order.getIdTaiKhoan() != null ? order.getIdTaiKhoan().getEmail() : null)")

    // nhân viên
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

    // ===== ✅ GIAO HÀNG: map ra form =====
    @Mapping(target = "idDiaChi",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getId() : null)")

    @Mapping(target = "quocGia",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getQuocGia() : null)")
    @Mapping(target = "tinhThanh",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getTinhThanh() : null)")
    @Mapping(target = "quanHuyen",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getQuanHuyen() : null)")
    @Mapping(target = "phuongXa",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getPhuongXa() : null)")
    @Mapping(target = "diaChiChiTiet",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getDiaChiChiTiet() : null)")

    @Mapping(target = "provinceId",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getProvinceId() : null)")
    @Mapping(target = "districtId",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getDistrictId() : null)")
    @Mapping(target = "wardCode",
            expression = "java(order.getIdDiaChi() != null ? order.getIdDiaChi().getWardCode() : null)")

    // ===== Ô xám (computed) =====
    @Mapping(target = "tenNguoiNhan",
            expression = "java(order.getIdDiaChi() != null && order.getIdDiaChi().getHoTen() != null ? " +
                    "order.getIdDiaChi().getHoTen() : order.getTenKhachHang())")
    @Mapping(target = "sdtNguoiNhan",
            expression = "java(order.getIdDiaChi() != null && order.getIdDiaChi().getSoDienThoai() != null ? " +
                    "order.getIdDiaChi().getSoDienThoai() : order.getSdtKhachHang())")
    @Mapping(target = "diaChiGiaoHang",
            expression = "java(buildDiaChiGiaoHang(order.getIdDiaChi()))")

    // list
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
    @Mapping(target = "anhUrl", ignore = true)
    PosOrderItemDTO toPosOrderItem(OrderCT orderCT);

    List<PosOrderItemDTO> toPosOrderItemList(Set<OrderCT> list);

    // ========== PAYMENTS ==========
    @Mapping(target = "id", source = "id")
    @Mapping(target = "idHinhThucThanhToan", source = "idHinhThucThanhToan.id")
    @Mapping(target = "tenHinhThuc", source = "idHinhThucThanhToan.tenHinhThuc")
    @Mapping(target = "soTien", source = "soTienThanhToan")
    @Mapping(target = "khachDua", source = "khachDua")
    @Mapping(target = "tienTraLai", source = "traLai")
    PosPaymentDTO toPosPayment(HinhThucThanhToanChiTiet entity);

    List<PosPaymentDTO> toPosPaymentList(Set<HinhThucThanhToanChiTiet> list);

    // ========== VOUCHERS ==========
    @Mapping(target = "idPhieuGiamGia", source = "idPhieuGiamGia.idPhieugiamgia")
    @Mapping(target = "ten", source = "idPhieuGiamGia.ten")
    @Mapping(target = "kieuGiamGia",
            expression = "java(entity.getIdPhieuGiamGia().getKieuGiamGia() != null ? " +
                    "entity.getIdPhieuGiamGia().getKieuGiamGia().name() : null)")
    @Mapping(target = "giaTriGiam",
            expression = "java(com.example.sever.mapper.PosOrderMapper.computeGiaTriGiam(entity))")
    @Mapping(target = "giaTriMin", source = "idPhieuGiamGia.giaTriMin")
    @Mapping(target = "trangThai", source = "idPhieuGiamGia.trangThai")
    PosVoucherDTO toPosVoucher(GiamGiaHoaDon entity);

    List<PosVoucherDTO> toPosVoucherList(Set<GiamGiaHoaDon> list);

    // ===== Helper địa chỉ =====
    default String buildDiaChiGiaoHang(DiaChi dc) {
        if (dc == null) return null;

        StringBuilder sb = new StringBuilder();
        if (dc.getDiaChiChiTiet() != null && !dc.getDiaChiChiTiet().isBlank()) sb.append(dc.getDiaChiChiTiet());
        if (dc.getPhuongXa() != null && !dc.getPhuongXa().isBlank()) { if (sb.length() > 0) sb.append(", "); sb.append(dc.getPhuongXa()); }
        if (dc.getQuanHuyen() != null && !dc.getQuanHuyen().isBlank()) { if (sb.length() > 0) sb.append(", "); sb.append(dc.getQuanHuyen()); }
        if (dc.getTinhThanh() != null && !dc.getTinhThanh().isBlank()) { if (sb.length() > 0) sb.append(", "); sb.append(dc.getTinhThanh()); }
        return sb.length() > 0 ? sb.toString() : null;
    }

    // Helper cấu hình laptop (giữ nguyên của bạn)
    default String buildCauHinh(LaptopChiTiet ct) {
        if (ct == null) return null;
        StringBuilder sb = new StringBuilder();

        if (ct.getIdCpu() != null) sb.append(ct.getIdCpu().getTen());
        if (ct.getIdRam() != null) { if (sb.length() > 0) sb.append(" / "); sb.append("RAM ").append(ct.getIdRam().getDungLuongRam()); }
        if (ct.getIdSsd() != null) { if (sb.length() > 0) sb.append(" / "); sb.append("SSD ").append(ct.getIdSsd().getDungLuongSsd()); }
        if (ct.getIdDohoa() != null) { if (sb.length() > 0) sb.append(" / "); sb.append("VGA ").append(ct.getIdDohoa().getTenDayDu()); }
        if (ct.getIdMauSac() != null) { if (sb.length() > 0) sb.append(" / "); sb.append("Màu ").append(ct.getIdMauSac().getTen()); }

        return sb.toString();
    }

    static BigDecimal computeGiaTriGiam(GiamGiaHoaDon e) {
        if (e == null) return BigDecimal.ZERO;
        BigDecimal truoc = e.getSoTienTruocGiam();
        BigDecimal sau = e.getSoTienSauGiam();
        if (truoc == null || sau == null) return BigDecimal.ZERO;
        BigDecimal diff = truoc.subtract(sau);
        return diff.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : diff;
    }
}
