package com.example.sever.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã xác thực đặt lại mật khẩu");
        message.setText(content);
        mailSender.send(message);
    }

    public void sendOrderConfirmationEmail(String toEmail, OrderEmailData orderData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đơn hàng - " + orderData.getMaDonHang());

            // Format số tiền theo định dạng Việt Nam
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            // Tạo HTML email
            String htmlContent = buildOrderEmailHtml(orderData, currencyFormat);

            helper.setText(htmlContent, true); // true = HTML content
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage(), e);
        }
    }

    private String buildOrderEmailHtml(OrderEmailData orderData, NumberFormat currencyFormat) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='vi'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 800px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { text-align: center; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 3px solid #2196F3; }");
        html.append(".company-name { font-size: 32px; font-weight: bold; color: #2196F3; margin: 10px 0; }");
        html.append(".invoice-title { font-size: 20px; color: #333; margin: 10px 0; }");
        html.append(".section { margin: 25px 0; }");
        html.append(".section-title { font-size: 18px; font-weight: bold; color: #2196F3; margin-bottom: 15px; padding-bottom: 8px; border-bottom: 2px solid #e0e0e0; }");
        html.append(".info-row { display: flex; margin: 10px 0; padding: 8px 0; }");
        html.append(".info-label { font-weight: 600; color: #555; width: 180px; }");
        html.append(".info-value { color: #333; flex: 1; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("th { background-color: #2196F3; color: white; padding: 12px; text-align: left; font-weight: 600; }");
        html.append("td { padding: 12px; border-bottom: 1px solid #e0e0e0; }");
        html.append("tr:hover { background-color: #f9f9f9; }");
        html.append(".text-right { text-align: right; }");
        html.append(".text-center { text-align: center; }");
        html.append(".summary { background-color: #f9f9f9; padding: 20px; border-radius: 5px; margin-top: 20px; }");
        html.append(".summary-row { display: flex; justify-content: space-between; padding: 8px 0; }");
        html.append(".summary-label { font-weight: 600; color: #555; }");
        html.append(".summary-value { color: #333; }");
        html.append(".total-row { border-top: 2px solid #2196F3; margin-top: 10px; padding-top: 15px; font-size: 18px; font-weight: bold; }");
        html.append(".footer { text-align: center; margin-top: 30px; padding-top: 20px; border-top: 2px solid #e0e0e0; color: #666; }");
        html.append(".note { background-color: #fff3cd; padding: 15px; border-radius: 5px; margin-top: 20px; border-left: 4px solid #ffc107; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<div class='company-name'>BEETOP</div>");
        html.append("<div class='invoice-title'>Thông tin hóa đơn</div>");
        html.append("</div>");

        // Thông tin đơn hàng
        html.append("<div class='section'>");
        html.append("<div class='section-title'>Thông tin đơn hàng</div>");
        html.append("<div class='info-row'><span class='info-label'>Mã hóa đơn:</span><span class='info-value'>").append(escapeHtml(orderData.getMaDonHang())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Ngày tạo:</span><span class='info-value'>").append(escapeHtml(orderData.getNgayDat())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Loại đơn:</span><span class='info-value'>").append(escapeHtml(orderData.getLoaiDon())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Trạng thái:</span><span class='info-value'>").append(escapeHtml(orderData.getTenTrangThai())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Phương thức thanh toán:</span><span class='info-value'>").append(escapeHtml(String.join(", ", orderData.getHinhThucThanhToan()))).append("</span></div>");
        html.append("</div>");

        // Thông tin người mua
        html.append("<div class='section'>");
        html.append("<div class='section-title'>Thông tin người mua hàng</div>");
        html.append("<div class='info-row'><span class='info-label'>Khách hàng:</span><span class='info-value'>").append(escapeHtml(orderData.getTenKhachHang())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Số điện thoại:</span><span class='info-value'>").append(escapeHtml(orderData.getSdtKhachHang())).append("</span></div>");
        html.append("</div>");

        // Thông tin người nhận
        html.append("<div class='section'>");
        html.append("<div class='section-title'>Thông tin người nhận hàng</div>");
        html.append("<div class='info-row'><span class='info-label'>Họ tên:</span><span class='info-value'>").append(escapeHtml(orderData.getTenKhachHang())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Số điện thoại người nhận:</span><span class='info-value'>").append(escapeHtml(orderData.getSdtKhachHang())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Địa chỉ:</span><span class='info-value'>").append(escapeHtml(orderData.getDiaChiGiaoHang())).append("</span></div>");
        html.append("</div>");

        // Sản phẩm đã mua
        html.append("<div class='section'>");
        html.append("<div class='section-title'>Sản phẩm đã mua:</div>");
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th class='text-center'>STT</th>");
        html.append("<th>Tên sản phẩm</th>");
        html.append("<th class='text-center'>Số lượng</th>");
        html.append("<th class='text-right'>Đơn giá</th>");
        html.append("<th class='text-right'>Tổng tiền</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");

        int stt = 1;
        for (OrderEmailProduct product : orderData.getDanhSachSanPham()) {
            html.append("<tr>");
            html.append("<td class='text-center'>").append(stt++).append("</td>");
            html.append("<td>").append(escapeHtml(product.getTenSanPham())).append("</td>");
            html.append("<td class='text-center'>").append(product.getSoLuong()).append("</td>");
            html.append("<td class='text-right'>").append(formatCurrency(product.getGiaBan(), currencyFormat)).append("</td>");
            html.append("<td class='text-right'>").append(formatCurrency(product.getThanhTien(), currencyFormat)).append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody>");
        html.append("</table>");
        html.append("</div>");

        // Tổng thanh toán
        html.append("<div class='summary'>");
        html.append("<div class='summary-row'>");
        html.append("<span class='summary-label'>Tổng tiền:</span>");
        html.append("<span class='summary-value'>").append(formatCurrency(orderData.getTongTienHang(), currencyFormat)).append("</span>");
        html.append("</div>");
        html.append("<div class='summary-row'>");
        html.append("<span class='summary-label'>Giảm giá:</span>");
        html.append("<span class='summary-value'>").append(formatCurrency(orderData.getKhuyenMai(), currencyFormat)).append("</span>");
        html.append("</div>");
        html.append("<div class='summary-row'>");
        html.append("<span class='summary-label'>Phí giao hàng:</span>");
        html.append("<span class='summary-value'>").append(formatCurrency(orderData.getPhiVanChuyen(), currencyFormat)).append("</span>");
        html.append("</div>");
        html.append("<div class='summary-row total-row'>");
        html.append("<span class='summary-label'>Tổng tiền thanh toán:</span>");
        html.append("<span class='summary-value' style='color: #2196F3;'>").append(formatCurrency(orderData.getTongThanhToan(), currencyFormat)).append("</span>");
        html.append("</div>");
        html.append("</div>");

        // Ghi chú
        if (orderData.getGhiChu() != null && !orderData.getGhiChu().trim().isEmpty()) {
            html.append("<div class='note'>");
            html.append("<strong>Ghi chú:</strong> ").append(escapeHtml(orderData.getGhiChu()));
            html.append("</div>");
        }

        // Footer
        html.append("<div class='footer'>");
        html.append("<p>Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của <strong>BeeTop</strong>!</p>");
        html.append("<p>Chúng tôi sẽ liên hệ với bạn sớm nhất để xác nhận đơn hàng.</p>");
        html.append("<p style='margin-top: 20px;'><em>Trân trọng,<br>Đội ngũ BeeTop</em></p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String formatCurrency(BigDecimal amount, NumberFormat currencyFormat) {
        if (amount == null) return currencyFormat.format(0);
        return currencyFormat.format(amount);
    }

    // Inner class để chứa dữ liệu email đơn hàng
    public static class OrderEmailData {
        private String maDonHang;
        private String tenKhachHang;
        private String sdtKhachHang;
        private String diaChiGiaoHang;
        private String ngayDat;
        private String loaiDon;
        private String tenTrangThai;
        private List<String> hinhThucThanhToan;
        private List<OrderEmailProduct> danhSachSanPham;
        private BigDecimal tongTienHang;
        private BigDecimal khuyenMai;
        private BigDecimal phiVanChuyen;
        private BigDecimal tongThanhToan;
        private String ghiChu;

        // Getters and Setters
        public String getMaDonHang() { return maDonHang; }
        public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }

        public String getTenKhachHang() { return tenKhachHang; }
        public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

        public String getSdtKhachHang() { return sdtKhachHang; }
        public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }

        public String getDiaChiGiaoHang() { return diaChiGiaoHang; }
        public void setDiaChiGiaoHang(String diaChiGiaoHang) { this.diaChiGiaoHang = diaChiGiaoHang; }

        public String getNgayDat() { return ngayDat; }
        public void setNgayDat(String ngayDat) { this.ngayDat = ngayDat; }

        public String getLoaiDon() { return loaiDon; }
        public void setLoaiDon(String loaiDon) { this.loaiDon = loaiDon; }

        public String getTenTrangThai() { return tenTrangThai; }
        public void setTenTrangThai(String tenTrangThai) { this.tenTrangThai = tenTrangThai; }

        public List<String> getHinhThucThanhToan() { return hinhThucThanhToan; }
        public void setHinhThucThanhToan(List<String> hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }

        public List<OrderEmailProduct> getDanhSachSanPham() { return danhSachSanPham; }
        public void setDanhSachSanPham(List<OrderEmailProduct> danhSachSanPham) { this.danhSachSanPham = danhSachSanPham; }

        public BigDecimal getTongTienHang() { return tongTienHang; }
        public void setTongTienHang(BigDecimal tongTienHang) { this.tongTienHang = tongTienHang; }

        public BigDecimal getKhuyenMai() { return khuyenMai; }
        public void setKhuyenMai(BigDecimal khuyenMai) { this.khuyenMai = khuyenMai; }

        public BigDecimal getPhiVanChuyen() { return phiVanChuyen; }
        public void setPhiVanChuyen(BigDecimal phiVanChuyen) { this.phiVanChuyen = phiVanChuyen; }

        public BigDecimal getTongThanhToan() { return tongThanhToan; }
        public void setTongThanhToan(BigDecimal tongThanhToan) { this.tongThanhToan = tongThanhToan; }

        public String getGhiChu() { return ghiChu; }
        public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    }

    public static class OrderEmailProduct {
        private String tenSanPham;
        private Integer soLuong;
        private BigDecimal giaBan;
        private BigDecimal thanhTien;

        // Getters and Setters
        public String getTenSanPham() { return tenSanPham; }
        public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

        public Integer getSoLuong() { return soLuong; }
        public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

        public BigDecimal getGiaBan() { return giaBan; }
        public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }

        public BigDecimal getThanhTien() { return thanhTien; }
        public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
    }
}