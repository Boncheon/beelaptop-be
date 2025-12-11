package com.example.sever.service.impl;

import com.example.sever.dto.VnPayDTO.VnPayCallbackResponseCustomer;
import com.example.sever.dto.VnPayDTO.VnPayPaymentRequestCustomer;
import com.example.sever.dto.VnPayDTO.VnPayPaymentResponseCustomer;
import com.example.sever.entity.Order;
import com.example.sever.entity.OrderCT;
import com.example.sever.exception.ResourceNotFoundException;
import com.example.sever.repository.HinhThucThanhToanChiTietRepository;
import com.example.sever.repository.LaptopChiTietRepository;
import com.example.sever.repository.OrderCTRepository;
import com.example.sever.repository.OrderRepository;
import com.example.sever.repository.SeriRepository;
import com.example.sever.service.MailService;
import com.example.sever.service.VnPayServiceCustomer;
import com.example.sever.utils.VnPayConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayServiceCustomerImpl implements VnPayServiceCustomer {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderCTRepository orderCTRepository;

    @Autowired
    private LaptopChiTietRepository laptopChiTietRepository;

    @Autowired
    private SeriRepository seriRepository;

    @Autowired
    private HinhThucThanhToanChiTietRepository hinhThucThanhToanChiTietRepository;

    @Autowired
    private MailService mailService;

    @Override
    public VnPayPaymentResponseCustomer createPaymentUrl(VnPayPaymentRequestCustomer request) {
        try {
            Order order = orderRepository.findById(request.getIdOrder())
                    .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

            String vnp_TxnRef = "VNP" + order.getId().toString().replace("-", "");

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", VnPayConstant.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", request.getAmount().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", request.getOrderInfo() != null ? request.getOrderInfo() : "Thanh toan don hang " + order.getMaDonHang());
            vnp_Params.put("vnp_OrderType", request.getOrderType() != null ? request.getOrderType() : "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VnPayConstant.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");

            TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
            Calendar cld = Calendar.getInstance(tz);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            formatter.setTimeZone(tz);

            vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

            Calendar expire = (Calendar) cld.clone();
            expire.add(Calendar.MINUTE, 15);
            vnp_Params.put("vnp_ExpireDate", formatter.format(expire.getTime()));

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = vnp_Params.get(fieldName);
                
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    String encodedFieldName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8);
                    String encodedFieldValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
                    
                    hashData.append(encodedFieldName);
                    hashData.append("=");
                    hashData.append(encodedFieldValue);
                    
                    query.append(encodedFieldName);
                    query.append("=");
                    query.append(encodedFieldValue);

                    if (i < fieldNames.size() - 1) {
                        query.append("&");
                        hashData.append("&");
                    }
                }
            }

            String vnp_SecureHash = hmacSHA512(VnPayConstant.vnp_HashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            String paymentUrl = VnPayConstant.vnp_Url + "?" + query;

            VnPayPaymentResponseCustomer res = new VnPayPaymentResponseCustomer();
            res.setPaymentUrl(paymentUrl);
            res.setVnpTxnRef(vnp_TxnRef);
            res.setMessage("Tạo URL thanh toán (không xác thực chữ ký)");

            return res;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL thanh toán VNPay (no-signature): " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VnPayCallbackResponseCustomer handlePaymentCallback(Map<String, String> params) {
        VnPayCallbackResponseCustomer response = new VnPayCallbackResponseCustomer();

        try {
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TransactionStatus = params.get("vnp_TransactionStatus");
            String vnp_Amount = params.get("vnp_Amount");
            String vnp_BankCode = params.get("vnp_BankCode");
            String vnp_TransactionNo = params.get("vnp_TransactionNo");

            response.setVnpTxnRef(vnp_TxnRef);
            response.setVnpResponseCode(vnp_ResponseCode);
            response.setVnpTransactionStatus(vnp_TransactionStatus);
            response.setVnpAmount(vnp_Amount);
            response.setVnpBankCode(vnp_BankCode);
            response.setVnpTransactionNo(vnp_TransactionNo);

            if (!"00".equals(vnp_ResponseCode)) {
                response.setSuccess(false);
                response.setMessage("Thanh toán thất bại. Mã lỗi: " + vnp_ResponseCode);
                return response;
            }


            String orderIdStr = vnp_TxnRef.substring(3);
            String uuidStr = orderIdStr.substring(0, 8) + "-" +
                    orderIdStr.substring(8, 12) + "-" +
                    orderIdStr.substring(12, 16) + "-" +
                    orderIdStr.substring(16, 20) + "-" +
                    orderIdStr.substring(20);
            UUID orderId = UUID.fromString(uuidStr);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

            if (order.getIdTaiKhoan() != null) {
                order.getIdTaiKhoan().getEmail();
            }
            if (order.getIdDiaChi() != null) {
                order.getIdDiaChi().getDiaChiChiTiet();
            }

            order.setTrangThai(2);
            orderRepository.save(order);

            deductProductQuantity(order.getId());

            sendOrderConfirmationEmail(order);

            response.setSuccess(true);
            response.setMessage("Thanh toán thành công");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Lỗi khi xử lý callback: " + e.getMessage());
        }

        return response;
    }



    @Transactional(rollbackFor = Exception.class)
    private void deductProductQuantity(UUID orderId) {
        List<OrderCT> orderCTList = orderCTRepository.findByIdOrder(orderId);

        for (OrderCT orderCT : orderCTList) {
            if (orderCT.getIdSeri() == null) {
                continue;
            }

            UUID seriId = orderCT.getIdSeri().getId();
            
            seriRepository.updateTrangThaiSeri(seriId, 2);
            
        }
    }


    private void sendOrderConfirmationEmail(Order order) {
        try {
            if (order.getIdTaiKhoan() == null || order.getIdTaiKhoan().getEmail() == null
                    || order.getIdTaiKhoan().getEmail().trim().isEmpty()) {
                return;
            }

            String email = order.getIdTaiKhoan().getEmail().trim();

            String diaChiGiaoHangStr = extractDiaChiDayDuFromGhiChu(order.getGhiChu());
            if (diaChiGiaoHangStr == null || diaChiGiaoHangStr.trim().isEmpty()) {
                diaChiGiaoHangStr = "";
            } else {
                diaChiGiaoHangStr = diaChiGiaoHangStr.trim();
            }

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String ngayDatStr = java.time.Instant.now()
                    .atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh"))
                    .format(formatter);

            List<MailService.OrderEmailProduct> emailProducts = new ArrayList<>();
            List<Object[]> productDataList = orderCTRepository.findProductInfoByIdOrder(order.getId());
            for (Object[] row : productDataList) {
                String tenSanPham = (String) row[3];
                BigDecimal giaBan = (BigDecimal) row[5];
                // Mỗi sản phẩm = 1, không nhân số lượng
                Integer soLuong = 1;
                // thanhTien = giaBan (không nhân số lượng vì mỗi sản phẩm = 1)
                BigDecimal thanhTien = giaBan;

                MailService.OrderEmailProduct emailProduct = new MailService.OrderEmailProduct();
                emailProduct.setTenSanPham(tenSanPham);
                emailProduct.setSoLuong(soLuong);
                emailProduct.setGiaBan(giaBan);
                emailProduct.setThanhTien(thanhTien);
                emailProducts.add(emailProduct);
            }

            List<String> hinhThucThanhToanList = hinhThucThanhToanChiTietRepository
                    .findTenHinhThucThanhToanByIdOrder(order.getId());

            BigDecimal tongTienChuaGiam = BigDecimal.ZERO;
            for (MailService.OrderEmailProduct product : emailProducts) {
                tongTienChuaGiam = tongTienChuaGiam.add(product.getThanhTien());
            }

            BigDecimal phiVanChuyen = order.getPhiVanChuyen() != null ? order.getPhiVanChuyen() : BigDecimal.ZERO;
            BigDecimal tongThanhToan = order.getTongTienThuHo() != null ? order.getTongTienThuHo() : tongTienChuaGiam.add(phiVanChuyen);
            BigDecimal khuyenMai = tongTienChuaGiam.add(phiVanChuyen).subtract(tongThanhToan);
            if (khuyenMai.compareTo(BigDecimal.ZERO) < 0) {
                khuyenMai = BigDecimal.ZERO;
            }

            MailService.OrderEmailData emailData = new MailService.OrderEmailData();
            emailData.setMaDonHang(order.getMaDonHang());
            emailData.setTenKhachHang(order.getTenKhachHang());
            emailData.setSdtKhachHang(order.getSdtKhachHang());
            emailData.setDiaChiGiaoHang(diaChiGiaoHangStr);
            emailData.setNgayDat(ngayDatStr);
            emailData.setLoaiDon(order.getLoaiDon());
            emailData.setTenTrangThai(convertTrangThaiToTen(order.getTrangThai()));
            emailData.setHinhThucThanhToan(hinhThucThanhToanList);
            emailData.setDanhSachSanPham(emailProducts);
            emailData.setTongTienHang(tongTienChuaGiam);
            emailData.setKhuyenMai(khuyenMai);
            emailData.setPhiVanChuyen(phiVanChuyen);
            emailData.setTongThanhToan(tongThanhToan);
            String ghiChuOriginal = removeDiaChiDayDuFromGhiChu(order.getGhiChu());
            emailData.setGhiChu(ghiChuOriginal);

            mailService.sendOrderConfirmationEmail(email, emailData);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email xác nhận đơn hàng VNPay: " + e.getMessage());
            e.printStackTrace();
        }
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
                return before;
            }
        }
        return ghiChu;
    }

    private String buildDiaChiGiaoHang(com.example.sever.entity.DiaChi diaChi) {
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
            default -> "Không xác định";
        };
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
                throw new RuntimeException("Không thể chuyển đổi String sang UUID: " + obj, e);
            }
        }
        throw new RuntimeException("Không thể chuyển đổi Object sang UUID: " + obj.getClass().getName());
    }

    @Override
    public boolean verifySignature(Map<String, String> params, String vnp_SecureHash) {
        try {
            Map<String, String> paramsForHash = new HashMap<>(params);
            paramsForHash.remove("vnp_SecureHash");
            paramsForHash.remove("vnp_SecureHashType");

            List<String> fieldNames = new ArrayList<>(paramsForHash.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = paramsForHash.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(fieldValue); // KHÔNG encode trong hashData
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }

            String calculatedHash = hmacSHA512(VnPayConstant.vnp_HashSecret, hashData.toString());

            return calculatedHash.equals(vnp_SecureHash);
        } catch (Exception e) {
            return false;
        }
    }


    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] hash = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo chữ ký HMAC SHA512: " + e.getMessage(), e);
        }
    }
}

