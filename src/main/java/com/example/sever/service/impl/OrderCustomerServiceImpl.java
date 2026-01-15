package com.example.sever.service.impl;

import com.example.sever.dto.OrderDTO.*;
import com.example.sever.dto.Pos.GHN.GhnFeeRequest; // ✅ ADD
import com.example.sever.entity.*;
import com.example.sever.exception.ResourceNotFoundException;
import com.example.sever.repository.*;
import com.example.sever.service.GhnClientService; // ✅ ADD
import com.example.sever.service.OrderCustomerService;
import com.example.sever.service.PhieuGiamGiaService;
import com.example.sever.statusauto.OrderStatus;
import com.example.sever.statusauto.PaymentStatus;
import com.example.sever.statusauto.SeriStatus;
import com.example.sever.utils.GhnFeeRequestBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode; // ✅ ADD
import java.time.Instant;
import java.util.*;

@Service
public class OrderCustomerServiceImpl implements OrderCustomerService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderCTRepository orderCTRepo;

    @Autowired
    private OrderActionLogRepository orderActionLogRepo;

    @Autowired
    private GiamGiaHoaDonRepository giamGiaHoaDonRepo;

    @Autowired
    private HinhThucThanhToanChiTietRepository hinhThucThanhToanChiTietRepo;

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Autowired
    private DiaChiRepository diaChiRepo;

    @Autowired
    private SeriRepository seriRepo;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepo;

    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepo;

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @Autowired
    private LaptopChiTietRepository laptopChiTietRepo;

    @Autowired
    private GioHangRepository gioHangRepo;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepo;

    @Autowired
    private com.example.sever.service.MailService mailService;

    // ✅ ADD: GHN client service (tính phí vận chuyển giống POS)
    @Autowired
    private GhnClientService ghnClientService;

    @PersistenceContext
    private EntityManager entityManager;



    private boolean needShipping(String loaiDon) {
        if (loaiDon == null) return false;
        String v = loaiDon.trim().toUpperCase();
        // OrderType dbValue: TAI_QUAY / ONLINE / GIAO_HANG
        return "ONLINE".equals(v) || "GIAO_HANG".equals(v);
    }


    // ======================================================================================
    private boolean isCOD(HinhThucThanhToan httt) {
        if (httt == null || httt.getTenHinhThuc() == null) return false;
        return httt.getTenHinhThuc().trim().equalsIgnoreCase("COD");
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCustomerResponse taoDonHangCustomer(OrderCustomerRequest request) {

        UUID currentUserId = requireCurrentUserId();

// Nếu FE có gửi idTaiKhoan thì bắt buộc phải trùng current user
        if (request.getIdTaiKhoan() == null) {
            request.setIdTaiKhoan(currentUserId); // (nếu DTO cho phép set)
        } else if (!currentUserId.equals(request.getIdTaiKhoan())) {
            throw new IllegalArgumentException("Bạn không có quyền tạo đơn cho tài khoản khác");
        }

        TaiKhoan taiKhoan = taiKhoanRepo.findById(request.getIdTaiKhoan())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));



        DiaChi diaChi = diaChiRepo.findById(request.getIdDiaChi())
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại"));
        if (diaChi.getIdTaiKhoan() == null || diaChi.getIdTaiKhoan().getId() == null
                || !diaChi.getIdTaiKhoan().getId().equals(taiKhoan.getId())) {
            throw new IllegalArgumentException("Địa chỉ không thuộc tài khoản");
        }
        if (request.getListOrderCT() == null || request.getListOrderCT().isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một sản phẩm");
        }

        BigDecimal tongTienChuaGiam = BigDecimal.ZERO;
        List<Map<String, Object>> productList = new ArrayList<>();

        // ✅ ADD: gom thông số GHN ngay trong loop (giống POS)
        int totalWeightGram = 0;
        int maxL = 0, maxW = 0, maxH = 0;

        for (OrderCTCustomerRequest ctRequest : request.getListOrderCT()) {
            UUID laptopChiTietId = ctRequest.getIdLaptopChiTiet();

            if (!laptopChiTietRepo.existsById(laptopChiTietId)) {
                throw new ResourceNotFoundException("LaptopChiTiet không tồn tại: " + laptopChiTietId);
            }

            // ✅ GIỮ SERI ATOMIC: ACTIVE -> PENDING (chống trùng khi nhiều người đặt)
            List<UUID> reserved = seriRepo.reserveOneSeriForLaptopCt(
                    laptopChiTietId,
                    SeriStatus.ACTIVE.code(),
                    SeriStatus.PENDING.code()
            );

            if (reserved == null || reserved.isEmpty()) {
                throw new IllegalArgumentException("Đã hết hàng");
            }

            UUID seriId = reserved.get(0);

            LaptopChiTiet lct = laptopChiTietRepo.findById(laptopChiTietId)
                    .orElseThrow(() -> new ResourceNotFoundException("LaptopChiTiet không tồn tại: " + laptopChiTietId));

            BigDecimal giaBanDb = lct.getGiaBan();
            if (giaBanDb == null) throw new IllegalStateException("Sản phẩm chưa có giá bán");

            BigDecimal thanhTien = giaBanDb;
            tongTienChuaGiam = tongTienChuaGiam.add(thanhTien);

            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("laptopChiTietId", laptopChiTietId);
            productInfo.put("seriId", seriId);
            productInfo.put("giaBan", giaBanDb);
            productList.add(productInfo);

            // ✅ ADD: build thông số weight/dimensions cho GHN (đồng nhất POS)
            Laptop laptop = lct.getIdLaptop();
            KichThuoc kt = (laptop != null ? laptop.getIdKichThuoc() : null);

// ---- DIMENSIONS (cm) ----
            int itemL = (kt != null ? GhnFeeRequestBuilder.toPositiveIntCeil(kt.getChieuDai()) : 0);
            int itemW = (kt != null ? GhnFeeRequestBuilder.toPositiveIntCeil(kt.getChieuRong()) : 0);
            int itemH = (kt != null ? GhnFeeRequestBuilder.toPositiveIntCeil(kt.getChieuCao()) : 0);

            if (itemL <= 0) itemL = GhnFeeRequestBuilder.DEFAULT_L_CM;
            if (itemW <= 0) itemW = GhnFeeRequestBuilder.DEFAULT_W_CM;
            if (itemH <= 0) itemH = GhnFeeRequestBuilder.DEFAULT_H_CM;

            maxL = Math.max(maxL, itemL);
            maxW = Math.max(maxW, itemW);
            maxH = Math.max(maxH, itemH);

// ---- WEIGHT (gram) ----
            int itemWeight = (kt != null ? GhnFeeRequestBuilder.toWeightGram(kt.getKhoiLuong()) : 0);
            if (itemWeight <= 0) itemWeight = GhnFeeRequestBuilder.DEFAULT_ITEM_WEIGHT_GRAM;

            totalWeightGram += itemWeight;
        }

        BigDecimal soTienGiam = BigDecimal.ZERO;
        PhieuGiamGia phieuGiamGia = null;

        if (request.getIdPhieuGiamGia() != null) {
            phieuGiamGia = phieuGiamGiaRepo.findById(request.getIdPhieuGiamGia())
                    .orElseThrow(() -> new ResourceNotFoundException("Phiếu giảm giá không tồn tại"));

            if (phieuGiamGia.getTrangThai() == null || phieuGiamGia.getTrangThai() != 1) {
                throw new IllegalArgumentException("Phiếu giảm giá không khả dụng");
            }

            if (tongTienChuaGiam.compareTo(phieuGiamGia.getGiaTriMin()) < 0) {
                throw new IllegalArgumentException("Giá trị đơn hàng chưa đạt điều kiện áp dụng phiếu giảm giá");
            }
            Integer qty = phieuGiamGia.getSoLuong();

            if (qty != null && qty <= 0) {
                throw new IllegalArgumentException("Phiếu giảm giá đã hết lượt sử dụng");
            }

            soTienGiam = phieuGiamGiaService.calculateDiscount(phieuGiamGia, tongTienChuaGiam);
        } else {
            phieuGiamGia = phieuGiamGiaService.findBestCouponForOrder(tongTienChuaGiam);
            if (phieuGiamGia != null) {
                soTienGiam = phieuGiamGiaService.calculateDiscount(phieuGiamGia, tongTienChuaGiam);
            }
        }

        // ✅ FIX: TÍNH PHÍ VẬN CHUYỂN BẰNG GHN (giống POS) - KHÔNG LẤY TỪ FE
        BigDecimal phiVanChuyen = BigDecimal.ZERO;
        if (needShipping(request.getLoaiDon())) {
            Integer toDistrictId = diaChi.getDistrictId();
            String toWardCode = (diaChi.getWardCode() != null ? diaChi.getWardCode().trim() : null);

            if (toDistrictId == null || toWardCode == null || toWardCode.isBlank()) {
                throw new IllegalArgumentException("Địa chỉ thiếu districtId/wardCode để tính phí vận chuyển GHN");
            }

            if (totalWeightGram <= 0) totalWeightGram = GhnFeeRequestBuilder.DEFAULT_ITEM_WEIGHT_GRAM;

            boolean useInsurance = Boolean.TRUE.equals(request.getUseInsurance()); // ✅ NEW (DTO)

            // ✅ FREESHIP như POS (theo subtotal)
            if (isFreeShip(tongTienChuaGiam)) {
                phiVanChuyen = BigDecimal.ZERO;
            } else {
                GhnFeeRequest feeReq = GhnFeeRequestBuilder.buildFeeRequest(
                        toDistrictId,
                        toWardCode,
                        totalWeightGram,
                        maxL, maxW, maxH,
                        tongTienChuaGiam,
                        useInsurance // ✅ bật/tắt insurance theo request (giống POS)
                );

                int fee = ghnClientService.calcFee(feeReq);
                phiVanChuyen = BigDecimal.valueOf(fee);
            }

        }

        BigDecimal phiDichVuKhac = request.getPhiDichVuKhac() != null ? request.getPhiDichVuKhac() : BigDecimal.ZERO;
        BigDecimal tongTienThuHo = tongTienChuaGiam.subtract(soTienGiam).add(phiVanChuyen).add(phiDichVuKhac);

        Random rand = new Random();
        StringBuilder sb = new StringBuilder("OD");
        for (int i = 0; i < 6; i++) {
            sb.append(rand.nextInt(10));
        }
        StringBuilder mhd = new StringBuilder("MDH");
        for (int i = 0; i < 6; i++) {
            mhd.append(rand.nextInt(10));
        }

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setIdOrder(sb.toString());
        order.setIdTaiKhoan(taiKhoan);
        order.setIdDiaChi(diaChi);
        order.setMaDonHang(mhd.toString());
        order.setTenKhachHang(request.getTenKhachHang());
        order.setSdtKhachHang(request.getSdtKhachHang());
        order.setLoaiDon(request.getLoaiDon());
        order.setPhiVanChuyen(phiVanChuyen);
        order.setPhiDichVuKhac(phiDichVuKhac);
        order.setGiaTriChuaGiam(tongTienChuaGiam);
        order.setGiaTriGiamGia(soTienGiam);
        order.setTongTienThuHo(tongTienThuHo);

        // ✅ SỬA: trạng thái theo enum mới
        order.setTrangThai(OrderStatus.PENDING_CONFIRM.code());

        // ✅ SỬA: mặc định chưa thanh toán
        order.setTrangThaiThanhToan(PaymentStatus.UNPAID.code());

        String ghiChuFinal = request.getGhiChu() != null ? request.getGhiChu().trim() : "";
        if (request.getDiaChiDayDu() != null && !request.getDiaChiDayDu().trim().isEmpty()) {
            if (!ghiChuFinal.isEmpty()) {
                ghiChuFinal += " ";
            }
            ghiChuFinal += "[DIA_CHI_DAY_DU:" + request.getDiaChiDayDu().trim() + "]";
        }
        order.setGhiChu(ghiChuFinal.isEmpty() ? null : ghiChuFinal);
        order.setNgayTao(Instant.now());

        Order savedOrder = orderRepo.save(order);


        for (Map<String, Object> productInfo : productList) {
            UUID seriId = (UUID) productInfo.get("seriId");
            BigDecimal giaBan = (BigDecimal) productInfo.get("giaBan");

            OrderCT orderCT = new OrderCT();
            orderCT.setId(UUID.randomUUID());

            StringBuilder sbct = new StringBuilder("ODCT");
            for (int i = 0; i < 6; i++) {
                sbct.append(rand.nextInt(10));
            }
            orderCT.setIdOrderCt(sbct.toString());
            orderCT.setIdOrder(savedOrder);

            Seri seri = new Seri();
            seri.setId(seriId);
            orderCT.setIdSeri(seri);
            orderCT.setGiaBan(giaBan);

            orderCTRepo.save(orderCT);

        }

        if (phieuGiamGia != null && soTienGiam.compareTo(BigDecimal.ZERO) > 0) {
            GiamGiaHoaDon giamGiaHoaDon = new GiamGiaHoaDon();
            StringBuilder gg = new StringBuilder("GG");
            for (int i = 0; i < 6; i++) {
                gg.append(rand.nextInt(10));
            }
            giamGiaHoaDon.setIdGiamgiahoadon(gg.toString());
            giamGiaHoaDon.setIdOrders(savedOrder);
            giamGiaHoaDon.setIdPhieuGiamGia(phieuGiamGia);
            giamGiaHoaDon.setSoTienTruocGiam(tongTienChuaGiam);
            giamGiaHoaDon.setSoTienSauGiam(tongTienChuaGiam.subtract(soTienGiam));

            giamGiaHoaDonRepo.save(giamGiaHoaDon);

            if (phieuGiamGia.getSoLuong() != null && phieuGiamGia.getSoLuong() > 0) {
                phieuGiamGia.setSoLuong(phieuGiamGia.getSoLuong() - 1);
                phieuGiamGiaRepo.save(phieuGiamGia);
            }
        }

        boolean hasCOD = false;

        if (request.getListHinhThucThanhToan() != null && !request.getListHinhThucThanhToan().isEmpty()) {
            for (PaymentCustomerRequest paymentRequest : request.getListHinhThucThanhToan()) {
                HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepo
                        .findById(paymentRequest.getIdHinhThucThanhToan())
                        .orElseThrow(() -> new ResourceNotFoundException("Hình thức thanh toán không tồn tại"));

                boolean cod = isCOD(hinhThucThanhToan);

                // Kiểm tra nếu đã có COD thì không thêm nữa
                if (cod && !hasCOD) {
                    hasCOD = true;  // Đánh dấu COD đã có
                } else if (cod && hasCOD) {
                    throw new IllegalArgumentException("Hình thức thanh toán COD đã được thêm.");
                }

                HinhThucThanhToanChiTiet paymentDetail = new HinhThucThanhToanChiTiet();
                paymentDetail.setId(UUID.randomUUID());

                StringBuilder httt = new StringBuilder("HTTT");
                for (int i = 0; i < 6; i++) {
                    httt.append(new Random().nextInt(10));
                }
                paymentDetail.setIdThanhToanCt(httt.toString());
                paymentDetail.setIdOrder(savedOrder);
                paymentDetail.setIdHinhThucThanhToan(hinhThucThanhToan);

                // COD: chưa thu tiền => ghi 0
                if (cod) {
                    paymentDetail.setSoTienThanhToan(BigDecimal.ZERO);
                    String note = (paymentRequest.getGhiChu() != null ? paymentRequest.getGhiChu().trim() : "");
                    paymentDetail.setGhiChu((note.isEmpty() ? "" : note + " | ") + "COD - CHƯA THU TIỀN");
                } else {
                    paymentDetail.setSoTienThanhToan(paymentRequest.getSoTien() != null ? paymentRequest.getSoTien() : BigDecimal.ZERO);
                    paymentDetail.setGhiChu(paymentRequest.getGhiChu());
                }

                hinhThucThanhToanChiTietRepo.save(paymentDetail);
            }
        }


        // ✅ SỬA: cập nhật trạng thái thanh toán theo tổng tiền đã trả
        // - Nếu là VNPay (đi qua luồng redirect/callback) => để UNPAID tại thời điểm tạo đơn
        // - Nếu không phải VNPay => tính tổng paid, đủ thì PAID, chưa đủ thì UNPAID
        if (Boolean.TRUE.equals(request.getIsVnPay())) {
            // VNPay sẽ PAID ở callback
            savedOrder.setTrangThaiThanhToan(PaymentStatus.UNPAID.code());
        } else if (hasCOD) {
            // ✅ COD: lúc tạo đơn luôn UNPAID
            savedOrder.setTrangThaiThanhToan(PaymentStatus.UNPAID.code());
        } else {
            BigDecimal totalPaid = hinhThucThanhToanChiTietRepo.sumSoTienByOrder(savedOrder.getId());
            totalPaid = (totalPaid != null ? totalPaid : BigDecimal.ZERO);

            if (totalPaid.compareTo(tongTienThuHo) >= 0) {
                savedOrder.setTrangThaiThanhToan(PaymentStatus.PAID.code());
            } else {
                savedOrder.setTrangThaiThanhToan(PaymentStatus.UNPAID.code());
            }
        }
        orderRepo.save(savedOrder);


        OrderActionLog actionLog = new OrderActionLog();
        actionLog.setId(UUID.randomUUID());
        StringBuilder acl = new StringBuilder("ACL");
        for (int i = 0; i < 6; i++) {
            acl.append(rand.nextInt(10));
        }
        actionLog.setIdOrderacl(acl.toString());
        actionLog.setIdOrder(savedOrder);
        actionLog.setIdTaiKhoan(taiKhoan);
        actionLog.setHanhDong(savedOrder.getTrangThai()); // hoặc OrderStatus.PENDING_CONFIRM.code()
        actionLog.setNgayTao(Instant.now());              // nên set rõ (dù @PrePersist có)
        actionLog.setMoTa("Khách hàng tạo đơn hàng mới (Chờ xác nhận): " + savedOrder.getMaDonHang());

        orderActionLogRepo.save(actionLog);

        try {
            Optional<GioHang> gioHangOpt = gioHangRepo.findByIdTaiKhoan_Id(request.getIdTaiKhoan());
            if (gioHangOpt.isPresent()) {
                GioHang gioHang = gioHangOpt.get();

                for (Map<String, Object> productInfo : productList) {
                    UUID laptopChiTietId = (UUID) productInfo.get("laptopChiTietId");

                    Optional<GioHangChiTiet> gioHangChiTietOpt = gioHangChiTietRepo.findByIdGioHangAndIdSpct_Id(gioHang, laptopChiTietId);

                    if (gioHangChiTietOpt.isPresent()) {
                        GioHangChiTiet gioHangChiTiet = gioHangChiTietOpt.get();
                        Integer soLuongTrongGio = gioHangChiTiet.getSoLuong();

                        if (soLuongTrongGio != null) {
                            if (soLuongTrongGio <= 1) {
                                gioHangChiTietRepo.delete(gioHangChiTiet);
                            } else {
                                gioHangChiTiet.setSoLuong(soLuongTrongGio - 1);
                                gioHangChiTietRepo.save(gioHangChiTiet);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xử lý giỏ hàng: " + e.getMessage());
        }

        try {
            if (request.getIsVnPay() == null || !request.getIsVnPay()) {
                if (taiKhoan.getEmail() != null && !taiKhoan.getEmail().trim().isEmpty()) {
                    String diaChiGiaoHangStr;
                    if (request.getDiaChiDayDu() != null && !request.getDiaChiDayDu().trim().isEmpty()) {
                        diaChiGiaoHangStr = request.getDiaChiDayDu().trim();
                    } else {
                        String diaChiFromGhiChu = extractDiaChiDayDuFromGhiChu(savedOrder.getGhiChu());
                        if (diaChiFromGhiChu != null && !diaChiFromGhiChu.isEmpty()) {
                            diaChiGiaoHangStr = diaChiFromGhiChu;
                        } else {
                            diaChiGiaoHangStr = "";
                        }
                    }
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    Instant ngay = savedOrder.getNgayTao();
                    String ngayDatStr = (ngay != null ? ngay : Instant.now())
                            .atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh"))
                            .format(formatter);

                    List<com.example.sever.service.MailService.OrderEmailProduct> emailProducts = new ArrayList<>();
                    Map<UUID, com.example.sever.service.MailService.OrderEmailProduct> productMap = new HashMap<>();
                    List<Object[]> productDataList = orderCTRepo.findProductInfoByIdOrder(savedOrder.getId());
                    for (Object[] row : productDataList) {
                        UUID idLaptopChiTiet = convertToUUID(row[2]);
                        String tenSanPham = (String) row[3];
                        BigDecimal giaBan = (BigDecimal) row[5];

                        if (productMap.containsKey(idLaptopChiTiet)) {
                            com.example.sever.service.MailService.OrderEmailProduct existingProduct = productMap.get(idLaptopChiTiet);
                            existingProduct.setSoLuong(existingProduct.getSoLuong() + 1);
                            existingProduct.setThanhTien(existingProduct.getThanhTien().add(giaBan));
                        } else {
                            Integer soLuong = 1;
                            BigDecimal thanhTien = giaBan;

                            com.example.sever.service.MailService.OrderEmailProduct emailProduct =
                                    new com.example.sever.service.MailService.OrderEmailProduct();
                            emailProduct.setTenSanPham(tenSanPham);
                            emailProduct.setSoLuong(soLuong);
                            emailProduct.setGiaBan(giaBan);
                            emailProduct.setThanhTien(thanhTien);
                            productMap.put(idLaptopChiTiet, emailProduct);
                        }
                    }
                    emailProducts.addAll(productMap.values());

                    List<String> hinhThucThanhToanList = hinhThucThanhToanChiTietRepo
                            .findTenHinhThucThanhToanByIdOrder(savedOrder.getId());

                    com.example.sever.service.MailService.OrderEmailData emailData =
                            new com.example.sever.service.MailService.OrderEmailData();
                    emailData.setMaDonHang(savedOrder.getMaDonHang());
                    emailData.setTenKhachHang(savedOrder.getTenKhachHang());
                    emailData.setSdtKhachHang(savedOrder.getSdtKhachHang());
                    emailData.setDiaChiGiaoHang(diaChiGiaoHangStr);
                    emailData.setNgayDat(ngayDatStr);
                    emailData.setLoaiDon(savedOrder.getLoaiDon());
                    emailData.setTenTrangThai(convertTrangThaiToTen(savedOrder.getTrangThai()));
                    emailData.setHinhThucThanhToan(hinhThucThanhToanList);
                    emailData.setDanhSachSanPham(emailProducts);
                    emailData.setTongTienHang(tongTienChuaGiam);
                    emailData.setKhuyenMai(soTienGiam);
                    emailData.setPhiVanChuyen(phiVanChuyen);
                    emailData.setTongThanhToan(tongTienThuHo);
                    String ghiChuOriginal = removeDiaChiDayDuFromGhiChu(savedOrder.getGhiChu());
                    emailData.setGhiChu(ghiChuOriginal);

                    mailService.sendOrderConfirmationEmail(taiKhoan.getEmail(), emailData);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email xác nhận đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }

        OrderCustomerResponse response = new OrderCustomerResponse();
        response.setIdOrder(savedOrder.getId());
        response.setMaDonHang(savedOrder.getMaDonHang());
        response.setTongTien(tongTienChuaGiam);
        response.setSoTienGiam(soTienGiam);
        response.setPhieuGiamGia(phieuGiamGia != null ? phieuGiamGia.getIdPhieugiamgia() : null);
        response.setTongPhaiTra(tongTienThuHo);
        response.setTrangThai(savedOrder.getTrangThai());
        response.setMessage("Tạo đơn hàng thành công!");

        // ✅ SỬA: set thêm fields mới trong response
        response.setLoaiDon(savedOrder.getLoaiDon());
        response.setTenTrangThai(convertTrangThaiToTen(savedOrder.getTrangThai()));
        response.setTrangThaiThanhToan(savedOrder.getTrangThaiThanhToan());
        PaymentStatus ps = PaymentStatus.fromCode(savedOrder.getTrangThaiThanhToan());
        response.setTenTrangThaiThanhToan(ps == PaymentStatus.PAID ? "Đã thanh toán" : "Chưa thanh toán");

        return response;
    }

    private String generateRandomCode(int length) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    private String generateMaDonHangCustomer() {
        String lastCode = orderRepo.findLastMaDonHang();
        int next = 1;

        if (lastCode != null && lastCode.length() >= 8) {
            try {
                String numberStr = lastCode.substring(2, 5);
                next = Integer.parseInt(numberStr) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }

        return String.format("OD%03d-2024", next);
    }

    @Override
    public List<OrderListCustomerResponse> getDanhSachDonHangByTaiKhoanCustomer(UUID idTaiKhoan) {
        UUID currentUserId = requireCurrentUserId();
        if (!currentUserId.equals(idTaiKhoan)) {
            throw new IllegalArgumentException("Bạn không có quyền xem đơn hàng của tài khoản khác");
        }

        if (idTaiKhoan == null) {
            throw new IllegalArgumentException("ID tài khoản không được để trống");
        }
        if (!taiKhoanRepo.existsById(idTaiKhoan)) {
            throw new ResourceNotFoundException("Tài khoản không tồn tại: " + idTaiKhoan);
        }
        List<Order> orders = orderRepo.findByIdTaiKhoanOrderByIdDesc(idTaiKhoan);
        List<OrderListCustomerResponse> responseList = new ArrayList<>();
        for (Order order : orders) {
            OrderListCustomerResponse response = OrderListCustomerResponse.builder()
                    .idOrder(order.getId())
                    .maDonHang(order.getMaDonHang())
                    .ngayTao(order.getNgayTao() != null ? order.getNgayTao() : order.getNgayCapNhat())
                    .trangThai(order.getTrangThai())
                    .tenKhachHang(order.getTenKhachHang())
                    .sdtKhachHang(order.getSdtKhachHang())
                    .tongTienThuHo(order.getTongTienThuHo())
                    .giaTriChuaGiam(order.getGiaTriChuaGiam())
                    .giaTriGiamGia(order.getGiaTriGiamGia())
                    .loaiDon(order.getLoaiDon())
                    .ghiChu(order.getGhiChu())
                    .build();

            List<String> hinhThucThanhToanList = hinhThucThanhToanChiTietRepo.findTenHinhThucThanhToanByIdOrder(order.getId());
            response.setHinhThucThanhToan(hinhThucThanhToanList);

            String diaChiStr = extractDiaChiDayDuFromGhiChu(order.getGhiChu());
            if (diaChiStr == null || diaChiStr.isEmpty()) {
                diaChiStr = buildDiaChiGiaoHangForList(order.getIdDiaChi());
            }
            response.setDiaChi(diaChiStr);

            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public List<OrderProductCustomerResponse> getDanhSachSanPhamByOrderCustomer(UUID idOrder) {
        if (idOrder == null) {
            throw new IllegalArgumentException("ID đơn hàng không được để trống");
        }

        UUID currentUserId = requireCurrentUserId();

// Load order + check ownership
        Order order = orderRepo.findById(idOrder)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại: " + idOrder));

        if (order.getIdTaiKhoan() == null || order.getIdTaiKhoan().getId() == null
                || !order.getIdTaiKhoan().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Bạn không có quyền xem đơn hàng này");
        }

        List<Object[]> productDataList = orderCTRepo.findProductInfoByIdOrder(order.getId());


        List<OrderProductCustomerResponse> responseList = new ArrayList<>();
        for (Object[] row : productDataList) {
            UUID idOrderCT = convertToUUID(row[0]);
            UUID idSeri = convertToUUID(row[1]);
            UUID idLaptopChiTiet = convertToUUID(row[2]);
            String tenSanPham = (String) row[3];
            String anhSanPham = row[4] != null ? (String) row[4] : null;
            BigDecimal giaBan = (BigDecimal) row[5];
            Integer soLuong = 1;

            BigDecimal thanhTien = giaBan;

            OrderProductCustomerResponse response = OrderProductCustomerResponse.builder()
                    .idOrderCT(idOrderCT)
                    .idSeri(idSeri)
                    .idLaptopChiTiet(idLaptopChiTiet)
                    .tenSanPham(tenSanPham)
                    .anhSanPham(anhSanPham)
                    .giaBan(giaBan)
                    .soLuong(soLuong)
                    .thanhTien(thanhTien)
                    .build();

            responseList.add(response);
        }

        return responseList;
    }

    private UUID convertToUUID(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof UUID) {
            return (UUID) obj;
        }
        if (obj instanceof String) {
            try {
                return UUID.fromString((String) obj);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Không thể convert String sang UUID: " + obj);
            }
        }
        throw new IllegalArgumentException("Không thể convert object sang UUID: " + obj.getClass().getName());
    }

    @Override
    public List<OrderDetailCustomerResponse> timKiemDonHangCustomer(SearchOrderCustomerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request không được để trống");
        }
        if (request.getMaDonHang() == null || request.getMaDonHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn hàng không được để trống");
        }
        if (request.getSdt() == null || request.getSdt().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        String normalizedSdt = request.getSdt().trim().replaceAll("[\\s-()]", "");
        String normalizedMaDonHang = request.getMaDonHang().trim();

        List<Order> orders = orderRepo.findByMaDonHangAndSdtKhachHang(normalizedMaDonHang, normalizedSdt);

        if (orders.isEmpty() && !normalizedSdt.equals(request.getSdt().trim())) {
            orders = orderRepo.findByMaDonHangAndSdtKhachHang(normalizedMaDonHang, request.getSdt().trim());
        }

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng với mã đơn: " + request.getMaDonHang() + " và số điện thoại: " + request.getSdt());
        }

        List<OrderDetailCustomerResponse> responseList = new ArrayList<>();
        for (Order order : orders) {
            String diaChiGiaoHang = extractDiaChiDayDuFromGhiChu(order.getGhiChu());
            if (diaChiGiaoHang == null || diaChiGiaoHang.isEmpty()) {
                diaChiGiaoHang = buildDiaChiGiaoHang(order.getIdDiaChi());
            }

            Instant ngayDat = order.getNgayTao() != null ? order.getNgayTao() : Instant.now();

            List<String> hinhThucThanhToan = hinhThucThanhToanChiTietRepo
                    .findTenHinhThucThanhToanByIdOrder(order.getId());

            List<OrderProductCustomerResponse> danhSachSanPham = orderCTRepo.findProductInfoByIdOrder(order.getId())
                    .stream()
                    .map(row -> {
                        UUID idOrderCT = convertToUUID(row[0]);
                        UUID idSeri = convertToUUID(row[1]);
                        UUID idLaptopChiTiet = convertToUUID(row[2]);
                        String tenSanPham = (String) row[3];
                        String anhSanPham = row[4] != null ? (String) row[4] : null;
                        BigDecimal giaBan = (BigDecimal) row[5];
                        Integer soLuong = 1;
                        BigDecimal thanhTien = giaBan;

                        return OrderProductCustomerResponse.builder()
                                .idOrderCT(idOrderCT)
                                .idSeri(idSeri)
                                .idLaptopChiTiet(idLaptopChiTiet)
                                .tenSanPham(tenSanPham)
                                .anhSanPham(anhSanPham)
                                .giaBan(giaBan)
                                .soLuong(soLuong)
                                .thanhTien(thanhTien)
                                .build();
                    })
                    .toList();

            String tenTrangThai = convertTrangThaiToTen(order.getTrangThai());

            OrderDetailCustomerResponse response = OrderDetailCustomerResponse.builder()
                    .idOrder(order.getId())
                    .maDonHang(order.getMaDonHang())
                    .tenKhachHang(order.getTenKhachHang())
                    .sdtKhachHang(order.getSdtKhachHang())
                    .diaChiGiaoHang(diaChiGiaoHang)
                    .ngayDat(ngayDat)
                    .hinhThucThanhToan(hinhThucThanhToan)
                    .trangThai(order.getTrangThai())
                    .tenTrangThai(tenTrangThai)
                    .danhSachSanPham(danhSachSanPham)
                    .tongTienHang(order.getGiaTriChuaGiam() != null ? order.getGiaTriChuaGiam() : BigDecimal.ZERO)
                    .khuyenMai(order.getGiaTriGiamGia() != null ? order.getGiaTriGiamGia() : BigDecimal.ZERO)
                    .phiVanChuyen(order.getPhiVanChuyen() != null ? order.getPhiVanChuyen() : BigDecimal.ZERO)
                    .tongThanhToan(order.getTongTienThuHo() != null ? order.getTongTienThuHo() : BigDecimal.ZERO)
                    .build();

            responseList.add(response);
        }

        return responseList;
    }

    private String buildDiaChiGiaoHang(DiaChi diaChi) {
        if (diaChi == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (diaChi.getDiaChiChiTiet() != null && !diaChi.getDiaChiChiTiet().trim().isEmpty()) {
            sb.append(diaChi.getDiaChiChiTiet());
        }
        if (diaChi.getPhuongXa() != null && !diaChi.getPhuongXa().trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(diaChi.getPhuongXa());
        }
        if (diaChi.getQuanHuyen() != null && !diaChi.getQuanHuyen().trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(diaChi.getQuanHuyen());
        }
        if (diaChi.getTinhThanh() != null && !diaChi.getTinhThanh().trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(diaChi.getTinhThanh());
        }
        if (diaChi.getQuocGia() != null && !diaChi.getQuocGia().trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(diaChi.getQuocGia());
        }
        return sb.toString();
    }

    private String buildDiaChiGiaoHangForList(DiaChi diaChi) {
        if (diaChi == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (diaChi.getDiaChiChiTiet() != null && !diaChi.getDiaChiChiTiet().trim().isEmpty()) {
            sb.append(diaChi.getDiaChiChiTiet());
        }
        if (diaChi.getPhuongXa() != null && !diaChi.getPhuongXa().trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(diaChi.getPhuongXa());
        }
        if (diaChi.getQuanHuyen() != null && !diaChi.getQuanHuyen().trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(diaChi.getQuanHuyen());
        }
        if (diaChi.getTinhThanh() != null && !diaChi.getTinhThanh().trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(diaChi.getTinhThanh());
        }
        return sb.toString();
    }

    private String extractDiaChiDayDuFromGhiChu(String ghiChu) {
        if (ghiChu == null || ghiChu.trim().isEmpty()) {
            return null;
        }
        int startIndex = ghiChu.lastIndexOf("[DIA_CHI_DAY_DU:");
        if (startIndex != -1) {
            int endIndex = ghiChu.indexOf("]", startIndex);
            if (endIndex != -1) {
                String diaChiDayDu = ghiChu.substring(startIndex + "[DIA_CHI_DAY_DU:".length(), endIndex);
                return diaChiDayDu.trim();
            }
        }
        return null;
    }

    private String removeDiaChiDayDuFromGhiChu(String ghiChu) {
        if (ghiChu == null || ghiChu.trim().isEmpty()) {
            return ghiChu;
        }
        int startIndex = ghiChu.lastIndexOf("[DIA_CHI_DAY_DU:");
        if (startIndex != -1) {
            int endIndex = ghiChu.indexOf("]", startIndex);
            if (endIndex != -1) {
                String before = ghiChu.substring(0, startIndex).trim();
                if (before.endsWith(" ")) {
                    before = before.substring(0, before.length() - 1).trim();
                }
                return before.isEmpty() ? null : before;
            }
        }
        return ghiChu;
    }

    // ✅ SỬA: map tên trạng thái theo enum mới (không hardcode số kiểu cũ)
    private String convertTrangThaiToTen(Integer trangThai) {
        if (trangThai == null) return "Không xác định";
        OrderStatus s = OrderStatus.fromCode(trangThai);
        return switch (s) {
            case DRAFT -> "Tạo đơn";
            case PENDING_CONFIRM -> "Chờ xác nhận";
            case CONFIRMED -> "Đã xác nhận";
            case PREPARING -> "Đang chuẩn bị hàng";
            case SHIPPING -> "Đang vận chuyển";
            case DELIVERED -> "Đã giao hàng";
            case COMPLETED -> "Hoàn tất";
            case CANCELED -> "Hủy";
        };
    }
    // thêm vào cuối class
    private UUID getCurrentUserId() {
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            Object principal = auth.getPrincipal();

            if (principal instanceof TaiKhoan tk) return tk.getId();
            if (principal instanceof String s) {
                String username = s.trim();
                if (!username.isEmpty() && !"anonymousUser".equalsIgnoreCase(username)) {
                    TaiKhoan tk = taiKhoanRepo.findByEmail(username).orElse(null);
                    if (tk != null) return tk.getId();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private UUID requireCurrentUserId() {
        UUID id = getCurrentUserId();
        if (id == null) throw new RuntimeException("Bạn chưa đăng nhập hoặc phiên không hợp lệ");
        return id;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCustomerResponse huyDonHangCustomer(UUID idOrder, UUID idTaiKhoan) {
        UUID currentUserId = requireCurrentUserId();
        idTaiKhoan = currentUserId; // ép về user đang đăng nhập (bỏ qua id client truyền)
        Order order = orderRepo.findById(idOrder)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        if (!order.getIdTaiKhoan().getId().equals(idTaiKhoan)) {
            throw new IllegalArgumentException("Bạn không có quyền hủy đơn hàng này");
        }

        if (order.getTrangThai() == null) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ");
        }

        // Đã huỷ rồi
        if (order.getTrangThai().equals(OrderStatus.CANCELED.code())) {
            throw new IllegalArgumentException("Đơn hàng đã được hủy trước đó");
        }

        // ONLINE/GIAO_HANG: đã xác nhận thì khách không được hủy
        if (needShipping(order.getLoaiDon()) && order.getTrangThai() >= OrderStatus.CONFIRMED.code()) {
            throw new IllegalArgumentException("Đơn hàng đã được xác nhận, khách không thể hủy");
        }

// vẫn giữ rule chung: đang giao hàng trở lên thì không hủy được
        if (order.getTrangThai() >= OrderStatus.SHIPPING.code()) {
            throw new IllegalArgumentException("Không thể hủy đơn hàng đang vận chuyển hoặc đã hoàn thành");
        }
// ====

        // 1) Update trạng thái đơn
        order.setTrangThai(OrderStatus.CANCELED.code());
        Order savedOrder = orderRepo.save(order);
        entityManager.flush();

        // 2) Trả seri về ACTIVE (atomic)
        List<OrderCT> orderCTList = orderCTRepo.findByIdOrder(idOrder);
        for (OrderCT orderCT : orderCTList) {
            if (orderCT.getIdSeri() != null && orderCT.getIdSeri().getId() != null) {
                UUID seriId = orderCT.getIdSeri().getId();

                // ✅ FIX: atomic update tránh race-condition
                seriRepo.updateTrangThaiSeriIfCurrent(
                        seriId,
                        SeriStatus.PENDING.code(),
                        SeriStatus.ACTIVE.code()
                );
            }
        }

        // 3) (Tuỳ chọn) Hoàn lại số lượng voucher nếu đơn có áp voucher và trước đó đã trừ
        try {
            List<GiamGiaHoaDon> ggList = giamGiaHoaDonRepo.findByIdOrders_Id(savedOrder.getId());
            if (ggList != null && !ggList.isEmpty()) {
                GiamGiaHoaDon gg = ggList.get(0);

                PhieuGiamGia pgg = gg.getIdPhieuGiamGia();
                if (pgg != null && pgg.getSoLuong() != null) {
                    pgg.setSoLuong(pgg.getSoLuong() + 1);
                    phieuGiamGiaRepo.save(pgg);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi hoàn lại voucher: " + e.getMessage());
        }

        // 4) Log action
        TaiKhoan taiKhoan = taiKhoanRepo.findById(idTaiKhoan)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        Random rand = new Random();
        OrderActionLog actionLog = new OrderActionLog();
        actionLog.setId(UUID.randomUUID());

        StringBuilder acl = new StringBuilder("ACL");
        for (int i = 0; i < 6; i++) acl.append(rand.nextInt(10));

        actionLog.setIdOrderacl(acl.toString());
        actionLog.setIdOrder(savedOrder);
        actionLog.setIdTaiKhoan(taiKhoan);
        actionLog.setHanhDong(OrderStatus.CANCELED.code());
        actionLog.setNgayTao(Instant.now());
        actionLog.setMoTa("Khách hàng hủy đơn hàng: " + savedOrder.getMaDonHang());
        orderActionLogRepo.save(actionLog);

        // 5) Response
        OrderCustomerResponse response = new OrderCustomerResponse();
        response.setIdOrder(savedOrder.getId());
        response.setMaDonHang(savedOrder.getMaDonHang());
        response.setTongTien(savedOrder.getTongTienThuHo());
        response.setSoTienGiam(savedOrder.getGiaTriGiamGia());
        response.setTongPhaiTra(savedOrder.getTongTienThuHo());
        response.setTrangThai(savedOrder.getTrangThai());
        response.setMessage("Hủy đơn hàng thành công");

        response.setLoaiDon(savedOrder.getLoaiDon());
        response.setTenTrangThai(convertTrangThaiToTen(savedOrder.getTrangThai()));
        response.setTrangThaiThanhToan(savedOrder.getTrangThaiThanhToan());
        PaymentStatus ps = PaymentStatus.fromCode(savedOrder.getTrangThaiThanhToan());
        response.setTenTrangThaiThanhToan(ps == PaymentStatus.PAID ? "Đã thanh toán" : "Chưa thanh toán");

        return response;
    }

    private static final BigDecimal FREESHIP_THRESHOLD = new BigDecimal("30000000");

    private boolean isFreeShip(BigDecimal subtotal) {
        return subtotal != null && subtotal.compareTo(FREESHIP_THRESHOLD) >= 0;
    }
}
