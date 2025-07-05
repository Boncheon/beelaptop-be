package com.example.sever.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    ACCESS_DENIED(403, "Bạn không có quyền truy cập tài nguyên này.", HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR(500, "Lỗi không xác định.", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_PROCESSING_ERROR(500, "Lỗi xử lý JSON.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(401, "Chưa xác thực: Token không hợp lệ hoặc đã hết hạn.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(400, "Token không hợp lệ.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(400, "Tên đăng nhập hoặc email không tồn tại.", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(400, "Tên đăng nhập và mật khẩu không hợp lệ.", HttpStatus.BAD_REQUEST),
    INVALID_TEN(400, "Họ tên phải có ít nhất 3 ký tự.", HttpStatus.BAD_REQUEST),
    INVALID_TEN_DANG_NHAP(400, "Tên đăng nhập phải có ít nhất 3 ký tự.", HttpStatus.BAD_REQUEST),
    INVALID_MAT_KHAU(400, "Mật khẩu phải có ít nhất 8 ký tự.", HttpStatus.BAD_REQUEST),
    INVALID_SO_DIEN_THOAI(400, "Số điện thoại phải có ít nhất 10 ký tự.", HttpStatus.BAD_REQUEST),
    INVALID_NGAY_SINH(400, "Ngày sinh không hợp lệ.", HttpStatus.BAD_REQUEST),
    INVALID_GIOI_TINH(400, "Giới tính không hợp lệ.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID(400, "Refresh token không hợp lệ.", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_EXPIRED(400, "Mã xác thực đã hết hạn.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(404, "Không tìm thấy tài khoản.", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(404, "Không tìm thấy vai trò.", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(409, "Email đã được sử dụng.", HttpStatus.CONFLICT),
    TEN_DANG_NHAP_EXISTED(409, "Tên đăng nhập đã được sử dụng.", HttpStatus.CONFLICT),
    TEN_EXISTED(409, "Họ tên đã được sử dụng.", HttpStatus.CONFLICT),
    SO_DIEN_THOAI_EXISTED(409, "Số điện thoại đã được sử dụng.", HttpStatus.CONFLICT),
    INVALID_GOOGLE_TOKEN(400, "Token Google không hợp lệ.", HttpStatus.BAD_REQUEST);
    final int code;
    final String message;
    final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}