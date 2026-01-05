package com.example.sever.service.impl;

import com.example.sever.dto.OrderDTO.OrderRespone;
import com.example.sever.dto.Pos.*;
import com.example.sever.dto.Pos.GHN.PosUpdateShippingRequest;
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
import com.example.sever.dto.Pos.GHN.GhnFeeRequest;
import com.example.sever.service.GhnClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
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
    private final DiaChiRepository diaChiRepository;
    private final GhnClientService ghnClientService;
    private final PhieuGiamGiaService phieuGiamGiaService;
    private final PosOrderMapper posOrderMapper;
    private final AnhRepository anhRepository;

    // ✅ ADD: action log repo
    private final OrderActionLogRepository orderActionLogRepository;

    private static final int ORDER_STATUS_DRAFT = 1;
    private static final int ORDER_STATUS_COMPLETED = 2; // POS hoàn tất
    private static final int ORDER_STATUS_CANCELLED = 3; // POS huỷ

    // ✅ ADD: Online/Delivery statuses (để log/luồng giao hàng)
    private static final int ORDER_STATUS_CONFIRMED = 2;
    private static final int ORDER_STATUS_PREPARING = 3;
    private static final int ORDER_STATUS_ONLINE_CANCELLED = 7;

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
            DiaChi dc = diaChiRepository.findById(request.getIdDiaChi())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Không tìm thấy địa chỉ: " + request.getIdDiaChi()
                    ));
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

        // ✅ ADD: ActionLog theo loại đơn
        if ("GIAO_HANG".equalsIgnoreCase(order.getLoaiDon())) {
            // giao hàng: coi như "đã xác nhận" sẵn (chỉ timeline, không đổi trạng thái draft)
            writeLog(order, ORDER_STATUS_CONFIRMED,
                    "Chuyển sang giao hàng (đã xác nhận): " + order.getMaDonHang());
        } else {
            // tại quầy: chỉ cần tạo đơn
            writeLog(order, ORDER_STATUS_DRAFT,
                    "Tạo đơn tại quầy: " + order.getMaDonHang());
        }

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

        // ✅ chỉ lấy OrderCT thuộc đúng đơn
        OrderCT ct = orderCTRepository.findByIdAndIdOrder_Id(orderCtId, orderId)
                .orElse(null);

        // ✅ idempotent: nếu không tồn tại -> coi như đã xoá rồi
        if (ct == null) {
            return getDetail(orderId);
        }

        // trả seri về ACTIVE
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

        // set tài khoản
        UUID tkId = request.getIdTaiKhoan();
        if (tkId != null) {
            TaiKhoan tk = new TaiKhoan();
            tk.setId(tkId);
            order.setIdTaiKhoan(tk);

            // ✅ Nếu FE không truyền idDiaChi -> tự lấy địa chỉ mặc định
            DiaChi picked = null;

            if (request.getIdDiaChi() != null) {
                picked = diaChiRepository.findById(request.getIdDiaChi()).orElse(null);
            } else {
                picked = diaChiRepository.findByIdTaiKhoan_IdAndMacDinhTrue(tkId)
                        .orElseGet(() -> {
                            List<DiaChi> list = diaChiRepository.findByIdTaiKhoan_Id(tkId);
                            return (list != null && !list.isEmpty()) ? list.get(0) : null;
                        });
            }

            if (picked != null) {
                order.setIdDiaChi(picked);

                // ✅ snapshot người nhận theo địa chỉ nếu FE không gửi
                if (request.getTenKhachHang() == null || request.getTenKhachHang().isBlank()) {
                    order.setTenKhachHang(picked.getHoTen());
                } else {
                    order.setTenKhachHang(request.getTenKhachHang());
                }

                if (request.getSdtKhachHang() == null || request.getSdtKhachHang().isBlank()) {
                    order.setSdtKhachHang(picked.getSoDienThoai());
                } else {
                    order.setSdtKhachHang(request.getSdtKhachHang());
                }
            } else {
                // không có địa chỉ
                order.setIdDiaChi(null);
                order.setTenKhachHang(request.getTenKhachHang());
                order.setSdtKhachHang(request.getSdtKhachHang());
            }
        } else {
            // bỏ chọn khách
            order.setIdTaiKhoan(null);
            order.setIdDiaChi(null);
            order.setTenKhachHang(request.getTenKhachHang());
            order.setSdtKhachHang(request.getSdtKhachHang());
        }

        orderRepository.save(order);
        return getDetail(orderId);
    }

    private String generateDiaChiCode() {
        Integer max = diaChiRepository.findMaxDiaChiCode();
        int next = (max == null) ? 1 : (max + 1);
        return String.format("DC%04d", next);
    }

    @Override
    @Transactional
    public PosOrderDetailDTO updateShipping(UUID orderId, PosUpdateShippingRequest req) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        boolean giaoHang = Boolean.TRUE.equals(req.getGiaoHang());

        // ✅ ADD: detect switch TAI_QUAY -> GIAO_HANG (log 1 lần)
        boolean wasGiaoHang = "GIAO_HANG".equalsIgnoreCase(order.getLoaiDon());

        // set loại đơn
        order.setLoaiDon(giaoHang ? "GIAO_HANG" : "TAI_QUAY");

        // ✅ ADD: log khi vừa bật giao hàng
        if (giaoHang && !wasGiaoHang) {
            writeLog(order, ORDER_STATUS_CONFIRMED,
                    "Chuyển sang giao hàng (đã xác nhận): " + order.getMaDonHang());
        }

        // Nếu tắt giao hàng -> reset phí
        if (!giaoHang) {
            order.setPhiVanChuyen(BigDecimal.ZERO);

            // ✅ FIX: tắt giao hàng thì bỏ địa chỉ gắn trên đơn (tránh giữ thông tin giao hàng cũ)
            order.setIdDiaChi(null);

            recalcOrderTotals(order); // ✅ cập nhật tongTienThuHo ngay
            return getDetail(orderId);
        }

        UUID tkId = (order.getIdTaiKhoan() != null ? order.getIdTaiKhoan().getId() : null);

        // ==============================
        // CASE 1: KHÁCH LẺ (không tài khoản) -> KHÔNG LƯU DiaChi, chỉ tính phí GHN
        // ==============================
        if (tkId == null) {
            DiaChi dc = null;

            if (req.getIdDiaChi() != null) {
                dc = diaChiRepository.findById(req.getIdDiaChi())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ: " + req.getIdDiaChi()));
                order.setIdDiaChi(dc);
            }

            Integer toDistrictId = (req.getDistrictId() != null ? req.getDistrictId()
                    : (dc != null ? dc.getDistrictId() : null));
            String toWardCode = (req.getWardCode() != null && !req.getWardCode().isBlank() ? req.getWardCode()
                    : (dc != null ? dc.getWardCode() : null));

            if (toDistrictId == null || toWardCode == null || toWardCode.isBlank()) {
                order.setPhiVanChuyen(BigDecimal.ZERO);
                recalcOrderTotals(order);
                return getDetail(orderId);
            }

            if (req.getHoTen() != null && !req.getHoTen().isBlank()) order.setTenKhachHang(req.getHoTen());
            if (req.getSoDienThoai() != null && !req.getSoDienThoai().isBlank()) order.setSdtKhachHang(req.getSoDienThoai());

            GhnFeeRequest feeReq = new GhnFeeRequest();
            feeReq.setToDistrictId(toDistrictId);
            feeReq.setToWardCode(toWardCode);

            int fee = ghnClientService.calcFee(feeReq);
            order.setPhiVanChuyen(BigDecimal.valueOf(fee));
            recalcOrderTotals(order); // ✅ cập nhật tongTienThuHo ngay
            return getDetail(orderId);
        }

        // ==============================
        // CASE 2: KHÁCH CÓ TÀI KHOẢN
        // ==============================
        DiaChi diaChiToUse = null;

        // 2.1) Nếu FE chọn địa chỉ có sẵn
        if (req.getIdDiaChi() != null) {
            diaChiToUse = diaChiRepository.findById(req.getIdDiaChi()).orElse(null);
            if (diaChiToUse == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ: " + req.getIdDiaChi());
            }

            // ✅ FIX: validate địa chỉ phải thuộc đúng tài khoản
            if (diaChiToUse.getIdTaiKhoan() == null || diaChiToUse.getIdTaiKhoan().getId() == null
                    || !tkId.equals(diaChiToUse.getIdTaiKhoan().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Địa chỉ không thuộc tài khoản khách hàng.");
            }

            order.setIdDiaChi(diaChiToUse);
        } else {
            // 2.2) FE không chọn idDiaChi
            boolean saveAddress = Boolean.TRUE.equals(req.getSaveAddress());

            if (saveAddress) {
                // ✅ Chỉ tạo mới khi user thật sự muốn lưu địa chỉ mới
                DiaChi newDc = new DiaChi();
                newDc.setId(UUID.randomUUID());

                TaiKhoan tkRef = new TaiKhoan();
                tkRef.setId(tkId);
                newDc.setIdTaiKhoan(tkRef);

                newDc.setIdDiaChi(generateDiaChiCode());

                newDc.setHoTen(req.getHoTen());
                newDc.setSoDienThoai(req.getSoDienThoai());

                newDc.setQuocGia(req.getQuocGia());
                newDc.setTinhThanh(req.getTinhThanh());
                newDc.setQuanHuyen(req.getQuanHuyen());
                newDc.setPhuongXa(req.getPhuongXa());
                newDc.setDiaChiChiTiet(req.getDiaChiChiTiet());

                newDc.setProvinceId(req.getProvinceId());
                newDc.setDistrictId(req.getDistrictId());
                newDc.setWardCode(req.getWardCode());

                boolean setAsDefault = Boolean.TRUE.equals(req.getSetAsDefault());
                newDc.setMacDinh(setAsDefault);

                if (setAsDefault) {
                    diaChiRepository.clearDefault(tkId);
                }

                diaChiToUse = diaChiRepository.save(newDc);
                order.setIdDiaChi(diaChiToUse);
            } else {
                // ✅ ĐÚNG NGHIỆP VỤ: KHÔNG LƯU, LẤY ĐỊA CHỈ MẶC ĐỊNH
                diaChiToUse = diaChiRepository.findByIdTaiKhoan_IdAndMacDinhTrue(tkId)
                        .orElseGet(() -> {
                            List<DiaChi> list = diaChiRepository.findByIdTaiKhoan_Id(tkId);
                            return (list != null && !list.isEmpty()) ? list.get(0) : null;
                        });

                order.setIdDiaChi(diaChiToUse);
            }
        }

        // ==============================
        // Snapshot tên/sdt (ưu tiên req nếu user đang nhập)
        // ==============================
        if (req.getHoTen() != null && !req.getHoTen().isBlank()) {
            order.setTenKhachHang(req.getHoTen());
        } else if (diaChiToUse != null && diaChiToUse.getHoTen() != null) {
            order.setTenKhachHang(diaChiToUse.getHoTen());
        }

        if (req.getSoDienThoai() != null && !req.getSoDienThoai().isBlank()) {
            order.setSdtKhachHang(req.getSoDienThoai());
        } else if (diaChiToUse != null && diaChiToUse.getSoDienThoai() != null) {
            order.setSdtKhachHang(diaChiToUse.getSoDienThoai());
        }

        // ==============================
        // TÍNH PHÍ GHN:
        // - Nếu user nhập district/ward (đổi địa chỉ tạm) => ưu tiên req để tính phí
        // - Nếu không có => lấy từ diaChiToUse
        // ==============================
        Integer toDistrictId = (req.getDistrictId() != null ? req.getDistrictId()
                : (diaChiToUse != null ? diaChiToUse.getDistrictId() : null));
        String toWardCode = (req.getWardCode() != null && !req.getWardCode().isBlank() ? req.getWardCode()
                : (diaChiToUse != null ? diaChiToUse.getWardCode() : null));

        // ✅ FIX: thiếu district/ward => KHÔNG THROW, chỉ set phí ship = 0 và return
        if (toDistrictId == null || toWardCode == null || toWardCode.isBlank()) {
            order.setPhiVanChuyen(BigDecimal.ZERO);
            recalcOrderTotals(order);
            return getDetail(orderId);
        }

        GhnFeeRequest feeReq = new GhnFeeRequest();
        feeReq.setToDistrictId(toDistrictId);
        feeReq.setToWardCode(toWardCode);

        int fee = ghnClientService.calcFee(feeReq);
        order.setPhiVanChuyen(BigDecimal.valueOf(fee));
        recalcOrderTotals(order); // ✅ cập nhật tongTienThuHo ngay
        return getDetail(orderId);
    }

    // ===== ÁP VOUCHER CHO ĐƠN DRAFT =====
    @Override
    @Transactional
    public OrderRespone applyVoucher(UUID orderId, PosApplyVoucherRequest request) {
        if (request == null || request.getVoucherId() == null || request.getVoucherId().isBlank()) {
            throw new IllegalArgumentException("Thiếu mã phiếu giảm giá");
        }

        String voucherCode = request.getVoucherId().trim();

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

        BigDecimal phiKhac = nvl(order.getPhiDichVuKhac());
        BigDecimal phiShip = nvl(order.getPhiVanChuyen());

        BigDecimal mustPay = tongTien
                .subtract(soTienGiam)
                .add(phiKhac)
                .add(phiShip);

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
        BigDecimal sauGiamHang = tongTien.subtract(soTienGiam);
        if (sauGiamHang.compareTo(BigDecimal.ZERO) < 0) sauGiamHang = BigDecimal.ZERO;

        g.setSoTienTruocGiam(tongTien);
        g.setSoTienSauGiam(sauGiamHang);

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

    @Transactional
    public void clearVoucher(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        // xoá giảm giá hoá đơn nếu có
        List<GiamGiaHoaDon> olds = giamGiaHoaDonRepository.findByIdOrders_Id(orderId);
        if (!olds.isEmpty()) giamGiaHoaDonRepository.deleteAll(olds);

        // tính lại tổng
        BigDecimal tongTien = nvl(orderCTRepository.sumGiaBanByOrderId(order.getId()));
        order.setGiaTriChuaGiam(tongTien);
        order.setGiaTriGiamGia(BigDecimal.ZERO);

        BigDecimal phiKhac = nvl(order.getPhiDichVuKhac());
        BigDecimal phiShip = nvl(order.getPhiVanChuyen());

        BigDecimal mustPay = tongTien
                .add(phiKhac)
                .add(phiShip);

        order.setTongTienThuHo(mustPay);
        orderRepository.save(order);
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

        for (OrderCT ct : items) {
            if (ct.getIdSeri() != null) {
                Seri seri = ct.getIdSeri();
                seri.setTrangThai(SERI_SOLD);
                seriRepository.save(seri);
            }
        }

        List<GiamGiaHoaDon> discounts =
                giamGiaHoaDonRepository.findByIdOrders_Id(orderId);
        for (GiamGiaHoaDon d : discounts) {
            PhieuGiamGia phieu = d.getIdPhieuGiamGia();
            if (phieu != null && phieu.getSoLuong() != null) {
                int current = phieu.getSoLuong();
                if (current > 0) {
                    phieu.setSoLuong(current - 1);
                    if (phieu.getSoLuong() <= 0) {
                        phieu.setTrangThai(0);
                    }
                    phieuGiamGiaRepository.save(phieu);
                }
            }
        }

        order.setTrangThaiThanhToan(PAYMENT_STATUS_PAID);

        // ✅ FIX: phân nhánh theo loại đơn + ghi log
        if ("GIAO_HANG".equalsIgnoreCase(order.getLoaiDon())) {
            // giao hàng: sau khi chốt tại quầy, bắt đầu bước tiếp theo (3)
            order.setTrangThai(ORDER_STATUS_PREPARING);
            orderRepository.save(order);
            writeLog(order, ORDER_STATUS_PREPARING, "Đang chuẩn bị hàng: " + order.getMaDonHang());
        } else {
            // tại quầy: hoàn tất = 2
            order.setTrangThai(ORDER_STATUS_COMPLETED);
            orderRepository.save(order);
            writeLog(order, ORDER_STATUS_COMPLETED, "Hoàn tất đơn tại quầy: " + order.getMaDonHang());
        }

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

        order.setTrangThaiThanhToan(PAYMENT_STATUS_UNPAID);

        // ✅ FIX: phân nhánh theo loại đơn + ghi log
        if ("GIAO_HANG".equalsIgnoreCase(order.getLoaiDon())) {
            order.setTrangThai(ORDER_STATUS_ONLINE_CANCELLED); // 7
            orderRepository.save(order);
            writeLog(order, ORDER_STATUS_ONLINE_CANCELLED, "Hủy đơn giao hàng: " + order.getMaDonHang());
        } else {
            order.setTrangThai(ORDER_STATUS_CANCELLED); // 3
            orderRepository.save(order);
            writeLog(order, ORDER_STATUS_CANCELLED, "Hủy đơn tại quầy: " + order.getMaDonHang());
        }

        return getDetail(orderId);
    }

    // ===== LẤY CHI TIẾT ĐƠN =====
    @Override
    @Transactional(readOnly = true)
    public PosOrderDetailDTO getDetail(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        PosOrderDetailDTO dto = posOrderMapper.toPosOrderDetail(order);

        if (dto.getItems() != null) {
            for (PosOrderItemDTO it : dto.getItems()) {
                UUID laptopCtId = it.getLaptopCtId();
                if (laptopCtId == null) continue;

                List<String> urls = anhRepository.findAllImgUrlByLaptopChiTietId(laptopCtId);
                if (urls != null && !urls.isEmpty()) {
                    it.setAnhUrl(urls.get(0));
                } else {
                    it.setAnhUrl(null);
                }
            }
        }

        return dto;
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
        BigDecimal phiShip = nvl(order.getPhiVanChuyen());

        BigDecimal mustPay = subtotal
                .subtract(totalDiscount)
                .add(phiKhac)
                .add(phiShip);

        if (mustPay.compareTo(BigDecimal.ZERO) < 0) {
            mustPay = BigDecimal.ZERO;
        }

        order.setTongTienThuHo(mustPay);
        orderRepository.save(order);
    }

    private BigDecimal nvl(BigDecimal b) {
        return b == null ? BigDecimal.ZERO : b;
    }

    @Override
    @Transactional
    public PosOrderDetailDTO addItemsBySeriCode(UUID orderId, List<String> seriCodes) {
        Order order = getOrderOrThrow(orderId);
        requireDraft(order);

        if (seriCodes == null || seriCodes.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mã seri trống");
        }

        List<UUID> seriUuids = new ArrayList<>();

        for (String code : seriCodes) {
            String trimmed = code.trim().toUpperCase();
            if (trimmed.isEmpty()) continue;

            Seri seri = seriRepository.findByIdSeriAndTrangThai(trimmed, SERI_ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Seri '" + trimmed + "' không tồn tại hoặc đã bán"));

            boolean alreadyInOrder = orderCTRepository.existsByIdOrder_IdAndIdSeri_Id(orderId, seri.getId());
            if (alreadyInOrder) {
                throw new RuntimeException("Seri '" + trimmed + "' đã có trong đơn hàng");
            }

            seriUuids.add(seri.getId());
        }

        PosAddItemsRequest request = new PosAddItemsRequest();
        request.setSeriIds(seriUuids);
        return addItems(orderId, request);
    }

    private String generateMaDonHang() {
        String datePart = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE);

        String prefix = "OD" + datePart + "-";

        String lastCode = orderRepository.findLastMaDonHangByPrefix(prefix);

        int next = 0;
        if (lastCode != null && lastCode.startsWith(prefix)) {
            String numberStr = lastCode.substring(prefix.length());
            try {
                next = Integer.parseInt(numberStr) + 1;
            } catch (NumberFormatException ignored) {
                next = 0;
            }
        }

        if (next > 1000) {
            next = 0;
        }

        return String.format("%s%04d", prefix, next);
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();

        if (principal instanceof TaiKhoan tk) {
            return tk.getId();
        }
        return null;
    }

    // ✅ ADD: helper ghi OrderActionLog
    private void writeLog(Order order, int hanhDong, String moTa) {
        OrderActionLog log = new OrderActionLog();
        log.setId(UUID.randomUUID());
        log.setIdOrderacl("ACL" + System.currentTimeMillis()); // <= 20 ký tự
        log.setIdOrder(order);
        log.setNgayTao(Instant.now());
        log.setHanhDong(hanhDong);
        log.setMoTa(moTa);

        UUID userId = getCurrentUserId();
        if (userId != null) {
            TaiKhoan tk = new TaiKhoan();
            tk.setId(userId);
            log.setIdTaiKhoan(tk);
        }

        orderActionLogRepository.save(log);
    }
}
