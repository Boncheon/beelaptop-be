package com.example.sever.dto.Pos;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
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
    private String loaiDon;      // "TAI_QUAY" / "ONLINE"...

    // --- Khách hàng ---
    private String tenKhachHang;
    private String sdtKhachHang;
    private String emailKhachHang;   // 🔥 thêm

    // --- Thông tin giao hàng (dùng để hiển thị ô xám) ---
    private String tenNguoiNhan;     // 🔥 thêm
    private String sdtNguoiNhan;     // 🔥 thêm

    // --- Giá trị đơn ---
    private BigDecimal giaTriChuaGiam;
    private BigDecimal giaTriGiamGia;
    private BigDecimal tongTienThuHo;

    private Integer trangThai;          // 0 = draft, 1 = đang xử lý, 2 = đã hoàn thành ...
    private UUID idNhanVien;            // ID trong bảng TaiKhoan
    private String maNhanVien;          // ví dụ: NV001 (idTaiKhoan)
    private Integer trangThaiThanhToan;

    // --- Nhân viên tạo đơn (để hiện trong card Thông tin nhân viên) ---
    private String tenNhanVien;         // 🔥 thêm
    private String sdtNhanVien;         // 🔥 thêm
    private String emailNhanVien;       // 🔥 thêm

    private Instant ngayTao;
    private Instant ngayCapNhat;

    // Chi tiết
    private List<PosOrderItemDTO> items;       // danh sách seri trong đơn
    private List<PosPaymentDTO> payments;      // các khoản thanh toán
    private List<PosVoucherDTO> vouchers;      // các voucher áp dụng
}
