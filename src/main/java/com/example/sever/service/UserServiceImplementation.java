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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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
            throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã tồn tại");
        }
        if (taiKhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã tồn tại");
        }

        Role role = roleRepository.findByIdRole("R003")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy quyền"));

        String idTaiKhoan = generateUniqueUserId();

        TaiKhoan user = userMapper.toTaiKhoan(request);
        user.setId(UUID.randomUUID());
        user.setIdTaiKhoan(idTaiKhoan);
        user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        user.setTrangThai(1);
        user.setIdRole(role);

        // Upload ảnh (nếu có)
        user.setAnh(uploadImage(request.getAnh()));

        user = taiKhoanRepository.save(user);
        saveUserAddress(user, request);

        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }
    @Override
    @Transactional
    public UserDetailResponse createAdmin(UserCreationRequest request) {

        TaiKhoan me = requireMe();
        if (!isOwner(me)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Chỉ tài khoản chủ (AD000) được tạo ADMIN");
        }
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã tồn tại");
        }
        if (taiKhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã tồn tại");
        }

        // ✅ ADMIN role (đổi R001 nếu hệ bạn khác)
        Role role = roleRepository.findByIdRole("R001")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy quyền ADMIN"));

        // ID mã admin (tuỳ bạn đặt ADxxx hoặc TKxxx)
        String idTaiKhoan = generateUniqueAdminId(); // mình tạo method bên dưới
        String rawPassword = generateRandomPassword();

        TaiKhoan user = userMapper.toTaiKhoan(request);
        user.setId(UUID.randomUUID());
        user.setIdTaiKhoan(idTaiKhoan);
        user.setMatKhau(passwordEncoder.encode(rawPassword));
        user.setTrangThai(1);
        user.setIdRole(role);

        // Upload ảnh (nếu có)
        user.setAnh(uploadImage(request.getAnh()));

        user = taiKhoanRepository.save(user);

        // nếu bạn muốn admin có địa chỉ mặc định (optional)
        if (hasAddressPayload(request)) {
            saveUserAddress(user, request);
        }

        sendPasswordEmail(user.getEmail(), rawPassword, user.getTen());

        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    @Transactional
    public UserDetailResponse updateAdmin(String id, UserCreationRequest request) {
        TaiKhoan user = taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));
        TaiKhoan me = requireMe();
        if (!isOwner(me)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Chỉ tài khoản chủ (AD000) được cập nhật ADMIN");
        }
        guardNotOwner(user);
        // ✅ chống trùng email/phone nhưng không đánh nhầm chính nó
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && taiKhoanRepository.existsByEmail(newEmail)) {
                throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã tồn tại");
            }
        }
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            String newPhone = request.getSoDienThoai().trim();
            if (!newPhone.equalsIgnoreCase(user.getSoDienThoai()) && taiKhoanRepository.existsBySoDienThoai(newPhone)) {
                throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã tồn tại");
            }
        }

        // ✅ ép role ADMIN (đổi R001 nếu hệ bạn khác)
        Role role = roleRepository.findByIdRole("R001")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy quyền ADMIN"));

        // Update field
        user.setTen(request.getTen());
        user.setEmail(request.getEmail());
        user.setSoDienThoai(request.getSoDienThoai());
        user.setNgaySinh(request.getNgaySinh());
        user.setGioiTinh(request.getGioiTinh());

        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }
        user.setIdRole(role);

        // ✅ FIX ẢNH giống bạn đang làm:
        if (request.getAnh() != null) {
            if (!request.getAnh().isEmpty()) {
                user.setAnh(uploadImage(request.getAnh()));
            } else {
                user.setAnh(null);
            }
        }

        user = taiKhoanRepository.save(user);

        if (hasAddressPayload(request)) {
            saveUserAddress(user, request);
        }

        log.info("Cập nhật admin với ID: {}", id);
        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    @Transactional
    public UserDetailResponse updateUserByAdmin(String id, UserCreationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin) {
            TaiKhoan me = null;

            Object principal = auth != null ? auth.getPrincipal() : null;
            if (principal instanceof TaiKhoan tk) {
                me = tk;
            } else {
                // fallback: auth.getName() thường là username/email
                String username = auth != null ? auth.getName() : null;
                if (username != null && !username.isBlank()) {
                    me = taiKhoanRepository.findByEmail(username).orElse(null);
                }
            }

            if (me == null) {
                throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng hiện tại");
            }

            if (!me.getId().toString().equals(id)) {
                throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền cập nhật tài khoản này");
            }
        }
        TaiKhoan user = taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        // chống trùng email/phone nhưng không tính chính nó
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && taiKhoanRepository.existsByEmail(newEmail)) {
                throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã tồn tại");
            }
            user.setEmail(newEmail);
        }

        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            String newPhone = request.getSoDienThoai().trim();
            if (!newPhone.equalsIgnoreCase(user.getSoDienThoai()) && taiKhoanRepository.existsBySoDienThoai(newPhone)) {
                throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã tồn tại");
            }
            user.setSoDienThoai(newPhone);
        }

        if (request.getTen() != null && !request.getTen().trim().isEmpty()) user.setTen(request.getTen().trim());
        if (request.getNgaySinh() != null) user.setNgaySinh(request.getNgaySinh());
        if (request.getGioiTinh() != null && !request.getGioiTinh().trim().isEmpty()) user.setGioiTinh(request.getGioiTinh().trim());

        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }

        // Ảnh: giữ logic như updateEmployee
        if (request.getAnh() != null) {
            if (!request.getAnh().isEmpty()) user.setAnh(uploadImage(request.getAnh()));
            else user.setAnh(null);
        }

        // QUAN TRỌNG: không đụng role ở đây (admin update profile/user chung)
        // user.setIdRole(user.getIdRole());

        user = taiKhoanRepository.save(user);

        if (hasAddressPayload(request)) {
            saveUserAddress(user, request);
        }

        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    @Transactional
    public UserDetailResponse createEmployee(UserCreationRequest request) {
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã tồn tại");
        }
        if (taiKhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã tồn tại");
        }

        Role role = roleRepository.findByIdRole("R002")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy quyền"));

        String idTaiKhoan = generateUniqueEmployeeId();
        String rawPassword = generateRandomPassword();

        TaiKhoan user = userMapper.toTaiKhoan(request);
        user.setId(UUID.randomUUID());
        user.setIdTaiKhoan(idTaiKhoan);
        user.setMatKhau(passwordEncoder.encode(rawPassword));
        user.setTrangThai(1);
        user.setIdRole(role);

        // Upload ảnh (nếu có)
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
            throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã tồn tại");
        }
        if (taiKhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã tồn tại");
        }

        Role role = roleRepository.findByIdRole("R003")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy quyền"));

        String idTaiKhoan = generateUniqueCustomerId();
        String rawPassword = generateRandomPassword();

        TaiKhoan user = userMapper.toTaiKhoan(request);
        user.setId(UUID.randomUUID());
        user.setIdTaiKhoan(idTaiKhoan);
        user.setMatKhau(passwordEncoder.encode(rawPassword));
        user.setTrangThai(1);
        user.setIdRole(role);

        // Upload ảnh (nếu có)
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
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        // ✅ chống trùng email/phone nhưng không đánh nhầm chính nó
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && taiKhoanRepository.existsByEmail(newEmail)) {
                throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã tồn tại");
            }
        }
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            String newPhone = request.getSoDienThoai().trim();
            if (!newPhone.equalsIgnoreCase(user.getSoDienThoai()) && taiKhoanRepository.existsBySoDienThoai(newPhone)) {
                throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã tồn tại");
            }
        }

        Role role = roleRepository.findByIdRole("R002")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy quyền"));

        user.setTen(request.getTen());
        user.setEmail(request.getEmail());
        user.setSoDienThoai(request.getSoDienThoai());
        user.setNgaySinh(request.getNgaySinh());
        user.setGioiTinh(request.getGioiTinh());
        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }
        user.setIdRole(role);

        // ✅ FIX ẢNH:
        // - request.getAnh() == null  => giữ nguyên ảnh cũ
        // - request.getAnh() != null & !isEmpty => upload ảnh mới
        // - request.getAnh() != null & isEmpty => user muốn xoá ảnh => set null
        if (request.getAnh() != null) {
            if (!request.getAnh().isEmpty()) {
                user.setAnh(uploadImage(request.getAnh()));
            } else {
                user.setAnh(null);
            }
        }

        user = taiKhoanRepository.save(user);
        if (hasAddressPayload(request)) {
            saveUserAddress(user, request);
        }
        log.info("Cập nhật nhân viên với ID: {}", id);
        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    @Transactional
    public UserDetailResponse updateCustomer(String id, UserCreationRequest request) {
        TaiKhoan user = taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        // ✅ chống trùng email/phone nhưng không đánh nhầm chính nó
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && taiKhoanRepository.existsByEmail(newEmail)) {
                throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã tồn tại");
            }
        }
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            String newPhone = request.getSoDienThoai().trim();
            if (!newPhone.equalsIgnoreCase(user.getSoDienThoai()) && taiKhoanRepository.existsBySoDienThoai(newPhone)) {
                throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã tồn tại");
            }
        }

        Role role = roleRepository.findByIdRole("R003")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy quyền"));

        user.setTen(request.getTen());
        user.setEmail(request.getEmail());
        user.setSoDienThoai(request.getSoDienThoai());
        user.setNgaySinh(request.getNgaySinh());
        user.setGioiTinh(request.getGioiTinh());
        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }
        user.setIdRole(role);

        // ✅ FIX ẢNH như trên
        if (request.getAnh() != null) {
            if (!request.getAnh().isEmpty()) {
                user.setAnh(uploadImage(request.getAnh()));
            } else {
                user.setAnh(null);
            }
        }

        user = taiKhoanRepository.save(user);
        if (hasAddressPayload(request)) {
            saveUserAddress(user, request);
        }

        log.info("Cập nhật khách hàng với ID: {}", id);
        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    private boolean isOwner(TaiKhoan u) {
        return u != null && u.getIdTaiKhoan() != null
                && "AD000".equalsIgnoreCase(u.getIdTaiKhoan().trim());
    }

    private TaiKhoan requireMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn chưa đăng nhập");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof TaiKhoan tk) return tk;

        String username = auth.getName();
        return taiKhoanRepository.findByEmail(username)
                .or(() -> taiKhoanRepository.findBySoDienThoai(username))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng hiện tại"));
    }

    private void guardNotOwner(TaiKhoan target) {
        if (isOwner(target)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Không thể thao tác tài khoản chủ (AD000)");
        }
    }

    @Override
    public TaiKhoan findUserById(String id) {
        return taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));
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
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));
        return userMapper.toUserDetailResponse(user, diaChiRepository);
    }

    @Override
    public List<UserDetailResponse> getUsersByRole(String roleId) {
        Role role = roleRepository.findByIdRole(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy quyền"));
        List<TaiKhoan> users = taiKhoanRepository.findByIdRole(role);
        return users.stream()
                .map(user -> userMapper.toUserDetailResponse(user, diaChiRepository))
                .collect(Collectors.toList());
    }
    private boolean hasAddressPayload(UserCreationRequest r) {
        if (r == null) return false;

        // text fields
        if (r.getTinhThanh() != null && !r.getTinhThanh().trim().isEmpty()) return true;
        if (r.getQuanHuyen() != null && !r.getQuanHuyen().trim().isEmpty()) return true;
        if (r.getPhuongXa() != null && !r.getPhuongXa().trim().isEmpty()) return true;
        if (r.getDiaChiChiTiet() != null && !r.getDiaChiChiTiet().trim().isEmpty()) return true;

        // GHN fields
        if (r.getProvinceId() != null) return true;
        if (r.getDistrictId() != null) return true;
        if (r.getWardCode() != null && !r.getWardCode().trim().isEmpty()) return true;

        return false;
    }

    @Override
    @Transactional
    public UserDetailResponse toggleUserStatus(String id) {


        TaiKhoan me = requireMe();
        TaiKhoan user = taiKhoanRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        guardNotOwner(user);

        // Optional: admin thường không được khóa/mở admin khác
        boolean targetIsAdmin = user.getIdRole() != null && "R001".equals(user.getIdRole().getIdRole());
        if (targetIsAdmin && !isOwner(me)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Chỉ tài khoản chủ (AD000) được thay đổi trạng thái ADMIN");
        }

        user.setTrangThai(user.getTrangThai() == 1 ? 0 : 1);
        user = taiKhoanRepository.save(user);
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
        UUID userId = user.getId();
        List<DiaChi> addresses = diaChiRepository.findAllByTaiKhoanId(userId);

        DiaChi addressToSave;
        boolean isCreate = (addresses == null || addresses.isEmpty());

        if (isCreate) {
            // Chưa có địa chỉ -> tạo mới và auto default = true
            addressToSave = new DiaChi();
            addressToSave.setId(UUID.randomUUID());

            Integer maxNum = diaChiRepository.findMaxDiaChiNumberWithLock(); // LOCK
            int nextCode = (maxNum == null ? 1 : maxNum + 1);
            addressToSave.setIdDiaChi(String.format("DC%04d", nextCode));

            // đảm bảo chỉ 1 default
            try { diaChiRepository.clearDefault(userId); } catch (Exception ignored) {}
            addressToSave.setMacDinh(true);

        } else {
            // Đã có địa chỉ -> chỉ update (ghi đè) lên default, nếu không có default thì fix data bẩn
            addressToSave = addresses.stream()
                    .filter(addr -> Boolean.TRUE.equals(addr.getMacDinh()))
                    .findFirst()
                    .orElse(null);

            if (addressToSave == null) {
                // data bẩn: không có default -> chọn 1 cái và set default
                addressToSave = addresses.get(0);
                try { diaChiRepository.clearDefault(userId); } catch (Exception ignored) {}
                addressToSave.setMacDinh(true);
            }
            // Nếu có nhiều default (data bẩn) thì sẽ fix ở ensureExactlyOneDefault() cuối hàm
        }

        // gán owner
        addressToSave.setIdTaiKhoan(user);
        if (addressToSave.getQuocGia() == null || addressToSave.getQuocGia().isBlank()) {
            addressToSave.setQuocGia("Việt Nam");
        }

        // ✅ chỉ set khi request có dữ liệu (tránh ghi đè null/"")
        setIfNotBlank(request.getTinhThanh(), addressToSave::setTinhThanh);
        setIfNotBlank(request.getQuanHuyen(), addressToSave::setQuanHuyen);
        setIfNotBlank(request.getPhuongXa(), addressToSave::setPhuongXa);
        setIfNotBlank(request.getDiaChiChiTiet(), addressToSave::setDiaChiChiTiet);

        // GHN
        if (request.getProvinceId() != null) addressToSave.setProvinceId(request.getProvinceId());
        if (request.getDistrictId() != null) addressToSave.setDistrictId(request.getDistrictId());
        setIfNotBlank(request.getWardCode(), addressToSave::setWardCode);

        // Nếu bạn muốn hoTen/sdt của address = user info thì để, không thì bỏ 2 dòng này
        setIfNotBlank(request.getTen(), addressToSave::setHoTen);
        setIfNotBlank(request.getSoDienThoai(), addressToSave::setSoDienThoai);

        diaChiRepository.save(addressToSave);

        // ✅ đảm bảo invariant: nếu có >=1 địa chỉ => đúng 1 default
        ensureExactlyOneDefault(userId);
    }

    private void setIfNotBlank(String value, java.util.function.Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) setter.accept(value.trim());
    }


    private String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String contentType = file.getContentType();
            if (contentType == null) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Không xác định được loại file ảnh");
            }

            boolean ok = contentType.equalsIgnoreCase("image/jpeg")
                    || contentType.equalsIgnoreCase("image/jpg")
                    || contentType.equalsIgnoreCase("image/png");

            if (!ok) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Chỉ chấp nhận ảnh JPG/PNG");
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Kích thước ảnh không được vượt quá 5MB");
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl != null) return secureUrl.toString();

            Object url = uploadResult.get("url");
            return (url != null ? url.toString() : null);

        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary: {}", e.getMessage());
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Lỗi upload ảnh: " + e.getMessage());
        }
    }

    private void ensureExactlyOneDefault(UUID userId) {
        List<DiaChi> list = diaChiRepository.findAllByTaiKhoanId(userId);
        if (list == null || list.isEmpty()) return;

        List<DiaChi> defaults = list.stream()
                .filter(d -> Boolean.TRUE.equals(d.getMacDinh()))
                .toList();

        // 0 default -> set cái đầu tiên làm default
        if (defaults.isEmpty()) {
            DiaChi pick = list.get(0);
            diaChiRepository.clearDefault(userId);
            pick.setMacDinh(true);
            diaChiRepository.save(pick);
            return;
        }

        // nhiều default -> giữ 1 cái, clear phần còn lại
        if (defaults.size() > 1) {
            DiaChi keep = defaults.get(0);
            diaChiRepository.clearDefault(userId);
            keep.setMacDinh(true);
            diaChiRepository.save(keep);
        }
    }


    @Override
    public UserDetailResponse getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
                throw new AppException(ErrorCode.USER_NOT_FOUND, "Bạn chưa đăng nhập");
            }

            Object principal = auth.getPrincipal();

            TaiKhoan user = null;

            // principal là TaiKhoan
            if (principal instanceof TaiKhoan tk) {
                user = tk;
            }

            // principal là String (email/username/phone)
            if (user == null && principal instanceof String s) {
                String username = s.trim();
                if (!username.isEmpty() && !"anonymousUser".equalsIgnoreCase(username)) {
                    // dùng phương thức sẵn có trong repo bạn đã có ở dự án: findByEmail/existsByEmail
                    // nếu không có findByEmail thì bạn thay bằng method phù hợp repo
                    try {
                        user = taiKhoanRepository.findByEmail(username).orElse(null);
                    } catch (Exception ignored) {
                    }

                    if (user == null) {
                        try {
                            // nếu repo bạn có findBySoDienThoai Optional
                            user = taiKhoanRepository.findBySoDienThoai(username).orElse(null);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            if (user == null) {
                throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng hiện tại");
            }

            log.info("Current user anh: {}", user.getAnh());
            return userMapper.toUserDetailResponse(user, diaChiRepository);

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng hiện tại");
        }
    }
    private String generateUniqueAdminId() {
        Integer maxCode = taiKhoanRepository.findMaxAdminCode();
        int nextCode = (maxCode != null ? maxCode : 0) + 1;
        return "AD" + String.format("%03d", nextCode);
    }

    @Override
    @Transactional
    public void changePassword(TaiKhoan user, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getMatKhau())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu hiện tại không đúng!");
        }
        user.setMatKhau(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(user);
        log.info("Password changed successfully for user: {}", user.getSoDienThoai());
    }
}
