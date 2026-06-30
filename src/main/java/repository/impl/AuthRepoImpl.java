package repository.impl;

import model.NhanVien;
import model.VaiTro;
import repository.DBConnect;
import repository.IAuthRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthRepoImpl implements IAuthRepository {
    @Override
    public NhanVien login(String tenDangNhap, String matKhauPlain, String hashedMatKhau) {
        String sql = "SELECT nv.*, vt.ten_vt FROM NHAN_VIEN nv " +
                "LEFT JOIN VAI_TRO vt ON nv.ma_vt = vt.ma_vt " +
                "WHERE nv.ten_dang_nhap = ? AND nv.trang_thai = 1";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("mat_khau");
                    boolean isValid = false;

                    // 1. Kiểm tra nếu DB đang lưu pass đã mã hóa SHA-256
                    if (dbPassword.equals(hashedMatKhau)) {
                        isValid = true;
                    }
                    // 2. Tự động chuyển đổi nếu DB vẫn đang lưu pass dạng chữ thường
                    else if (dbPassword.equals(matKhauPlain)) {
                        isValid = true;
                        updatePassword(tenDangNhap, hashedMatKhau); // Migrate mật khẩu âm thầm
                    }

                    if (isValid) {
                        NhanVien nv = new NhanVien();
                        nv.setMaNV(rs.getString("ma_nv"));
                        nv.setTenDangNhap(rs.getString("ten_dang_nhap"));
                        nv.setMatKhau(rs.getString("mat_khau"));
                        nv.setHoTen(rs.getString("ho_ten"));
                        nv.setSDT(rs.getString("so_dien_thoai"));
                        nv.setEmail(rs.getString("email"));
                        nv.setTrangThai(rs.getInt("trang_thai"));

                        VaiTro vt = new VaiTro();
                        vt.setMaVaiTro(rs.getInt("ma_vt"));
                        vt.setTenVaiTro(rs.getString("ten_vt"));
                        nv.setVaiTro(vt);
                        return nv;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String findUsernameByEmail(String email) {
        String sql = "SELECT ten_dang_nhap FROM NHAN_VIEN WHERE email = ? AND trang_thai = 1";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("ten_dang_nhap");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean updatePassword(String tenDangNhap, String hashedMatKhauMoi) {
        String sql = "UPDATE NHAN_VIEN SET mat_khau = ? WHERE ten_dang_nhap = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hashedMatKhauMoi);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
