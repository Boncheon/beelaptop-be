package com.example.sever.dto.Pos;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosOrderDetailDTO {

    private UUID id;
    private String idOrder;      // cột id_order (code nội bộ)
    private String maDonHang;    // cột ma_don_hang (code hiển thị)
    private String loaiDon;      // "TAI_QUAY" / "GIAO_HANG"...

    // --- Khách hàng (snapshot trong Orders) ---
    private String tenKhachHang;
    private String sdtKhachHang;
    private String emailKhachHang;

    // --- Thông tin giao hàng (ô xám hiển thị) ---
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiGiaoHang;

    // --- ✅ ĐỊA CHỈ CHI TIẾT ĐỂ ĐỔ LÊN FORM + GHN ---
    private UUID idDiaChi;

    private String quocGia;
    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiChiTiet;

    private Integer provinceId;
    private Integer districtId;
    private String wardCode;

    // --- Giá trị đơn ---
    private BigDecimal giaTriChuaGiam;
    private BigDecimal giaTriGiamGia;
    private BigDecimal tongTienThuHo;
    private BigDecimal phiVanChuyen;

    private Integer trangThai;
    private UUID idNhanVien;
    private String maNhanVien;
    private Integer trangThaiThanhToan;

    // --- Nhân viên tạo đơn ---
    private String tenNhanVien;
    private String sdtNhanVien;
    private String emailNhanVien;

    private Instant ngayTao;
    private Instant ngayCapNhat;

    // Chi tiết
    private List<PosOrderItemDTO> items;
    private List<PosPaymentDTO> payments;
    private List<PosVoucherDTO> vouchers;

    private String ghiChu;
}
