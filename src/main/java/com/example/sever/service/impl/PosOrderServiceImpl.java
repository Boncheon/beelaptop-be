package com.example.sever.service.impl;

import com.example.sever.dto.OrderDTO.OrderRespone;
import com.example.sever.dto.Pos.*;
import com.example.sever.entity.*;
import com.example.sever.mapper.PosOrderMapper;
import com.example.sever.repository.*;
import com.example.sever.service.PhieuGiamGiaService;
import com.example.sever.service.PosOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PosOrderServiceImpl implements PosOrderService {

    private final OrderRepository orderRepository;
    private final OrderCTRepository orderCTRepository;
    private final SeriRepository seriRepository;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepository;
    private final HinhThucThanhToanChiTietRepository hinhThucThanhToanChiTietRepository;
    private final GiamGiaHoaDonRepository giamGiaHoaDonRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

    private final PhieuGiamGiaService phieuGiamGiaService;
    private final PosOrderMapper posOrderMapper;

    private static final int ORDER_STATUS_DRAFT = 1;
    private static final int ORDER_STATUS_COMPLETED = 2;
    private static final int ORDER_STATUS_CANCELLED = 3;

    private static final int SERI_ACTIVE = 1;
    private static final int SERI_PENDING = 2;
    private static final int SERI_SOLD = 3;

    // trạng thái thanh toán
    private static final int PAYMENT_STATUS_UNPAID = 0;   // chưa thanh toán
    private static final int PAYMENT_STATUS_PAID   = 1;   // đã thanh toán

    // ===== TẠO ĐƠN NHÁP =====
    @Override
    @Transactional
    public PosOrderDetailDTO createDraftOrder(PosCreateOrderRequest request) {
        Order order = new Order();

        order.setId(UUID.randomUUID());
        order.setIdOrder("ORD" + System.currentTimeMillis());
        order.setMaDonHang(generateMaDonHang());
        order.setLoaiDon(request.getLoaiDon() != null ? request.getLoaiDon() : "TAI_QUAY");

        order.setTenKhachHang(request.getTenKhachHang());
        order.setSdtKhachHang(request.getSdtKhachHang());

        if (request.getIdTaiKhoan() != null) {
            TaiKhoan tk = new TaiKhoan();
            tk.setId(request.getIdTaiKhoan());
            order.setIdTaiKhoan(tk);
        }

        if (request.getIdDiaChi() != null) {
            DiaChi dc = new DiaChi();
            dc.setId(request.getIdDiaChi());
            order.setIdDiaChi(dc);
        }

        // 🔥 lấy ID nhân viên / admin đang login
        UUID nhanVienId = getCurrentUserId();
        if (nhanVienId != null) {
            TaiKhoan nhanVien = new TaiKhoan();
            nhanVien.setId(nhanVienId);
            order.setIdNhanVien(nhanVien);    // truyền TaiKhoan, không truyền UUID
        }

        order.setGiaTriChuaGiam(BigDecimal.ZERO);
        order.setGiaTriGiamGia(BigDecimal.ZERO);
        order.setTongTienThuHo(BigDecimal.ZERO);
        order.setPhiVanChuyen(BigDecimal.ZERO);
        order.setPhiDichVuKhac(BigDecimal.ZERO);

        order.setTrangThai(ORDER_STATUS_DRAFT);
        order.setTrangThaiThanhToan(PAYMENT_STATUS_UNPAID);   // 🔥 mặc định chưa thanh toán
        order.setGhiChu(request.getGhiChu());

        orderRepository.save(order);
        return posOrderMapper.toPosOrderDetail(order);
    }

    // ===== THÊM SERI VÀO ĐƠN (ACTIVE -> PENDING) =====
    @Override
    @Transactional
    public PosOrderDetailDTO addItems(UUID orderId, PosAddItemsRequest request) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        if (request.getSeriIds() == null || request.getSeriIds().isEmpty()) {
            throw new IllegalArgumentException("Danh sách seri không được rỗng");
        }

        List<OrderCT> toSave = new ArrayList<>();
        List<Seri> seriToUpdate = new ArrayList<>();

        for (UUID seriId : request.getSeriIds()) {

            Seri seri = seriRepository.findById(seriId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Seri: " + seriId));

            if (!Objects.equals(seri.getTrangThai(), SERI_ACTIVE)) {
                throw new IllegalStateException(
                        "Seri " + seri.getIdSeri() + " không còn ở trạng thái hoạt động");
            }

            if (seri.getIdLapTopCt() == null
                    || seri.getIdLapTopCt().getGiaBan() == null) {
                throw new IllegalStateException("Không có giá bán cho seri " + seri.getIdSeri());
            }

            OrderCT line = new OrderCT();
            line.setId(UUID.randomUUID());
            line.setIdOrder(order);
            line.setIdSeri(seri);
            line.setGiaBan(seri.getIdLapTopCt().getGiaBan());

            toSave.add(line);

            // 🔥 giữ hàng: chuyển seri -> PENDING
            seri.setTrangThai(SERI_PENDING);
            seriToUpdate.add(seri);
        }

        orderCTRepository.saveAll(toSave);
        seriRepository.saveAll(seriToUpdate);

        recalcOrderTotals(order);

        return getDetail(orderId);
    }

    // ===== XÓA 1 DÒNG SERI (trả về ACTIVE) =====
    @Override
    @Transactional
    public PosOrderDetailDTO removeItem(UUID orderId, UUID orderCtId) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        OrderCT ct = orderCTRepository.findById(orderCtId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy OrderCT: " + orderCtId));

        if (!ct.getIdOrder().getId().equals(order.getId())) {
            throw new IllegalArgumentException("Dòng chi tiết không thuộc đơn này");
        }

        if (ct.getIdSeri() != null) {
            Seri seri = ct.getIdSeri();
            seri.setTrangThai(SERI_ACTIVE);
            seriRepository.save(seri);
        }

        orderCTRepository.delete(ct);
        recalcOrderTotals(order);

        return getDetail(orderId);
    }

    // ===== CHỌN / CẬP NHẬT KHÁCH HÀNG =====
    @Override
    @Transactional
    public PosOrderDetailDTO selectCustomer(UUID orderId, PosSelectCustomerRequest request) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        if (request.getIdTaiKhoan() != null) {
            TaiKhoan tk = new TaiKhoan();
            tk.setId(request.getIdTaiKhoan());
            order.setIdTaiKhoan(tk);
        } else {
            order.setIdTaiKhoan(null);
        }

        if (request.getIdDiaChi() != null) {
            DiaChi dc = new DiaChi();
            dc.setId(request.getIdDiaChi());
            order.setIdDiaChi(dc);
        } else {
            order.setIdDiaChi(null);
        }

        order.setTenKhachHang(request.getTenKhachHang());
        order.setSdtKhachHang(request.getSdtKhachHang());

        orderRepository.save(order);
        return getDetail(orderId);
    }

    // ===== ÁP VOUCHER CHO ĐƠN DRAFT =====
    @Override
    @Transactional
    public OrderRespone applyVoucher(UUID orderId, PosApplyVoucherRequest request) {

        if (request == null
                || request.getVoucherIds() == null
                || request.getVoucherIds().isEmpty()) {
            throw new IllegalArgumentException("Thiếu ID phiếu giảm giá");
        }

        // Lấy mã phiếu dạng "PGG001"
        String voucherCode = request.getVoucherIds().get(0);

        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        // Tìm theo mã phiếu (idPhieugiamgia), KHÔNG dùng UUID nữa
        PhieuGiamGia phieu = phieuGiamGiaRepository
                .findByIdPhieugiamgia(voucherCode)
                .orElseThrow(() ->
                        new IllegalArgumentException("Phiếu giảm giá không tồn tại: " + voucherCode));

        // Tổng tiền hàng từ chi tiết
        BigDecimal tongTien = nvl(orderCTRepository.sumGiaBanByOrderId(order.getId()));
        order.setGiaTriChuaGiam(tongTien);

        // Tính số tiền giảm
        BigDecimal soTienGiam = phieuGiamGiaService.calculateDiscount(phieu, tongTien);
        order.setGiaTriGiamGia(soTienGiam);

        BigDecimal mustPay = tongTien.subtract(soTienGiam);
        if (mustPay.compareTo(BigDecimal.ZERO) < 0) {
            mustPay = BigDecimal.ZERO;
        }
        order.setTongTienThuHo(mustPay);

        // Tìm giảm giá hóa đơn hiện tại
        List<GiamGiaHoaDon> olds = giamGiaHoaDonRepository.findByIdOrders_Id(order.getId());

        GiamGiaHoaDon g;
        if (!olds.isEmpty()) {
            g = olds.get(0);
            for (int i = 1; i < olds.size(); i++) {
                giamGiaHoaDonRepository.delete(olds.get(i));
            }
        } else {
            g = new GiamGiaHoaDon();
            g.setIdGiamgiahoadon("GG" + System.currentTimeMillis());
            g.setIdOrders(order);
        }

        g.setIdPhieuGiamGia(phieu);
        g.setSoTienTruocGiam(tongTien);
        g.setSoTienSauGiam(mustPay);

        orderRepository.save(order);
        giamGiaHoaDonRepository.save(g);

        OrderRespone res = new OrderRespone();
        res.setMaDonHang(order.getMaDonHang());
        res.setTongTien(order.getGiaTriChuaGiam());
        res.setSoTienGiam(order.getGiaTriGiamGia());
        res.setPhieuGiamGia(phieu.getIdPhieugiamgia()); // trả về đúng mã
        res.setTongPhaiTra(order.getTongTienThuHo());

        return res;
    }

    // ===== THÊM THANH TOÁN (TIỀN KHÁCH ĐƯA) =====
    @Override
    @Transactional
    public PosOrderDetailDTO addPayment(UUID orderId, PosAddPaymentRequest request) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        if (request.getIdHinhThucThanhToan() == null) {
            throw new IllegalArgumentException("Thiếu id hình thức thanh toán");
        }

        HinhThucThanhToan hinhThuc = hinhThucThanhToanRepository
                .findById(request.getIdHinhThucThanhToan())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy hình thức thanh toán: " + request.getIdHinhThucThanhToan()));

        if (request.getKhachDua() == null
                || request.getKhachDua().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền khách đưa phải > 0");
        }

        // ✅ luôn cập nhật lại tổng cần thanh toán
        recalcOrderTotals(order);

        BigDecimal mustPay = nvl(order.getTongTienThuHo());                               // tổng phải thu
        BigDecimal paidBefore =
                nvl(hinhThucThanhToanChiTietRepository.sumSoTienByOrder(orderId));        // đã thanh toán trước đó

        // Nếu đã thanh toán đủ rồi thì không cho thêm payment nữa
        if (paidBefore.compareTo(mustPay) >= 0) {
            throw new IllegalStateException("Đơn hàng đã thanh toán đủ, không thể thêm thanh toán.");
        }

        BigDecimal remaining = mustPay.subtract(paidBefore);                               // còn phải thu
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        BigDecimal khachDua = request.getKhachDua();
        BigDecimal soTienThanhToan = khachDua.min(remaining);                              // chỉ thu tối đa = remaining
        BigDecimal tienTraLai = khachDua.subtract(soTienThanhToan);                        // tiền thừa trả lại

        // Lưu chi tiết thanh toán
        HinhThucThanhToanChiTiet ct = new HinhThucThanhToanChiTiet();
        ct.setId(UUID.randomUUID());
        ct.setIdHinhthucthanhtoanchitiet("PAY" + System.currentTimeMillis());
        ct.setIdOrder(order);
        ct.setIdHinhThucThanhToan(hinhThuc);
        ct.setSoTienThanhToan(soTienThanhToan);
        ct.setKhachDua(khachDua);
        ct.setTraLai(tienTraLai);

        hinhThucThanhToanChiTietRepository.save(ct);

        // ✅ TÍNH LẠI TỔNG ĐÃ THANH TOÁN SAU KHI THÊM BẢN GHI MỚI
        BigDecimal paidAfter =
                nvl(hinhThucThanhToanChiTietRepository.sumSoTienByOrder(orderId));

        // 🔥 CẬP NHẬT TRẠNG THÁI THANH TOÁN
        //  - Nếu paidAfter >= mustPay  → ĐÃ THANH TOÁN
        //  - Ngược lại                  → CHƯA THANH TOÁN (có thể đã thanh toán một phần)
        if (paidAfter.compareTo(mustPay) >= 0) {
            order.setTrangThaiThanhToan(PAYMENT_STATUS_PAID);
        } else {
            order.setTrangThaiThanhToan(PAYMENT_STATUS_UNPAID);
        }

        orderRepository.save(order);

        return getDetail(orderId);
    }

    // ===== HOÀN TẤT ĐƠN (PENDING -> SOLD, trừ voucher) =====
    @Override
    @Transactional
    public PosOrderDetailDTO complete(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        List<OrderCT> items = orderCTRepository.findByIdOrder_Id(orderId);
        if (items.isEmpty()) {
            throw new IllegalStateException("Đơn hàng không có sản phẩm");
        }

        recalcOrderTotals(order);

        BigDecimal mustPay = nvl(order.getTongTienThuHo());
        BigDecimal totalPaid =
                nvl(hinhThucThanhToanChiTietRepository.sumSoTienByOrder(orderId));

        if (totalPaid.compareTo(mustPay) < 0) {
            throw new IllegalStateException(
                    "Chưa thanh toán đủ tiền. Đã trả: "
                            + totalPaid + ", cần: " + mustPay);
        }

        // Chốt seri: PENDING -> SOLD
        for (OrderCT ct : items) {
            if (ct.getIdSeri() != null) {
                Seri seri = ct.getIdSeri();
                seri.setTrangThai(SERI_SOLD);
                seriRepository.save(seri);
            }
        }

        // 🔥 Trừ số lượng voucher khi đơn đã hoàn tất
        List<GiamGiaHoaDon> discounts =
                giamGiaHoaDonRepository.findByIdOrders_Id(orderId);
        for (GiamGiaHoaDon d : discounts) {
            PhieuGiamGia phieu = d.getIdPhieuGiamGia();
            if (phieu != null && phieu.getSoLuong() != null) {
                int current = phieu.getSoLuong();
                if (current > 0) {
                    phieu.setSoLuong(current - 1);
                    if (phieu.getSoLuong() <= 0) {
                        phieu.setTrangThai(0); // 0 = hết lượt
                    }
                    phieuGiamGiaRepository.save(phieu);
                }
            }
        }

        // 🔥 đảm bảo trạng thái đơn + thanh toán
        order.setTrangThaiThanhToan(PAYMENT_STATUS_PAID);
        order.setTrangThai(ORDER_STATUS_COMPLETED);
        orderRepository.save(order);

        return getDetail(orderId);
    }

    // ===== HUỶ ĐƠN (trả seri về ACTIVE, không trừ voucher) =====
    @Override
    @Transactional
    public PosOrderDetailDTO cancel(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        List<OrderCT> items = orderCTRepository.findByIdOrder_Id(orderId);
        for (OrderCT ct : items) {
            if (ct.getIdSeri() != null) {
                Seri seri = ct.getIdSeri();
                seri.setTrangThai(SERI_ACTIVE);
                seriRepository.save(seri);
            }
        }

        order.setTrangThai(ORDER_STATUS_CANCELLED);
        order.setTrangThaiThanhToan(PAYMENT_STATUS_UNPAID); // 🔥 huỷ đơn ⇒ chưa thanh toán
        orderRepository.save(order);

        return getDetail(orderId);
    }

    // ===== LẤY CHI TIẾT ĐƠN =====
    @Override
    @Transactional(readOnly = true)
    public PosOrderDetailDTO getDetail(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        return posOrderMapper.toPosOrderDetail(order);
    }

    @Override
    @Transactional
    public void addSeriToOrder(UUID orderId, PosAddItemsRequest request) {
        addItems(orderId, request);
    }

    // ===== HELPER =====
    private Order getOrderOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Order: " + id));
    }

    private void requireDraft(Order order) {
        if (!Objects.equals(order.getTrangThai(), ORDER_STATUS_DRAFT)) {
            throw new IllegalStateException("Đơn hàng đã hoàn thành/huỷ, không thể chỉnh sửa");
        }
    }

    private void recalcOrderTotals(Order order) {
        List<OrderCT> items = orderCTRepository.findByIdOrder_Id(order.getId());
        BigDecimal subtotal = items.stream()
                .map(OrderCT::getGiaBan)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<GiamGiaHoaDon> discounts =
                giamGiaHoaDonRepository.findByIdOrders_Id(order.getId());
        BigDecimal totalDiscount = discounts.stream()
                .map(d -> {
                    BigDecimal truoc = d.getSoTienTruocGiam();
                    BigDecimal sau = d.getSoTienSauGiam();
                    if (truoc == null || sau == null) return BigDecimal.ZERO;
                    return truoc.subtract(sau);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setGiaTriChuaGiam(subtotal);
        order.setGiaTriGiamGia(totalDiscount);

        BigDecimal phiKhac = nvl(order.getPhiDichVuKhac());

        BigDecimal mustPay = subtotal
                .subtract(totalDiscount)
                .add(phiKhac);

        if (mustPay.compareTo(BigDecimal.ZERO) < 0) {
            mustPay = BigDecimal.ZERO;
        }

        order.setTongTienThuHo(mustPay);
        orderRepository.save(order);
    }

    private BigDecimal nvl(BigDecimal b) {
        return b == null ? BigDecimal.ZERO : b;
    }

    private String generateMaDonHang() {
        // Phần ngày: yyyyMMdd
        String datePart = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE); // 20251207

        // Prefix mã đơn trong ngày
        String prefix = "OD" + datePart + "-";            // OD20251207-

        // Lấy mã cuối cùng trong ngày đó từ DB
        String lastCode = orderRepository.findLastMaDonHangByPrefix(prefix);

        int next = 0; // bắt đầu từ 0000
        if (lastCode != null && lastCode.startsWith(prefix)) {
            String numberStr = lastCode.substring(prefix.length()); // lấy phần 0000 phía sau
            try {
                next = Integer.parseInt(numberStr) + 1;
            } catch (NumberFormatException ignored) {
                next = 0;
            }
        }

        // Giới hạn từ 0 -> 1000
        if (next > 1000) {
            // tuỳ em: có thể throw exception nếu muốn chặn tạo quá 1000 đơn/ngày
            // throw new IllegalStateException("Vượt quá số lượng đơn cho phép trong ngày");
            next = 0; // hoặc quay lại từ 0000
        }

        // %04d => chuẩn hoá 4 chữ số: 0 -> 0000, 15 -> 0015, 1000 -> 1000
        return String.format("%s%04d", prefix, next);
    }

    // 🔥 lấy ID tài khoản đang đăng nhập (nhân viên / admin)
    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();

        if (principal instanceof TaiKhoan tk) {
            return tk.getId();
        }
        // Nếu bạn dùng CustomUserDetails riêng thì sửa lại đoạn này
        return null;
    }
}
