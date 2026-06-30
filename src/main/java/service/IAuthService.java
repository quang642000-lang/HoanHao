package service;

import model.NhanVien;

public interface IAuthService {
    // Ném Exception thẳng ra Controller để hiển thị thông báo lỗi/khóa tài khoản
    NhanVien login(String tenDangNhap, String matKhauPlain) throws Exception;

    String checkEmailAndGetUsername(String email);

    boolean resetPassword(String tenDangNhap, String matKhauMoi);
}