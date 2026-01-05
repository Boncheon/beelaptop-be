package com.example.sever.service.impl;

import com.example.sever.dto.OrderDTO.*;
import com.example.sever.entity.*;
import com.example.sever.exception.ResourceNotFoundException;
import com.example.sever.repository.*;
import com.example.sever.service.MailService;
import com.example.sever.service.OrderCustomerService;
import com.example.sever.service.PhieuGiamGiaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @PersistenceContext
    private EntityManager entityManager;
// update logic code huy 05.01
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCustomerResponse taoDonHangCustomer(OrderCustomerRequest request) {
        TaiKhoan taiKhoan = taiKhoanRepo.findById(request.getIdTaiKhoan())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        DiaChi diaChi = diaChiRepo.findById(request.getIdDiaChi())
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại"));

        if (request.getListOrderCT() == null || request.getListOrderCT().isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một sản phẩm");
        }

        BigDecimal tongTienChuaGiam = BigDecimal.ZERO;
        List<Map<String, Object>> productList = new ArrayList<>();

        Map<UUID, Integer> seriUsageCount = new HashMap<>();

        for (OrderCTCustomerRequest ctRequest : request.getListOrderCT()) {
            UUID laptopChiTietId = ctRequest.getIdLaptopChiTiet();

            if (!laptopChiTietRepo.existsById(laptopChiTietId)) {
                throw new ResourceNotFoundException("LaptopChiTiet không tồn tại: " + laptopChiTietId);
            }

            List<String> seriIdStrings = seriRepo.findSeriIdsByLaptopChiTietIdAndTrangThai(
                    laptopChiTietId.toString().toUpperCase()
            );

            java.util.List<UUID> seriIds = new java.util.ArrayList<>();
            for (String seriIdStr : seriIdStrings) {
                try {
                    seriIds.add(UUID.fromString(seriIdStr));
                } catch (IllegalArgumentException e) {
                }
            }

            if (seriIds.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy Seri cho LaptopChiTiet: " + laptopChiTietId + ". Vui lòng kiểm tra dữ liệu Seri trong database (cột id_lap_top_ct).");
            }

            int currentUsageCount = seriUsageCount.getOrDefault(laptopChiTietId, 0);
            if (currentUsageCount >= seriIds.size()) {
                throw new IllegalArgumentException(
                        "Không đủ Seri khả dụng cho LaptopChiTiet: " + laptopChiTietId +
                                ". Cần: " + (currentUsageCount + 1) + ", Có sẵn: " + seriIds.size()
                );
            }

            UUID seriId = seriIds.get(currentUsageCount);
            seriUsageCount.put(laptopChiTietId, currentUsageCount + 1);

            BigDecimal thanhTien = ctRequest.getGiaBan();
            tongTienChuaGiam = tongTienChuaGiam.add(thanhTien);

            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("laptopChiTietId", laptopChiTietId);
            productInfo.put("seriId", seriId);
            productInfo.put("giaBan", ctRequest.getGiaBan());
            productList.add(productInfo);
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

            soTienGiam = phieuGiamGiaService.calculateDiscount(phieuGiamGia, tongTienChuaGiam);
        } else {
            phieuGiamGia = phieuGiamGiaService.findBestCouponForOrder(tongTienChuaGiam);
            if (phieuGiamGia != null) {
                soTienGiam = phieuGiamGiaService.calculateDiscount(phieuGiamGia, tongTienChuaGiam);
            }
        }

        BigDecimal phiVanChuyen = request.getPhiVanChuyen() != null ? request.getPhiVanChuyen() : BigDecimal.ZERO;
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
        order.setTrangThai(1);
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
        entityManager.flush();
        orderRepo.updateNgayTaoById(savedOrder.getId());
        entityManager.refresh(savedOrder);

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
            seriRepo.updateTrangThaiSeri(seriId.toString().toUpperCase(), 2);
        }

        if (phieuGiamGia != null && soTienGiam.compareTo(BigDecimal.ZERO) > 0) {
            GiamGiaHoaDon giamGiaHoaDon = new GiamGiaHoaDon();
            giamGiaHoaDon.setId(UUID.randomUUID());
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

        if (request.getListHinhThucThanhToan() != null && !request.getListHinhThucThanhToan().isEmpty()) {
            for (PaymentCustomerRequest paymentRequest : request.getListHinhThucThanhToan()) {
                HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepo.findById(paymentRequest.getIdHinhThucThanhToan())
                        .orElseThrow(() -> new ResourceNotFoundException("Hình thức thanh toán không tồn tại"));

                HinhThucThanhToanChiTiet paymentDetail = new HinhThucThanhToanChiTiet();
                paymentDetail.setId(UUID.randomUUID());
                StringBuilder httt = new StringBuilder("HTTT");
                for (int i = 0; i < 6; i++) {
                    httt.append(rand.nextInt(10));
                }
                paymentDetail.setIdHinhthucthanhtoanchitiet(httt.toString());
                paymentDetail.setIdOrder(savedOrder);
                paymentDetail.setIdHinhThucThanhToan(hinhThucThanhToan);
                paymentDetail.setSoTienThanhToan(paymentRequest.getSoTien());
                paymentDetail.setGhiChu(paymentRequest.getGhiChu());

                hinhThucThanhToanChiTietRepo.save(paymentDetail);
            }
        }

        OrderActionLog actionLog = new OrderActionLog();
        actionLog.setId(UUID.randomUUID());
        StringBuilder acl = new StringBuilder("ACL");
        for (int i = 0; i < 6; i++) {
            acl.append(rand.nextInt(10));
        }
        actionLog.setIdOrderacl(acl.toString());
        actionLog.setIdOrder(savedOrder);
        actionLog.setIdTaiKhoan(taiKhoan);
        actionLog.setHanhDong(1); // 1 = Tạo đơn
        actionLog.setMoTa("Khách hàng tạo đơn hàng mới: " + savedOrder.getMaDonHang());

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
                    String ngayDatStr = Instant.now().atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).format(formatter);

                    List<com.example.sever.service.MailService.OrderEmailProduct> emailProducts = new ArrayList<>();
                    Map<UUID, com.example.sever.service.MailService.OrderEmailProduct> productMap = new HashMap<>();
                    List<Object[]> productDataList = orderCTRepo.findProductInfoByIdOrder(savedOrder.getId());
                    for (Object[] row : productDataList) {
                        UUID idLaptopChiTiet = convertToUUID(row[2]);
                        String tenSanPham = (String) row[3];
                        BigDecimal giaBan = (BigDecimal) row[5];

                        if (productMap.containsKey(idLaptopChiTiet)) {
                            // Sản phẩm đã tồn tại (cùng idLaptopChiTiet), tăng số lượng và cộng thêm thành tiền
                            com.example.sever.service.MailService.OrderEmailProduct existingProduct = productMap.get(idLaptopChiTiet);
                            existingProduct.setSoLuong(existingProduct.getSoLuong() + 1);
                            existingProduct.setThanhTien(existingProduct.getThanhTien().add(giaBan));
                        } else {
                            // Sản phẩm chưa tồn tại, tạo mới
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

        return response;
    }



    // Helper method để tạo mã ngẫu nhiên (tái sử dụng)
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
                String numberStr = lastCode.substring(2, 5); // VD: OD007-2024 → "007"
                next = Integer.parseInt(numberStr) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }

        return String.format("OD%03d-2024", next);
    }

    @Override
    public List<OrderListCustomerResponse> getDanhSachDonHangByTaiKhoanCustomer(UUID idTaiKhoan) {
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
                    .ngayTao(order.getNgayTao() != null ? order.getNgayTao() : Instant.now())
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

        if (!orderRepo.existsById(idOrder)) {
            throw new ResourceNotFoundException("Đơn hàng không tồn tại: " + idOrder);
        }

        List<Object[]> productDataList = orderCTRepo.findProductInfoByIdOrder(idOrder);

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
        // Validate request
        if (request == null) {
            throw new IllegalArgumentException("Request không được để trống");
        }
        if (request.getMaDonHang() == null || request.getMaDonHang().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn hàng không được để trống");
        }
        if (request.getSdt() == null || request.getSdt().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        // Normalize số điện thoại: loại bỏ khoảng trắng và ký tự đặc biệt
        String normalizedSdt = request.getSdt().trim().replaceAll("[\\s-()]", "");
        String normalizedMaDonHang = request.getMaDonHang().trim();

        // Tìm đơn hàng theo mã đơn và số điện thoại khách hàng (sdt_khach_hang trong bảng Order)
        List<Order> orders = orderRepo.findByMaDonHangAndSdtKhachHang(normalizedMaDonHang, normalizedSdt);

        // Nếu không tìm thấy với số điện thoại đã normalize, thử với số điện thoại gốc
        if (orders.isEmpty() && !normalizedSdt.equals(request.getSdt().trim())) {
            orders = orderRepo.findByMaDonHangAndSdtKhachHang(normalizedMaDonHang, request.getSdt().trim());
        }

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng với mã đơn: " + request.getMaDonHang() + " và số điện thoại: " + request.getSdt());
        }

        // Convert sang DTO
        List<OrderDetailCustomerResponse> responseList = new ArrayList<>();
        for (Order order : orders) {
            // Lấy địa chỉ giao hàng
            String diaChiGiaoHang = extractDiaChiDayDuFromGhiChu(order.getGhiChu());
            if (diaChiGiaoHang == null || diaChiGiaoHang.isEmpty()) {
                diaChiGiaoHang = buildDiaChiGiaoHang(order.getIdDiaChi());
            }

            // Lấy ngày đặt từ Order entity
            Instant ngayDat = order.getNgayTao() != null ? order.getNgayTao() : Instant.now();

            // Lấy hình thức thanh toán
            List<String> hinhThucThanhToan = hinhThucThanhToanChiTietRepo
                    .findTenHinhThucThanhToanByIdOrder(order.getId());

            // Lấy danh sách sản phẩm
            List<OrderProductCustomerResponse> danhSachSanPham = orderCTRepo.findProductInfoByIdOrder(order.getId())
                    .stream()
                    .map(row -> {
                        UUID idOrderCT = convertToUUID(row[0]);
                        UUID idSeri = convertToUUID(row[1]);
                        UUID idLaptopChiTiet = convertToUUID(row[2]);
                        String tenSanPham = (String) row[3];
                        String anhSanPham = row[4] != null ? (String) row[4] : null;
                        BigDecimal giaBan = (BigDecimal) row[5];
                        // Mỗi sản phẩm = 1, không nhân số lượng
                        Integer soLuong = 1;
                        BigDecimal thanhTien = giaBan; // thanhTien = giaBan (không nhân số lượng)

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

            // Convert trạng thái
            String tenTrangThai = convertTrangThaiToTen(order.getTrangThai());

            // Build response
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

    private String convertTrangThaiToTen(Integer trangThai) {
        if (trangThai == null) {
            return "Không xác định";
        }
        return switch (trangThai) {
            case 1 -> "Chờ xác nhận";
            case 2 -> "Đã xác nhận";
            case 3 -> "Chờ vận chuyển";
            case 4 -> "Đang vận chuyển";
            case 5 -> "Đã thanh toán";
            case 6 -> "Thành công";
            case 7 -> "Hủy đơn";
            default -> "Không xác định";
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCustomerResponse huyDonHangCustomer(UUID idOrder, UUID idTaiKhoan) {
        Order order = orderRepo.findById(idOrder)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        if (!order.getIdTaiKhoan().getId().equals(idTaiKhoan)) {
            throw new IllegalArgumentException("Bạn không có quyền hủy đơn hàng này");
        }

        if (order.getTrangThai() == null) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ");
        }

        if (order.getTrangThai() == 7) {
            throw new IllegalArgumentException("Đơn hàng đã được hủy trước đó");
        }

        if (order.getTrangThai() >= 4) {
            throw new IllegalArgumentException("Không thể hủy đơn hàng đang trong quá trình vận chuyển hoặc đã hoàn thành");
        }

        order.setTrangThai(7);
        Order savedOrder = orderRepo.save(order);
        entityManager.flush();

        List<OrderCT> orderCTList = orderCTRepo.findByIdOrder(idOrder);
        for (OrderCT orderCT : orderCTList) {
            if (orderCT.getIdSeri() != null) {
                UUID seriId = orderCT.getIdSeri().getId();
                Optional<Integer> currentTrangThai = seriRepo.findTrangThaiById(seriId);

                if (currentTrangThai.isPresent() && currentTrangThai.get() == 2) {
                    seriRepo.updateTrangThaiSeri(seriId, 1);
                }
            }
        }

        TaiKhoan taiKhoan = taiKhoanRepo.findById(idTaiKhoan)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        Random rand = new Random();
        OrderActionLog actionLog = new OrderActionLog();
        actionLog.setId(UUID.randomUUID());
        StringBuilder acl = new StringBuilder("ACL");
        for (int i = 0; i < 6; i++) {
            acl.append(rand.nextInt(10));
        }
        actionLog.setIdOrderacl(acl.toString());
        actionLog.setIdOrder(savedOrder);
        actionLog.setIdTaiKhoan(taiKhoan);
        actionLog.setHanhDong(7);
        actionLog.setMoTa("Khách hàng hủy đơn hàng: " + savedOrder.getMaDonHang());

        orderActionLogRepo.save(actionLog);

        OrderCustomerResponse response = new OrderCustomerResponse();
        response.setIdOrder(savedOrder.getId());
        response.setMaDonHang(savedOrder.getMaDonHang());
        response.setTongTien(savedOrder.getTongTienThuHo());
        response.setSoTienGiam(savedOrder.getGiaTriGiamGia());
        response.setTongPhaiTra(savedOrder.getTongTienThuHo());
        response.setTrangThai(savedOrder.getTrangThai());
        response.setMessage("Hủy đơn hàng thành công");

        return response;
    }
}

