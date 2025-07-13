package com.example.sever.service;

import com.cloudinary.Cloudinary;
import com.example.sever.dto.request.UserCreationRequest;
import com.example.sever.dto.response.UserDetailResponse;
import com.example.sever.entity.DiaChi;
import com.example.sever.entity.Role;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.mapper.UserMapper;
import com.example.sever.repository.DiaChiRepository;
import com.example.sever.repository.RoleRepository;
import com.example.sever.repository.TaiKhoanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplementation implements UserService {

    TaiKhoanRepository taiKhoanRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    MailService mailService;
    DiaChiRepository diaChiRepository;
    Cloudinary cloudinary; // Inject Cloudinary

    @Override
    @Transactional
    public UserDetailResponse createUser(UserCreationRequest request) {
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED, "Only JPEG and PNG images are allowed");
        }
        if (taiKhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Only JPEG and PNG images are allowed");
        }

        Role role = roleRepository.findByIdRole("R003")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Only JPEG and PNG images are allowed"));

        String idTaiKhoan = generateUniqueUserId();

        TaiKhoan user = userMapper.toTaiKhoan(request);
        user.setId(UUID.randomUUID());
        user.setIdTaiKhoan(idTaiKhoan);
        user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        user.setTrangThai(1);
        user.setIdRole(role);

        // Xử lý upload ảnh
        user.setAnh(uploadImage(request.getAnh()));

        user = taiKhoanRepository.save(user);
        saveUserAddress(user, request);

        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    @Transactional
    public UserDetailResponse createEmployee(UserCreationRequest request) {
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED, "Only JPEG and PNG images are allowed");
        }
        if (taiKhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Only JPEG and PNG images are allowed");
        }

        Role role = roleRepository.findByIdRole("R002")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Only JPEG and PNG images are allowed"));

        String idTaiKhoan = generateUniqueEmployeeId();
        String rawPassword = generateRandomPassword();

        TaiKhoan user = userMapper.toTaiKhoan(request);
        user.setId(UUID.randomUUID());
        user.setIdTaiKhoan(idTaiKhoan);
        user.setMatKhau(passwordEncoder.encode(rawPassword));
        user.setTrangThai(1);
        user.setIdRole(role);

        // Xử lý upload ảnh
        user.setAnh(uploadImage(request.getAnh()));

        user = taiKhoanRepository.save(user);
        saveUserAddress(user, request);

        sendPasswordEmail(user.getEmail(), rawPassword, user.getTen());

        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    @Transactional
    public UserDetailResponse createCustomer(UserCreationRequest request) {
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED, "Only JPEG and PNG images are allowed");
        }
        if (taiKhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Only JPEG and PNG images are allowed");
        }

        Role role = roleRepository.findByIdRole("R003")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Only JPEG and PNG images are allowed"));

        String idTaiKhoan = generateUniqueCustomerId();
        String rawPassword = generateRandomPassword();

        TaiKhoan user = userMapper.toTaiKhoan(request);
        user.setId(UUID.randomUUID());
        user.setIdTaiKhoan(idTaiKhoan);
        user.setMatKhau(passwordEncoder.encode(rawPassword));
        user.setTrangThai(1);
        user.setIdRole(role);

        // Xử lý upload ảnh
        user.setAnh(uploadImage(request.getAnh()));

        user = taiKhoanRepository.save(user);
        saveUserAddress(user, request);

        sendPasswordEmail(user.getEmail(), rawPassword, user.getTen());

        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    @Transactional
    public UserDetailResponse updateEmployee(String id, UserCreationRequest request) {
        TaiKhoan user = taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Only JPEG and PNG images are allowed"));

        Role role = roleRepository.findByIdRole("R002")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Only JPEG and PNG images are allowed"));

        user.setTen(request.getTen());
        user.setEmail(request.getEmail());
        user.setSoDienThoai(request.getSoDienThoai());
        user.setNgaySinh(request.getNgaySinh());
        user.setGioiTinh(request.getGioiTinh());
        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }
        user.setIdRole(role);

        // Xử lý upload ảnh
        if (request.getAnh() != null && !request.getAnh().isEmpty()) {
            user.setAnh(uploadImage(request.getAnh())); // ảnh mới
        } else {
            // người dùng xóa ảnh (upload field trống)
            user.setAnh(null);
        }

        user = taiKhoanRepository.save(user);
        saveUserAddress(user, request);
        log.info("Cập nhật nhân viên với ID: {}", id);
        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    @Transactional
    public UserDetailResponse updateCustomer(String id, UserCreationRequest request) {
        TaiKhoan user = taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Only JPEG and PNG images are allowed"));

        Role role = roleRepository.findByIdRole("R003")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Only JPEG and PNG images are allowed"));

        user.setTen(request.getTen());
        user.setEmail(request.getEmail());
        user.setSoDienThoai(request.getSoDienThoai());
        user.setNgaySinh(request.getNgaySinh());
        user.setGioiTinh(request.getGioiTinh());
        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }
        user.setIdRole(role);

        // Xử lý upload ảnh
        if (request.getAnh() != null && !request.getAnh().isEmpty()) {
            user.setAnh(uploadImage(request.getAnh())); // ảnh mới
        } else {
            // người dùng xóa ảnh (upload field trống)
            user.setAnh(null);
        }

        user = taiKhoanRepository.save(user);
        saveUserAddress(user, request);
        log.info("Cập nhật khách hàng với ID: {}", id);
        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    public TaiKhoan findUserById(String id) {
        return taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Only JPEG and PNG images are allowed"));
    }

    private String generateUniqueUserId() {
        Integer maxCode = taiKhoanRepository.findMaxUserCode();
        int nextCode = (maxCode != null ? maxCode : 0) + 1;
        return "TK" + String.format("%03d", nextCode);
    }

    private String generateUniqueEmployeeId() {
        Integer maxCode = taiKhoanRepository.findMaxEmployeeCode();
        int nextCode = (maxCode != null ? maxCode : 0) + 1;
        return "NV" + String.format("%03d", nextCode);
    }

    private String generateUniqueCustomerId() {
        Integer maxCode = taiKhoanRepository.findMaxCustomerCode();
        int nextCode = (maxCode != null ? maxCode : 0) + 1;
        return "KH" + String.format("%03d", nextCode);
    }

    public UserDetailResponse getUserDetail(String id) {
        TaiKhoan user = taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Only JPEG and PNG images are allowed"));
        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    public List<UserDetailResponse> getUsersByRole(String roleId) {
        Role role = roleRepository.findByIdRole(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Only JPEG and PNG images are allowed"));
        List<TaiKhoan> users = taiKhoanRepository.findByIdRole(role);
        return users.stream()
                .map(user -> userMapper.toUserDetailResponse(user, diaChiRepository))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDetailResponse toggleUserStatus(String id) {
        TaiKhoan user = taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Only JPEG and PNG images are allowed"));
        user.setTrangThai(user.getTrangThai() == 1 ? 0 : 1);
        user = taiKhoanRepository.save(user);
        log.info("Đã chuyển trạng thái tài khoản {} thành {}", id, user.getTrangThai() == 1 ? "Hoạt động" : "Không hoạt động");
        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void sendPasswordEmail(String email, String password, String userName) {
        String emailContent = String.format("Xin chào %s,\n\nTài khoản của bạn đã được tạo thành công.\n" +
                        "Tên đăng nhập: %s\nMật khẩu: %s\n\nVui lòng đăng nhập và đổi mật khẩu để đảm bảo an toàn.\n" +
                        "Lưu ý: Mật khẩu này chỉ được sử dụng một lần và sẽ hết hạn sau 24 giờ.",
                userName, email, password);
        mailService.sendResetPasswordEmail(email, emailContent);
        log.info("Email mật khẩu đã gửi tới: {}", email);
    }

    private void saveUserAddress(TaiKhoan user, UserCreationRequest request) {
        DiaChi address = diaChiRepository.findByIdTaiKhoan(user).orElse(new DiaChi());

        if (address.getId() == null) {
            address.setId(UUID.randomUUID());
            Integer maxCode = diaChiRepository.findMaxDiaChiCode();
            int nextCode = (maxCode != null ? maxCode : 0) + 1;
            address.setIdDiaChi("DC" + String.format("%03d", nextCode));
        }

        address.setIdTaiKhoan(user);
        address.setQuocGia(request.getQuocGia());
        address.setTinhThanh(request.getTinhThanh());
        address.setQuanHuyen(request.getQuanHuyen());
        address.setPhuongXa(request.getPhuongXa());
        address.setDiaChiChiTiet(request.getDiaChiChiTiet());

        diaChiRepository.save(address);
    }

    private String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null; // Hoặc trả về URL mặc định nếu cần
        }
        try {
            // Validate file type (optional)
            String contentType = file.getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Only JPEG and PNG images are allowed");
            }

            // Validate file size (optional, ví dụ: tối đa 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Image size must not exceed 5MB");
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary: {}", e.getMessage());
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, e.getMessage());
        }
    }

    @Override
    public UserDetailResponse getCurrentUser() {
        try {
            TaiKhoan user = (TaiKhoan) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Current user anh: {}", user.getAnh()); // Thêm log
            return userMapper.toUserDetailResponse(user, diaChiRepository);
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Only JPEG and PNG images are allowed");
        }
    }
}