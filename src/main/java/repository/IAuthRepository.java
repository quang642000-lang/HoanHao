package repository;

import model.NhanVien;

public interface IAuthRepository {
    // Đăng nhập kết hợp check mật khẩu cũ/mới
    NhanVien login(String tenDangNhap, String matKhauPlain, String hashedMatKhau);

    // Phục vụ tính năng quên mật khẩu qua Email
    String findUsernameByEmail(String email);

    // Cập nhật lại mật khẩu băm SHA-256
    boolean updatePassword(String tenDangNhap, String hashedMatKhauMoi);
}