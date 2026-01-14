package com.example.sever.service.impl;

import com.example.sever.dto.OrderDTO.OrderRespone;
import com.example.sever.dto.Pos.*;
import com.example.sever.dto.Pos.GHN.PosUpdateShippingRequest;
import com.example.sever.entity.*;
import com.example.sever.mapper.PosOrderMapper;
import com.example.sever.repository.*;
import com.example.sever.service.PhieuGiamGiaService;
import com.example.sever.service.PosOrderService;
import com.example.sever.statusauto.OrderStatus;
import com.example.sever.statusauto.OrderType;
import com.example.sever.statusauto.PaymentStatus;
import com.example.sever.statusauto.SeriStatus;
import com.example.sever.utils.GhnFeeRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
import java.math.RoundingMode; // ✅ FIX: add import
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

    // ===== ENUM CODES (đồng bộ theo enum bạn gửi) =====
    private static final int SERI_ACTIVE  = SeriStatus.ACTIVE.code();
    private static final int SERI_PENDING = SeriStatus.PENDING.code();
    private static final int SERI_SOLD    = SeriStatus.SOLD.code();

    private static final int PAYMENT_STATUS_UNPAID = PaymentStatus.UNPAID.code();
    private static final int PAYMENT_STATUS_PAID   = PaymentStatus.PAID.code();

    private static final int ORDER_STATUS_DRAFT           = OrderStatus.DRAFT.code();            // 0
    private static final int ORDER_STATUS_PENDING_CONFIRM = OrderStatus.PENDING_CONFIRM.code();  // 1 (nếu bạn dùng)
    private static final int ORDER_STATUS_CONFIRMED       = OrderStatus.CONFIRMED.code();        // 2
    private static final int ORDER_STATUS_PREPARING       = OrderStatus.PREPARING.code();        // 3
    private static final int ORDER_STATUS_SHIPPING        = OrderStatus.SHIPPING.code();         // 4 (nếu bạn dùng)
    private static final int ORDER_STATUS_DELIVERED       = OrderStatus.DELIVERED.code();        // 5 (nếu bạn dùng)
    private static final int ORDER_STATUS_COMPLETED       = OrderStatus.COMPLETED.code();        // 6
    private static final int ORDER_STATUS_CANCELLED       = OrderStatus.CANCELED.code();         // 7

    // ===== helpers =====
    private boolean isDelivery(Order order) {
        return OrderType.DELIVERY.dbValue().equalsIgnoreCase(order.getLoaiDon());
    }

    // ===== TẠO ĐƠN NHÁP =====
    @Override
    @Transactional
    public PosOrderDetailDTO createDraftOrder(PosCreateOrderRequest request) {
        Order order = new Order();

        order.setId(UUID.randomUUID());
        order.setIdOrder("ORD" + System.currentTimeMillis());
        order.setMaDonHang(generateMaDonHang());

        // ✅ default theo enum OrderType
        order.setLoaiDon(request.getLoaiDon() != null ? request.getLoaiDon() : OrderType.POS.dbValue());

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

        // ✅ chống trùng mã đơn nếu concurrent (cần unique constraint ở DB để phát huy)
        saveOrderWithMaRetry(order, 3);

        // ✅ ADD: ActionLog theo loại đơn
        writeLog(order, ORDER_STATUS_DRAFT, "Tạo đơn nháp: " + order.getMaDonHang());

        return posOrderMapper.toPosOrderDetail(order);
    }

    // ===== THÊM SERI VÀO ĐƠN (ACTIVE -> PENDING) =====
    @Override
    @Transactional
    public PosOrderDetailDTO addItems(UUID orderId, PosAddItemsRequest request) {
        Order order = getOrderOrThrow(orderId);
        requireDraftOrPendingConfirm(order);

        if (request == null || request.getSeriIds() == null || request.getSeriIds().isEmpty()) {
            throw new IllegalArgumentException("Danh sách seri không được rỗng");
        }

        Set<UUID> uniqueSeriIds = new LinkedHashSet<>(request.getSeriIds());
        List<OrderCT> toSave = new ArrayList<>();

        for (UUID seriId : uniqueSeriIds) {
            Seri seri = seriRepository.findById(seriId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Seri: " + seriId));

            // Kiểm tra xem seri đã có trong đơn chưa
            boolean alreadyInOrder = orderCTRepository.existsByIdOrder_IdAndIdSeri_Id(orderId, seriId);
            if (alreadyInOrder) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "SERI_ALREADY_IN_CART: Seri '" + seri.getIdSeri() + "' đã có trong giỏ"
                );
            }

            // validate giá
            if (seri.getIdLapTopCt() == null || seri.getIdLapTopCt().getGiaBan() == null) {
                throw new IllegalStateException("Không có giá bán cho seri " + seri.getIdSeri());
            }

            // 🔥 giữ hàng an toàn: ACTIVE -> PENDING (atomic)
            int updated = seriRepository.updateTrangThaiSeriIfCurrent(seriId, SERI_ACTIVE, SERI_PENDING);
            if (updated != 1) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "SERI_NOT_AVAILABLE: Sản phẩm không có sẵn (seri '" + seri.getIdSeri() + "')"
                );
            }
            // giữ đồng bộ trong persistence context (tránh stale)
            seri.setTrangThai(SERI_PENDING);

            OrderCT line = new OrderCT();
            line.setId(UUID.randomUUID());
            line.setIdOrder(order);
            line.setIdSeri(seri);
            line.setGiaBan(seri.getIdLapTopCt().getGiaBan());
            toSave.add(line);
        }

        orderCTRepository.saveAll(toSave);

        recalcOrderTotals(order);
        return getDetail(orderId);
    }

    // ===== XÓA 1 DÒNG SERI (trả về ACTIVE) =====
    @Override
    @Transactional
    public PosOrderDetailDTO removeItem(UUID orderId, UUID orderCtId) {
        Order order = getOrderOrThrow(orderId);
        requireDraftOrPendingConfirm(order);

        // ✅ chỉ lấy OrderCT thuộc đúng đơn
        OrderCT ct = orderCTRepository.findByIdAndIdOrder_Id(orderCtId, orderId)
                .orElse(null);

        // ✅ idempotent: nếu không tồn tại -> coi như đã xoá rồi
        if (ct == null) {
            return getDetail(orderId);
        }

        // trả seri về ACTIVE an toàn: PENDING -> ACTIVE
        if (ct.getIdSeri() != null && ct.getIdSeri().getId() != null) {
            UUID seriId = ct.getIdSeri().getId();
            seriRepository.updateTrangThaiSeriIfCurrent(seriId, SERI_PENDING, SERI_ACTIVE);
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
        requireDraftOrPendingConfirm(order);

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

                // ✅ bảo mật: địa chỉ phải thuộc đúng tài khoản khách hàng
                if (picked != null) {
                    if (picked.getIdTaiKhoan() == null || picked.getIdTaiKhoan().getId() == null
                            || !tkId.equals(picked.getIdTaiKhoan().getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Địa chỉ không thuộc tài khoản khách hàng.");
                    }
                }
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
                order.setIdDiaChi(null);

                if (request.getTenKhachHang() != null && !request.getTenKhachHang().isBlank()) {
                    order.setTenKhachHang(request.getTenKhachHang());
                }
                if (request.getSdtKhachHang() != null && !request.getSdtKhachHang().isBlank()) {
                    order.setSdtKhachHang(request.getSdtKhachHang());
                }
            }
        }  else {
        order.setIdTaiKhoan(null);
        order.setIdDiaChi(null);

        // ✅ nếu FE gửi "" => clear thật
        boolean clearName = request.getTenKhachHang() != null && request.getTenKhachHang().isBlank();
        boolean clearPhone = request.getSdtKhachHang() != null && request.getSdtKhachHang().isBlank();
        boolean isClearCustomer = clearName && clearPhone;

        if (request.getTenKhachHang() != null) {
            String ten = request.getTenKhachHang().trim();
            order.setTenKhachHang(ten.isEmpty() ? null : ten);
        }

        if (request.getSdtKhachHang() != null) {
            String sdt = request.getSdtKhachHang().trim();
            order.setSdtKhachHang(sdt.isEmpty() ? null : sdt);
        }

        // ✅ nếu đang "bỏ khách" thì reset luôn ship (tránh còn ghiChu/phi ship cũ)
        if (isClearCustomer) {
            order.setPhiVanChuyen(BigDecimal.ZERO);
            order.setGhiChu(removeShipNote(order.getGhiChu()));
            recalcOrderTotals(order);
        }
    }

        orderRepository.save(order);
        return getDetail(orderId);
    }

    private String generateDiaChiCode() {
        Integer maxNum = diaChiRepository.findMaxDiaChiNumberWithLock(); // ✅ UPDLOCK + HOLDLOCK
        int next = (maxNum == null ? 1 : maxNum + 1);
        return String.format("DC%04d", next); // DC0001, DC0002...
    }

    @Override
    @Transactional
    public PosOrderDetailDTO updateShipping(UUID orderId, PosUpdateShippingRequest req) {
        Order order = getOrderOrThrow(orderId);
        requireDraftOrPendingConfirm(order);

        boolean giaoHang = Boolean.TRUE.equals(req.getGiaoHang());

        // ✅ detect switch POS -> DELIVERY (log 1 lần)
        boolean wasDelivery = isDelivery(order);

        // ✅ set loại đơn theo enum OrderType
        order.setLoaiDon(giaoHang ? OrderType.DELIVERY.dbValue() : OrderType.POS.dbValue());

        // ✅ Switch POS -> DELIVERY (log + set status PENDING_CONFIRM)
        if (giaoHang && !wasDelivery) {
            // ✅ giữ trạng thái ĐƠN NHÁP
            order.setTrangThai(ORDER_STATUS_DRAFT); // 0
            orderRepository.save(order);
            writeLog(order, ORDER_STATUS_DRAFT, "Chuyển sang giao hàng: " + order.getMaDonHang());
        }

        // ✅ Switch DELIVERY -> POS (optional nhưng nên có để timeline/logic nhất quán)
        if (!giaoHang && wasDelivery) {
            order.setTrangThai(ORDER_STATUS_DRAFT); // 0
            orderRepository.save(order);
            writeLog(order, ORDER_STATUS_DRAFT, "Chuyển về đơn tại quầy: " + order.getMaDonHang());
        }

        // Nếu tắt giao hàng -> reset phí
        if (!giaoHang) {
            order.setPhiVanChuyen(BigDecimal.ZERO);

            // ✅ tắt giao hàng thì bỏ địa chỉ gắn trên đơn
            order.setIdDiaChi(null);
            order.setGhiChu(removeShipNote(order.getGhiChu()));
            recalcOrderTotals(order);
            return getDetail(orderId);
        }

        UUID tkId = (order.getIdTaiKhoan() != null ? order.getIdTaiKhoan().getId() : null);

        // ==============================
        // CASE 1: KHÁCH LẺ (không tài khoản)
        // ==============================
        if (tkId == null) {
            // ✅ bảo mật: khách lẻ không được trỏ tới địa chỉ đã lưu trong DB
            if (req.getIdDiaChi() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Khách lẻ không được chọn địa chỉ đã lưu. Vui lòng gửi đầy đủ districtId/wardCode và địa chỉ chi tiết.");
            }

            Integer toDistrictId = req.getDistrictId();
            String toWardCode = (req.getWardCode() != null ? req.getWardCode().trim() : null);

            if (toDistrictId == null || toWardCode == null || toWardCode.isBlank()) {
                order.setPhiVanChuyen(BigDecimal.ZERO);
                recalcOrderTotals(order);
                return getDetail(orderId);
            }

            if (req.getHoTen() != null && !req.getHoTen().isBlank()) order.setTenKhachHang(req.getHoTen());
            if (req.getSoDienThoai() != null && !req.getSoDienThoai().isBlank()) order.setSdtKhachHang(req.getSoDienThoai());
            order.setGhiChu(upsertShipNote(order.getGhiChu(), req));
            boolean useInsurance = Boolean.TRUE.equals(req.getUseInsurance());
            BigDecimal subtotal = nvl(orderCTRepository.sumGiaBanByOrderId(order.getId()));
            List<OrderCT> itemsNow = orderCTRepository.findByIdOrder_Id(order.getId());
            if (itemsNow == null || itemsNow.isEmpty()) {
                order.setPhiVanChuyen(BigDecimal.ZERO);
                recalcOrderTotals(order);
                return getDetail(orderId);
            }
            int fee;
            if (isFreeShip(subtotal)) {
                fee = 0;
            } else {
                GhnFeeRequest feeReq = buildGhnFeeRequest(order, toDistrictId, toWardCode, useInsurance);
                fee = ghnClientService.calcFee(feeReq);
            }

            order.setPhiVanChuyen(BigDecimal.valueOf(fee));
            recalcOrderTotals(order);
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

            // validate địa chỉ phải thuộc đúng tài khoản
            if (diaChiToUse.getIdTaiKhoan() == null || diaChiToUse.getIdTaiKhoan().getId() == null
                    || !tkId.equals(diaChiToUse.getIdTaiKhoan().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Địa chỉ không thuộc tài khoản khách hàng.");
            }

            order.setIdDiaChi(diaChiToUse);
        } else {
            // 2.2) FE không chọn idDiaChi
            boolean saveAddress = Boolean.TRUE.equals(req.getSaveAddress());

            if (saveAddress) {
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

                diaChiToUse = saveDiaChiWithCodeRetry(newDc, 3);
                order.setIdDiaChi(diaChiToUse);
            } else {
                diaChiToUse = diaChiRepository.findByIdTaiKhoan_IdAndMacDinhTrue(tkId)
                        .orElseGet(() -> {
                            List<DiaChi> list = diaChiRepository.findByIdTaiKhoan_Id(tkId);
                            return (list != null && !list.isEmpty()) ? list.get(0) : null;
                        });

                order.setIdDiaChi(diaChiToUse);
            }
        }

        // Snapshot tên/sdt
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

        // TÍNH PHÍ GHN
        Integer toDistrictId = (req.getDistrictId() != null ? req.getDistrictId()
                : (diaChiToUse != null ? diaChiToUse.getDistrictId() : null));
        String toWardCode = (req.getWardCode() != null && !req.getWardCode().isBlank() ? req.getWardCode()
                : (diaChiToUse != null ? diaChiToUse.getWardCode() : null));

        if (toDistrictId == null || toWardCode == null || toWardCode.isBlank()) {
            order.setPhiVanChuyen(BigDecimal.ZERO);
            recalcOrderTotals(order);
            return getDetail(orderId);
        }

        boolean useInsurance = Boolean.TRUE.equals(req.getUseInsurance());
        BigDecimal subtotal = nvl(orderCTRepository.sumGiaBanByOrderId(order.getId()));
        List<OrderCT> itemsNow = orderCTRepository.findByIdOrder_Id(order.getId());
        if (itemsNow == null || itemsNow.isEmpty()) {
            order.setPhiVanChuyen(BigDecimal.ZERO);
            recalcOrderTotals(order);
            return getDetail(orderId);
        }
        int fee;
        if (isFreeShip(subtotal)) {
            fee = 0;
        } else {
            GhnFeeRequest feeReq = buildGhnFeeRequest(order, toDistrictId, toWardCode, useInsurance);
            fee = ghnClientService.calcFee(feeReq);
        }

        order.setPhiVanChuyen(BigDecimal.valueOf(fee));

        recalcOrderTotals(order);
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
        requireDraftOrPendingConfirm(order);

        // ✅ Tính tiền hàng
        BigDecimal tongTien = nvl(orderCTRepository.sumGiaBanByOrderId(order.getId()));
        order.setGiaTriChuaGiam(tongTien);

        // ✅ Load voucher
        PhieuGiamGia phieu = phieuGiamGiaRepository.findByIdPhieugiamgia(voucherCode)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu giảm giá không tồn tại: " + voucherCode));

        // ✅ validate voucher
        validateVoucherUsable(phieu, tongTien);

        // ✅ Tính giảm
        BigDecimal soTienGiam = phieuGiamGiaService.calculateDiscount(phieu, tongTien);
        order.setGiaTriGiamGia(soTienGiam);

        BigDecimal phiKhac = nvl(order.getPhiDichVuKhac());
        BigDecimal phiShip = nvl(order.getPhiVanChuyen());

        BigDecimal mustPay = tongTien
                .subtract(soTienGiam)
                .add(phiKhac)
                .add(phiShip);

        if (mustPay.compareTo(BigDecimal.ZERO) < 0) mustPay = BigDecimal.ZERO;
        order.setTongTienThuHo(mustPay);

        // =========================
        // ✅ GGHD: LOCK + chỉ dùng 1 record
        // =========================
        // 1) khóa theo order để apply/clear không đụng nhau
        GiamGiaHoaDon g = giamGiaHoaDonRepository.findOneByOrderIdForUpdate(order.getId())
                .orElse(null);

        // 2) nếu dữ liệu cũ từng tạo nhiều dòng, xóa sạch rồi tạo lại 1 dòng
        // (cách này "đập đi làm lại" để hệ thống sạch luôn)
        if (g == null) {
            // đảm bảo không còn rác
            giamGiaHoaDonRepository.deleteByOrderId(order.getId());

            g = new GiamGiaHoaDon();

            g.setIdGiamgiahoadon("GG" + System.currentTimeMillis());
            g.setIdOrders(order);
        }

        g.setIdPhieuGiamGia(phieu);

        BigDecimal sauGiamHang = tongTien.subtract(soTienGiam);
        if (sauGiamHang.compareTo(BigDecimal.ZERO) < 0) sauGiamHang = BigDecimal.ZERO;

        g.setSoTienTruocGiam(tongTien);
        g.setSoTienSauGiam(sauGiamHang);

        // ✅ Save trong cùng transaction
        orderRepository.save(order);
        giamGiaHoaDonRepository.save(g);

        OrderRespone res = new OrderRespone();
        res.setMaDonHang(order.getMaDonHang());
        res.setTongTien(order.getGiaTriChuaGiam());
        res.setSoTienGiam(order.getGiaTriGiamGia());
        res.setPhieuGiamGia(phieu.getIdPhieugiamgia());
        res.setTongPhaiTra(order.getTongTienThuHo());
        return res;
    }


    @Override
    @Transactional
    public void clearVoucher(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        requireDraftOrPendingConfirm(order);

        // ✅ LOCK + xoá sạch theo order (chỉ 1 lần)
        giamGiaHoaDonRepository.findOneByOrderIdForUpdate(order.getId());
        giamGiaHoaDonRepository.deleteByOrderId(order.getId());

        BigDecimal tongTien = nvl(orderCTRepository.sumGiaBanByOrderId(order.getId()));
        order.setGiaTriChuaGiam(tongTien);
        order.setGiaTriGiamGia(BigDecimal.ZERO);

        BigDecimal mustPay = tongTien
                .add(nvl(order.getPhiDichVuKhac()))
                .add(nvl(order.getPhiVanChuyen()));

        if (mustPay.compareTo(BigDecimal.ZERO) < 0) mustPay = BigDecimal.ZERO;
        order.setTongTienThuHo(mustPay);

        orderRepository.save(order);
    }


    // ===== THÊM THANH TOÁN =====
    @Override
    @Transactional
    public PosOrderDetailDTO addPayment(UUID orderId, PosAddPaymentRequest request) {
        Order order = getOrderOrThrow(orderId);
        requireDraftOrPendingConfirm(order);

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

        recalcOrderTotals(order);

        BigDecimal mustPay = nvl(order.getTongTienThuHo());
        BigDecimal paidBefore =
                nvl(hinhThucThanhToanChiTietRepository.sumSoTienByOrder(orderId));

        if (paidBefore.compareTo(mustPay) >= 0) {
            throw new IllegalStateException("Đơn hàng đã thanh toán đủ, không thể thêm thanh toán.");
        }

        BigDecimal remaining = mustPay.subtract(paidBefore);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        BigDecimal khachDua = request.getKhachDua();
        BigDecimal soTienThanhToan = khachDua.min(remaining);
        BigDecimal tienTraLai = khachDua.subtract(soTienThanhToan);

        HinhThucThanhToanChiTiet ct = new HinhThucThanhToanChiTiet();
        ct.setId(UUID.randomUUID());
        ct.setIdThanhToanCt("PAY" + System.currentTimeMillis());
        ct.setIdOrder(order);
        ct.setIdHinhThucThanhToan(hinhThuc);
        ct.setSoTienThanhToan(soTienThanhToan);
        ct.setKhachDua(khachDua);
        ct.setTraLai(tienTraLai);

        hinhThucThanhToanChiTietRepository.save(ct);

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

    // ===== HOÀN TẤT ĐƠN =====
    @Override
    @Transactional
    public PosOrderDetailDTO complete(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        requireDraftOrPendingConfirm(order);

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
            if (ct.getIdSeri() != null && ct.getIdSeri().getId() != null) {
                UUID seriId = ct.getIdSeri().getId();

                // ✅ chốt bán an toàn: PENDING -> SOLD
                int updated = seriRepository.updateTrangThaiSeriIfCurrent(seriId, SERI_PENDING, SERI_SOLD);
                if (updated != 1) {
                    throw new IllegalStateException("Seri " + ct.getIdSeri().getIdSeri() + " không ở trạng thái giữ hàng (PENDING)");
                }
            }
        }

// ✅ Voucher: validate lại ở thời điểm complete + trừ số lượng (atomic)
        List<GiamGiaHoaDon> discounts = giamGiaHoaDonRepository.findByIdOrders_Id(orderId);
        for (GiamGiaHoaDon d : discounts) {
            PhieuGiamGia phieu = d.getIdPhieuGiamGia();
            if (phieu == null) continue;

            // validate lại (voucher có thể hết hạn / bị tắt sau lúc apply)
            validateVoucherUsable(phieu, nvl(order.getGiaTriChuaGiam()));

            // ✅ trừ số lượng atomic để tránh race-condition
            int updated = phieuGiamGiaRepository.decrementQtyIfAvailable(phieu.getId());
            if (updated != 1) {
                throw new IllegalStateException("Voucher '" + phieu.getIdPhieugiamgia() + "' đã hết lượt sử dụng");
            }

            // ✅ optional: nếu về 0 thì tắt
            PhieuGiamGia latest = phieuGiamGiaRepository.findById(phieu.getId()).orElse(null);
            if (latest != null && latest.getSoLuong() != null && latest.getSoLuong() <= 0) {
                latest.setTrangThai(0);
                phieuGiamGiaRepository.save(latest);
            }
        }

        order.setTrangThaiThanhToan(PAYMENT_STATUS_PAID);

        if (isDelivery(order)) {
            order.setTrangThai(ORDER_STATUS_PREPARING);
            orderRepository.save(order);
            writeLog(order, ORDER_STATUS_PREPARING, "Đang chuẩn bị hàng: " + order.getMaDonHang());
        } else {
            order.setTrangThai(ORDER_STATUS_COMPLETED);
            orderRepository.save(order);
            writeLog(order, ORDER_STATUS_COMPLETED, "Hoàn tất đơn tại quầy: " + order.getMaDonHang());
        }

        return getDetail(orderId);
    }

    // ===== HUỶ ĐƠN =====
    @Override
    @Transactional
    public PosOrderDetailDTO cancel(UUID orderId) {
        Order order = getOrderOrThrow(orderId);
        requireDraftOrPendingConfirm(order);

// ✅ nếu đã có thanh toán thì không cho huỷ (tránh lệch đối soát)
        BigDecimal paid = nvl(hinhThucThanhToanChiTietRepository.sumSoTienByOrder(orderId));
        if (paid.compareTo(BigDecimal.ZERO) > 0 || Objects.equals(order.getTrangThaiThanhToan(), PAYMENT_STATUS_PAID)) {
            throw new IllegalStateException("Đơn đã có thanh toán, không thể huỷ. Vui lòng hoàn tiền/ghi nhận refund trước.");
        }

        List<OrderCT> items = orderCTRepository.findByIdOrder_Id(orderId);
        for (OrderCT ct : items) {
            if (ct.getIdSeri() != null && ct.getIdSeri().getId() != null) {
                UUID seriId = ct.getIdSeri().getId();
                // trả hàng: PENDING -> ACTIVE (idempotent)
                seriRepository.updateTrangThaiSeriIfCurrent(seriId, SERI_PENDING, SERI_ACTIVE);
            }
        }
        order.setTrangThaiThanhToan(PAYMENT_STATUS_UNPAID);

        // ✅ theo enum mới: CANCELED(7) dùng chung
        order.setTrangThai(ORDER_STATUS_CANCELLED);
        orderRepository.save(order);

        writeLog(order, ORDER_STATUS_CANCELLED,
                (isDelivery(order) ? "Hủy đơn giao hàng: " : "Hủy đơn tại quầy: ")
                        + order.getMaDonHang());

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy Order: " + id
                ));
    }

    private void requireDraftOrPendingConfirm(Order order) {
        int st = (order.getTrangThai() == null) ? ORDER_STATUS_DRAFT : order.getTrangThai();

        // Chỉ chặn khi đã kết thúc
        if (st == ORDER_STATUS_CANCELLED || st == ORDER_STATUS_COMPLETED) {
            throw new IllegalStateException("Đơn hàng đã hoàn thành/huỷ, không thể chỉnh sửa");
        }

        // Cho phép chỉnh khi còn DRAFT hoặc PENDING_CONFIRM
        if (st != ORDER_STATUS_DRAFT && st != ORDER_STATUS_PENDING_CONFIRM) {
            throw new IllegalStateException("Đơn đã chuyển trạng thái, không thể chỉnh sửa");
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
                    BigDecimal diff = truoc.subtract(sau);
                    return diff.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : diff;

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

    // ===== VALIDATION & SAVE HELPERS =====
    private void validateVoucherUsable(PhieuGiamGia phieu, BigDecimal tongTienHang) {
        if (phieu == null) {
            throw new IllegalStateException("Voucher không tồn tại");
        }
        if (phieu.getTrangThai() == null || phieu.getTrangThai() != 1) {
            throw new IllegalStateException("Voucher '" + phieu.getIdPhieugiamgia() + "' không ở trạng thái hoạt động");
        }

        LocalDate today = LocalDate.now();
        if (phieu.getNgayBatDau() != null && today.isBefore(phieu.getNgayBatDau())) {
            throw new IllegalStateException("Voucher '" + phieu.getIdPhieugiamgia() + "' chưa đến ngày áp dụng");
        }
        if (phieu.getNgayKetThuc() != null && today.isAfter(phieu.getNgayKetThuc())) {
            throw new IllegalStateException("Voucher '" + phieu.getIdPhieugiamgia() + "' đã hết hạn");
        }

        Integer qty = phieu.getSoLuong();
        if (qty == null) {
            throw new IllegalStateException("Voucher '" + phieu.getIdPhieugiamgia() + "' thiếu số lượng (soLuong)");
        }
        if (qty <= 0) {
            throw new IllegalStateException("Voucher '" + phieu.getIdPhieugiamgia() + "' đã hết lượt sử dụng");
        }

        BigDecimal min = phieu.getGiaTriMin();
        if (min != null && tongTienHang != null && tongTienHang.compareTo(min) < 0) {
            throw new IllegalStateException("Đơn chưa đủ điều kiện tối thiểu để áp voucher (min: " + min + ")");
        }
    }

    private void saveOrderWithMaRetry(Order order, int attempts) {
        int tried = 0;
        while (true) {
            try {
                orderRepository.save(order);
                return;
            } catch (DataIntegrityViolationException ex) {
                tried++;
                if (tried >= attempts) throw ex;
                order.setMaDonHang(generateMaDonHang());
            }
        }
    }

    private DiaChi saveDiaChiWithCodeRetry(DiaChi dc, int attempts) {
        int tried = 0;
        while (true) {
            try {
                return diaChiRepository.save(dc);
            } catch (DataIntegrityViolationException ex) {
                tried++;
                if (tried >= attempts) throw ex;
                dc.setIdDiaChi(generateDiaChiCode());
            }
        }
    }

    @Override
    @Transactional
    public PosOrderDetailDTO addItemsBySeriCode(UUID orderId, List<String> seriCodes) {
        Order order = getOrderOrThrow(orderId);
        requireDraftOrPendingConfirm(order);

        if (seriCodes == null || seriCodes.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mã seri trống");
        }

        List<UUID> seriUuids = new ArrayList<>();

        for (String code : seriCodes) {
            String trimmed = code.trim().toUpperCase();
            if (trimmed.isEmpty()) continue;

            Seri seri = seriRepository.findFirstByIdSeri(trimmed)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "SERI_NOT_FOUND: Không tìm thấy seri '" + trimmed + "'"
                    ));

            if (!Objects.equals(seri.getTrangThai(), SERI_ACTIVE)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "SERI_NOT_AVAILABLE: Sản phẩm không có sẵn (seri '" + trimmed + "')"
                );
            }
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



    // ✅ FIX: build fee request đúng khi nhiều sản phẩm + insurance = tổng tiền hàng
    private GhnFeeRequest buildGhnFeeRequest(Order order, Integer toDistrictId, String toWardCode, boolean useInsurance) {
        List<OrderCT> items = orderCTRepository.findByIdOrder_Id(order.getId());
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vui lòng thêm sản phẩm vào đơn trước khi tính phí vận chuyển."
            );
        }

        int totalWeightGram = 0;
        int maxL = 0, maxW = 0, maxH = 0;

        for (OrderCT ct : items) {
            if (ct == null) continue;
            Seri seri = ct.getIdSeri();
            if (seri == null) continue;
            LaptopChiTiet lct = seri.getIdLapTopCt();
            if (lct == null) continue;

            Laptop laptop = lct.getIdLaptop();
            KichThuoc kt = (laptop != null ? laptop.getIdKichThuoc() : null);

            int itemL = (kt != null ? GhnFeeRequestBuilder.toPositiveIntCeil(kt.getChieuDai()) : 0);
            int itemW = (kt != null ? GhnFeeRequestBuilder.toPositiveIntCeil(kt.getChieuRong()) : 0);
            int itemH = (kt != null ? GhnFeeRequestBuilder.toPositiveIntCeil(kt.getChieuCao()) : 0);

            if (itemL <= 0) itemL = GhnFeeRequestBuilder.DEFAULT_L_CM;
            if (itemW <= 0) itemW = GhnFeeRequestBuilder.DEFAULT_W_CM;
            if (itemH <= 0) itemH = GhnFeeRequestBuilder.DEFAULT_H_CM;

            maxL = Math.max(maxL, itemL);
            maxW = Math.max(maxW, itemW);
            maxH = Math.max(maxH, itemH);

            int itemWeight = (kt != null ? GhnFeeRequestBuilder.toWeightGram(kt.getKhoiLuong()) : 0);
            if (itemWeight <= 0) itemWeight = GhnFeeRequestBuilder.DEFAULT_ITEM_WEIGHT_GRAM;

            totalWeightGram += itemWeight;
        }

        BigDecimal subtotal = nvl(orderCTRepository.sumGiaBanByOrderId(order.getId()));
        order.setGiaTriChuaGiam(subtotal); // optional sync

        // ✅ CHỖ QUAN TRỌNG: truyền useInsurance vào builder
        return GhnFeeRequestBuilder.buildFeeRequest(
                toDistrictId,
                toWardCode,
                totalWeightGram,
                maxL, maxW, maxH,
                subtotal,
                useInsurance
        );
    }


    private int toPositiveIntCeil(Double v) {
        if (v == null) return 0;
        if (v <= 0) return 0;
        return (int) Math.ceil(v);
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

    private String upsertShipNote(String note, PosUpdateShippingRequest req) {
        String start = "<<SHIP>>";
        String end   = "<</SHIP>>";

        String hoTen = safe(req.getHoTen());
        String sdt   = safe(req.getSoDienThoai());

        // Địa chỉ dạng chữ để người đọc hiểu (FE nên gửi đủ các field này)
        String diaChiText = String.join(", ",
                Arrays.asList(
                        safe(req.getDiaChiChiTiet()),
                        safe(req.getPhuongXa()),
                        safe(req.getQuanHuyen()),
                        safe(req.getTinhThanh())
                ).stream().filter(s -> !s.isBlank()).toList()
        );

        // fallback nếu thiếu chữ thì vẫn lưu được ID/code
        String ghnLine = "GHN: P=" + nullSafe(req.getProvinceId())
                + " | D=" + nullSafe(req.getDistrictId())
                + " | W=" + safe(req.getWardCode());

        String content =
                "Người nhận: " + hoTen + " | SĐT: " + sdt + "\n" +
                        "Địa chỉ: " + diaChiText + "\n" +
                        ghnLine;

        String block = start + "\n" + content + "\n" + end;

        if (note == null) note = "";
        String pattern = "(?s)\\Q" + start + "\\E.*?\\Q" + end + "\\E";

        if (note.matches("(?s).*" + pattern + ".*")) {
            return note.replaceAll(pattern, java.util.regex.Matcher.quoteReplacement(block));
        }
        if (!note.isBlank() && !note.endsWith("\n")) note += "\n";
        return note + "\n" + block + "\n";
    }

    private String removeShipNote(String note) {
        if (note == null) return null;
        String start = "<<SHIP>>";
        String end   = "<</SHIP>>";
        String pattern = "(?s)\\n?\\Q" + start + "\\E.*?\\Q" + end + "\\E\\n?";
        return note.replaceAll(pattern, "");
    }

    private static final BigDecimal FREESHIP_THRESHOLD = new BigDecimal("30000000");

    private boolean isFreeShip(BigDecimal subtotal) {
        return subtotal != null && subtotal.compareTo(FREESHIP_THRESHOLD) >= 0;
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }
    private String nullSafe(Object o) { return o == null ? "" : String.valueOf(o); }
}
