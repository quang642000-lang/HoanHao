package repository.impl;

import model.NhanVien;
import model.VaiTro;
import repository.DBConnect;
import repository.INhanVienRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanVienRepoImpl implements INhanVienRepository {
    @Override
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT nv.*, vt.ten_vt FROM NHAN_VIEN nv LEFT JOIN VAI_TRO vt ON nv.ma_vt = vt.ma_vt ORDER BY nv.ma_nv DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("ma_nv")); nv.setTenDangNhap(rs.getString("ten_dang_nhap"));
                nv.setMatKhau(rs.getString("mat_khau")); nv.setHoTen(rs.getString("ho_ten"));
                nv.setSDT(rs.getString("so_dien_thoai")); nv.setEmail(rs.getString("email"));
                nv.setTrangThai(rs.getInt("trang_thai"));
                VaiTro vt = new VaiTro(); vt.setMaVaiTro(rs.getInt("ma_vt")); vt.setTenVaiTro(rs.getString("ten_vt"));
                nv.setVaiTro(vt); list.add(nv);
            }
        } catch (Exception e) { e.printStackTrace(); } return list;
    }

    @Override
    public List<NhanVien> getAll(int offset, int limit) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT nv.*, vt.ten_vt FROM NHAN_VIEN nv LEFT JOIN VAI_TRO vt ON nv.ma_vt = vt.ma_vt ORDER BY nv.ma_nv DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset); ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("ma_nv")); nv.setTenDangNhap(rs.getString("ten_dang_nhap"));
                    nv.setHoTen(rs.getString("ho_ten")); nv.setSDT(rs.getString("so_dien_thoai"));
                    nv.setEmail(rs.getString("email")); nv.setTrangThai(rs.getInt("trang_thai"));
                    VaiTro vt = new VaiTro(); vt.setMaVaiTro(rs.getInt("ma_vt")); vt.setTenVaiTro(rs.getString("ten_vt"));
                    nv.setVaiTro(vt); list.add(nv);
                }
            }
        } catch (Exception e) { e.printStackTrace(); } return list;
    }

    @Override public int getTotalCount() {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM NHAN_VIEN"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); } return 0;
    }

    @Override public boolean add(NhanVien nv) {
        String sql = "INSERT INTO NHAN_VIEN (ten_dang_nhap, mat_khau, ho_ten, so_dien_thoai, email, trang_thai, ma_vt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getTenDangNhap()); ps.setString(2, nv.getMatKhau()); ps.setString(3, nv.getHoTen());
            ps.setString(4, nv.getSDT()); ps.setString(5, nv.getEmail()); ps.setInt(6, nv.getTrangThai()); ps.setInt(7, nv.getVaiTro().getMaVaiTro());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override public boolean update(NhanVien nv) {
        String sql = "UPDATE NHAN_VIEN SET ho_ten = ?, so_dien_thoai = ?, email = ?, ma_vt = ? WHERE ma_nv = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen()); ps.setString(2, nv.getSDT()); ps.setString(3, nv.getEmail());
            ps.setInt(4, nv.getVaiTro().getMaVaiTro()); ps.setString(5, nv.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override public boolean updateTrangThai(String maNV, int trangThaiMoi) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE NHAN_VIEN SET trang_thai = ? WHERE ma_nv = ?")) {
            ps.setInt(1, trangThaiMoi); ps.setString(2, maNV); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override public boolean delete(String maNV) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM NHAN_VIEN WHERE ma_nv = ?")) {
            ps.setString(1, maNV); return ps.executeUpdate() > 0;
        } catch (Exception e) { System.out.println("Lỗi khóa ngoại Hóa Đơn"); } return false;
    }

    @Override public boolean resetPassword(String maNV, String matKhauMoi) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE NHAN_VIEN SET mat_khau = ? WHERE ma_nv = ?")) {
            ps.setString(1, matKhauMoi); ps.setString(2, maNV); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }
}
