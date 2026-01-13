package com.example.sever.service.impl;

import com.cloudinary.Cloudinary;
import com.example.sever.dto.AccountDTO.DiaChiCreateRequest;
import com.example.sever.dto.AccountDTO.DiaChiUpdateRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerRequest;
import com.example.sever.dto.AccountDTO.UpdateProfileCustomerResponse;
import com.example.sever.dto.request.TaiKhoanAddRequestDTO;
import com.example.sever.dto.request.TaiKhoanUpdateRequestDTO;
import com.example.sever.dto.response.DiaChi.DiaChiProjection;
import com.example.sever.dto.response.DiaChi.DiaChiResponse;
import com.example.sever.dto.response.TaiKhoanDisplayReponse;
import com.example.sever.entity.DiaChi;
import com.example.sever.entity.Role;
import com.example.sever.entity.TaiKhoan;
import com.example.sever.exception.AppException;
import com.example.sever.exception.ErrorCode;
import com.example.sever.mapper.TaiKhoanMapper;
import com.example.sever.repository.DiaChiRepository;
import com.example.sever.repository.RoleRepository;
import com.example.sever.repository.TaiKhoanRepository;
import com.example.sever.service.TaiKhoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // ✅ FIX (retry mã địa chỉ)
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; // ✅ FIX (ownership)
import org.springframework.security.core.context.SecurityContextHolder; // ✅ FIX (ownership)
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaiKhoanServiceImpl implements TaiKhoanService {
    private final RoleRepository roleRepository;
    private final TaiKhoanRepository taikhoanRepository;

    private final TaiKhoanMapper taikhoanMapper;
    @Autowired
    private DiaChiRepository diaChiRepository;
    @Autowired
    private Cloudinary cloudinary;

    public TaiKhoanServiceImpl(RoleRepository roleRepository, TaiKhoanRepository taikhoanRepository,
                               TaiKhoanMapper taikhoanMapper) {
        this.roleRepository = roleRepository;
        this.taikhoanRepository = taikhoanRepository;
        this.taikhoanMapper = taikhoanMapper;
    }

    @Override
    public Page<TaiKhoanDisplayReponse> getAllTaiKhoanforDisplay(Pageable pageable) {
        Page<TaiKhoan> tkPage = taikhoanRepository.findAll(pageable);
        List<TaiKhoanDisplayReponse> romDisplayReponses = tkPage.getContent().stream()
                .map(taikhoanMapper::getAlldisplayTaiKhoan).collect(Collectors.toList());

        return new PageImpl<>(romDisplayReponses , pageable, tkPage.getTotalElements());
    }

    @Override
    public TaiKhoanDisplayReponse addTaiKhoan(TaiKhoanAddRequestDTO updatedto) {

        TaiKhoan tk = new TaiKhoan();
        tk.setId(UUID.randomUUID());
        tk.setIdTaiKhoan(updatedto.getIdTaiKhoan());
        tk.setTen(updatedto.getTen());
        tk.setTrangThai(updatedto.getTrangThai());
        tk.setAnh(updatedto.getAnh());
        tk.setGioiTinh(updatedto.getGioiTinh());
        tk.setMatKhau(updatedto.getMatKhau());
        tk.setNgaySinh(updatedto.getNgaySinh());
        tk.setSoDienThoai(updatedto.getSoDienThoai());
        // Lấy entity liên kết từ DB
        Role role = roleRepository.findById(updatedto.getRole().getId())
                .orElseThrow(() -> new RuntimeException("RAM không tồn tại"));
        // Gán vào phiên bản
        tk.setIdRole(role);

        // Lưu lại
        TaiKhoan saved = taikhoanRepository.save(tk);
        return taikhoanMapper.getAlldisplayTaiKhoan(saved);
    }

    @Override
    public TaiKhoanDisplayReponse updateTaiKhoan(TaiKhoanUpdateRequestDTO updatedto) {
        TaiKhoan tk = taikhoanRepository.findById(updatedto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        tk.setTen(updatedto.getTen());
        tk.setTrangThai(updatedto.getTrangThai());
        tk.setAnh(updatedto.getAnh());
        tk.setGioiTinh(updatedto.getGioiTinh());
        tk.setMatKhau(updatedto.getMatKhau());
        tk.setNgaySinh(updatedto.getNgaySinh());
        tk.setSoDienThoai(updatedto.getSoDienThoai());
        // Lấy entity liên kết từ DB
        Role role = roleRepository.findById(updatedto.getRole().getId())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
        // Gán vào phiên bản
        tk.setIdRole(role);

        // Lưu lại
        TaiKhoan saved = taikhoanRepository.save(tk);
        return taikhoanMapper.getAlldisplayTaiKhoan(saved);
    }

    // ========================= ✅ ADD: role helpers (ADMIN bypass) =========================
    private boolean hasRole(String role) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getAuthorities() == null) return false;
            String need = "ROLE_" + role;
            return auth.getAuthorities().stream().anyMatch(a -> need.equals(a.getAuthority()));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAdmin() {
        return hasRole("ADMIN");
    }
    // ========================= END ADD =========================

    @Override
    public List<DiaChiProjection> findAllAdress(UUID id) {
        // ✅ FIX: ADMIN được phép xem tất cả address
        if (isAdmin() || isStaff()) {
            return diaChiRepository.findAllByTaiKhoanProjection(id);
        }

        // ✅ FIX: không cho lấy address của user khác (nếu có login)
        UUID currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Bạn chưa đăng nhập");
        }
        if (!currentUserId.equals(id)) {
            throw new RuntimeException("Bạn không có quyền xem địa chỉ của tài khoản này");
        }
        return diaChiRepository.findAllByTaiKhoanProjection(id);
    }

    @Override
    @Transactional
    public DiaChiResponse createAddressCustomer(DiaChiCreateRequest request) {

        // ✅ FIX: ADMIN được phép tạo cho user bất kỳ; USER thường chỉ tạo cho chính mình
        if (!isAdmin() && !isStaff()) {
            UUID currentUserId = requireCurrentUserId();
            if (request.getIdTaiKhoan() == null) {
                request.setIdTaiKhoan(currentUserId);
            } else if (!currentUserId.equals(request.getIdTaiKhoan())) {
                throw new RuntimeException("Bạn không có quyền tạo địa chỉ cho tài khoản khác");
            }
        } else {
            // ADMIN: bắt buộc phải có idTaiKhoan
            if (request.getIdTaiKhoan() == null) {
                throw new RuntimeException("Thiếu idTaiKhoan");
            }
        }

        TaiKhoan taiKhoan = taikhoanRepository.findById(request.getIdTaiKhoan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        DiaChi diaChi = new DiaChi();
        diaChi.setId(UUID.randomUUID());
        // diaChi.setIdDiaChi(nextDiaChiCode()); // ✅ moved into retry block
        diaChi.setIdTaiKhoan(taiKhoan);

        diaChi.setQuocGia("VietNam");
        diaChi.setTinhThanh(request.getTinhThanh());
        diaChi.setQuanHuyen(request.getQuanHuyen());
        diaChi.setPhuongXa(request.getPhuongXa());
        diaChi.setDiaChiChiTiet(request.getDiaChiChiTiet());

        diaChi.setHoTen(request.getHoTen());
        diaChi.setSoDienThoai(request.getSoDienThoai());

        // ✅ 3 field GHN
        diaChi.setProvinceId(request.getProvinceId());
        diaChi.setDistrictId(request.getDistrictId());
        diaChi.setWardCode(request.getWardCode());

        boolean hasDefault = diaChiRepository.findByIdTaiKhoan_IdAndMacDinhTrue(taiKhoan.getId()).isPresent();
        diaChi.setMacDinh(!hasDefault);


        // ✅ FIX: retry mã địa chỉ nếu DB có unique constraint và bị trùng do concurrent
        DiaChi saved = null;
        for (int i = 0; i < 3; i++) {
            try {
                diaChi.setIdDiaChi(nextDiaChiCode());
                saved = diaChiRepository.save(diaChi);
                ensureExactlyOneDefault(taiKhoan.getId());
                break;
            } catch (DataIntegrityViolationException ex) {
                // retry
                if (i == 2) throw ex;
            }
        }

        return new DiaChiResponse(
                saved.getId(),
                saved.getIdDiaChi(),
                saved.getIdTaiKhoan().getId(),
                saved.getQuocGia(),
                saved.getTinhThanh(),
                saved.getQuanHuyen(),
                saved.getPhuongXa(),
                saved.getDiaChiChiTiet(),
                saved.getMacDinh(),
                saved.getHoTen(),
                saved.getSoDienThoai(),
                saved.getProvinceId(),
                saved.getDistrictId(),
                saved.getWardCode()
        );
    }

    @Override
    @Transactional
    public DiaChiResponse updateAddressCustomer(UUID id, DiaChiUpdateRequest request) {
        // ✅ FIX: ownership check (ADMIN bypass nằm trong mustOwnAddress)
        UUID currentUserId = getCurrentUserId();
        if (currentUserId == null && !isAdmin()  && !isStaff()) {
            throw new RuntimeException("Bạn chưa đăng nhập hoặc phiên đăng nhập không hợp lệ");
        }
        DiaChi diaChi = mustOwnAddress(currentUserId, id);

        diaChi.setQuocGia("VietNam");
        diaChi.setTinhThanh(request.getTinhThanh());
        diaChi.setQuanHuyen(request.getQuanHuyen());
        diaChi.setPhuongXa(request.getPhuongXa());
        diaChi.setDiaChiChiTiet(request.getDiaChiChiTiet());

        diaChi.setHoTen(request.getHoTen());
        diaChi.setSoDienThoai(request.getSoDienThoai());

        // ✅ 3 field GHN
        diaChi.setProvinceId(request.getProvinceId());
        diaChi.setDistrictId(request.getDistrictId());
        diaChi.setWardCode(request.getWardCode());

        DiaChi saved = diaChiRepository.save(diaChi);

        return new DiaChiResponse(
                saved.getId(),
                saved.getIdDiaChi(),
                saved.getIdTaiKhoan().getId(),
                saved.getQuocGia(),
                saved.getTinhThanh(),
                saved.getQuanHuyen(),
                saved.getPhuongXa(),
                saved.getDiaChiChiTiet(),
                saved.getMacDinh(),
                saved.getHoTen(),
                saved.getSoDienThoai(),
                saved.getProvinceId(),
                saved.getDistrictId(),
                saved.getWardCode()
        );
    }

    @Override
    @Transactional
    public DiaChiResponse setAddressDefaultCustomer(UUID id) {
        // ✅ FIX: ownership check (ADMIN bypass nằm trong mustOwnAddress)
        UUID currentUserId = requireCurrentUserId();
        DiaChi diaChi = mustOwnAddress(currentUserId, id);

        UUID idTaiKhoan = diaChi.getIdTaiKhoan().getId();

        diaChiRepository.clearDefault(idTaiKhoan);

        diaChi.setMacDinh(true);
        DiaChi saved = diaChiRepository.save(diaChi);

        return new DiaChiResponse(
                saved.getId(),
                saved.getIdDiaChi(),
                idTaiKhoan,
                saved.getQuocGia(),
                saved.getTinhThanh(),
                saved.getQuanHuyen(),
                saved.getPhuongXa(),
                saved.getDiaChiChiTiet(),
                true,
                saved.getHoTen(),
                saved.getSoDienThoai(),
                saved.getProvinceId(),
                saved.getDistrictId(),
                saved.getWardCode()
        );
    }

    @Override
    @Transactional
    public void deleteAddressCustomer(UUID id) {
        UUID currentUserId = requireCurrentUserId();
        DiaChi diaChi = mustOwnAddress(currentUserId, id);

        UUID taiKhoanId = diaChi.getIdTaiKhoan().getId();
        boolean wasDefault = Boolean.TRUE.equals(diaChi.getMacDinh());

        diaChiRepository.delete(diaChi);

        // ✅ Rule: nếu xoá địa chỉ default -> tự set 1 địa chỉ còn lại làm default
        if (wasDefault) {
            List<DiaChi> remain = diaChiRepository.findAllByTaiKhoanId(taiKhoanId);
            if (remain != null && !remain.isEmpty()) {
                diaChiRepository.clearDefault(taiKhoanId);
                DiaChi pick = remain.get(0);
                pick.setMacDinh(true);
                diaChiRepository.save(pick);
            }
        } else {
            // optional: fix data bẩn nếu trước đó DB có vấn đề
            ensureExactlyOneDefault(taiKhoanId);
        }
    }

    @Override
    @Transactional
    public UpdateProfileCustomerResponse updateProfileCustomer(UUID id, UpdateProfileCustomerRequest request) {
        // ✅ FIX: ADMIN được phép update user khác; USER thường chỉ update chính mình
        UUID currentUserId = getCurrentUserId();
        if (!isAdmin()) {
            if (currentUserId != null && !currentUserId.equals(id)) {
                throw new RuntimeException("Bạn không có quyền cập nhật tài khoản này");
            }
        }

        // Tìm tài khoản
        TaiKhoan taiKhoan = taikhoanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        // Kiểm tra email trùng (nếu thay đổi email)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!request.getEmail().equals(taiKhoan.getEmail())) {
                if (taikhoanRepository.existsByEmail(request.getEmail())) {
                    throw new AppException(ErrorCode.EMAIL_EXISTED, "Email đã được sử dụng");
                }
            }
        }

        // Kiểm tra số điện thoại trùng (nếu thay đổi số điện thoại)
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            if (!request.getSoDienThoai().equals(taiKhoan.getSoDienThoai())) {
                if (taikhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
                    throw new AppException(ErrorCode.SO_DIEN_THOAI_EXISTED, "Số điện thoại đã được sử dụng");
                }
            }
        }

        // ✅ FIX: chỉ update khi non-blank (tránh set "")
        if (request.getTen() != null && !request.getTen().trim().isEmpty()) {
            taiKhoan.setTen(request.getTen().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            taiKhoan.setEmail(request.getEmail().trim());
        }
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            taiKhoan.setSoDienThoai(request.getSoDienThoai().trim());
        }
        if (request.getNgaySinh() != null) {
            taiKhoan.setNgaySinh(request.getNgaySinh());
        }
        if (request.getGioiTinh() != null && !request.getGioiTinh().trim().isEmpty()) {
            taiKhoan.setGioiTinh(request.getGioiTinh().trim());
        }

        // Xử lý ảnh: ưu tiên upload file mới, nếu không có thì dùng URL, nếu không có thì giữ nguyên
        if (request.getAnh() != null && !request.getAnh().isEmpty()) {
            // Upload file mới
            String imageUrl = uploadImage(request.getAnh());
            taiKhoan.setAnh(imageUrl);
        } else if (request.getAnhUrl() != null && !request.getAnhUrl().trim().isEmpty()) {
            // Chỉ update URL (từ JSON request)
            taiKhoan.setAnh(request.getAnhUrl().trim());
        }
        // Nếu cả hai đều null, giữ nguyên ảnh cũ

        // Lưu lại (updated_at sẽ tự động cập nhật bởi @LastModifiedDate)
        TaiKhoan saved = taikhoanRepository.save(taiKhoan);

        // Tạo response
        UpdateProfileCustomerResponse response = new UpdateProfileCustomerResponse();
        response.setId(saved.getId());
        response.setIdTaiKhoan(saved.getIdTaiKhoan());
        response.setTen(saved.getTen());
        response.setEmail(saved.getEmail());
        response.setSoDienThoai(saved.getSoDienThoai());
        response.setNgaySinh(saved.getNgaySinh());
        response.setGioiTinh(saved.getGioiTinh());
        response.setAnh(saved.getAnh());
        response.setUpdatedAt(saved.getUpdatedAt());
        response.setMessage("Cập nhật thông tin tài khoản thành công");

        return response;
    }

    private String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg"))) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Chỉ chấp nhận file ảnh JPEG hoặc PNG");
            }

            // Validate file size (tối đa 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Kích thước ảnh không được vượt quá 5MB");
            }

            // Upload lên Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());

            // ✅ FIX: dùng secure_url
            String imageUrl = String.valueOf(uploadResult.get("secure_url"));

            log.info("Upload ảnh thành công: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            log.error("Lỗi khi upload ảnh lên Cloudinary: {}", e.getMessage());
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED, "Lỗi khi upload ảnh: " + e.getMessage());
        }
    }
    private boolean isStaff() {
        return hasRole("NHAN_VIEN");
    }
    private String nextDiaChiCode() {
        Integer maxNum = diaChiRepository.findMaxDiaChiNumberWithLock(); // ✅ LOCK
        int next = (maxNum == null ? 1 : maxNum + 1);
        return String.format("DC%04d", next);
    }

    private DiaChi mustOwnAddress(UUID userId, UUID diaChiId) {
        DiaChi dc = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ!"));

        // ✅ FIX: ADMIN được phép thao tác mọi địa chỉ
        if (isAdmin()) return dc;

        if (userId == null) {
            throw new RuntimeException("Bạn chưa đăng nhập");
        }

        if (dc.getIdTaiKhoan() == null || dc.getIdTaiKhoan().getId() == null
                || !dc.getIdTaiKhoan().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền thao tác địa chỉ này");
        }
        return dc;
    }

    private void ensureExactlyOneDefault(UUID taiKhoanId) {
        List<DiaChi> list = diaChiRepository.findAllByTaiKhoanId(taiKhoanId);
        if (list == null || list.isEmpty()) return;

        List<DiaChi> defaults = list.stream()
                .filter(d -> Boolean.TRUE.equals(d.getMacDinh()))
                .toList();

        // 0 default -> set cái đầu tiên
        if (defaults.isEmpty()) {
            DiaChi pick = list.get(0);
            diaChiRepository.clearDefault(taiKhoanId);
            pick.setMacDinh(true);
            diaChiRepository.save(pick);
            return;
        }

        // nhiều default -> giữ 1, clear phần còn lại
        if (defaults.size() > 1) {
            DiaChi keep = defaults.get(0);
            diaChiRepository.clearDefault(taiKhoanId);
            keep.setMacDinh(true);
            diaChiRepository.save(keep);
        }
    }

    // ========================= ✅ FIX: lấy userId từ SecurityContext =========================
    private UUID getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;

            Object principal = auth.getPrincipal();
            if (principal instanceof TaiKhoan tk) {
                return tk.getId();
            }

            // Trường hợp principal là username/email (String)
            if (principal instanceof String s) {
                String username = s.trim();
                if (!username.isEmpty() && !"anonymousUser".equalsIgnoreCase(username)) {
                    // ưu tiên email
                    TaiKhoan tk = taikhoanRepository.findByEmail(username).orElse(null);
                    if (tk != null) return tk.getId();

                    // fallback phone (nếu repo có)
                    try {
                        tk = taikhoanRepository.findBySoDienThoai(username).orElse(null);
                        if (tk != null) return tk.getId();
                    } catch (Exception ignored) {
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private UUID requireCurrentUserId() {
        UUID id = getCurrentUserId();
        if (id == null) throw new RuntimeException("Bạn chưa đăng nhập hoặc phiên đăng nhập không hợp lệ");
        return id;
    }
}
