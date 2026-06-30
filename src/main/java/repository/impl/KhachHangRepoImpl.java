package repository.impl;

import model.KhachHang;
import repository.DBConnect;
import repository.IKhachHangRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhachHangRepoImpl implements IKhachHangRepository {

    @Override
    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KHACH_HANG";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getString("ma_kh"));
                kh.setTenKH(rs.getString("ten_kh"));
                kh.setSDT(rs.getString("so_dien_thoai"));
                kh.setDiemTichLuy(rs.getInt("diem_tich_luy"));
                list.add(kh);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<KhachHang> getAll(int offset, int limit) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KHACH_HANG ORDER BY ma_kh DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKH(rs.getString("ma_kh"));
                    kh.setTenKH(rs.getString("ten_kh"));
                    kh.setSDT(rs.getString("so_dien_thoai"));
                    kh.setDiemTichLuy(rs.getInt("diem_tich_luy"));
                    list.add(kh);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM KHACH_HANG";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public KhachHang timKiemTheoSdt(String sdt) {
        String sql = "SELECT * FROM KHACH_HANG WHERE so_dien_thoai = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKH(rs.getString("ma_kh"));
                    kh.setTenKH(rs.getString("ten_kh"));
                    kh.setSDT(rs.getString("so_dien_thoai"));
                    kh.setDiemTichLuy(rs.getInt("diem_tich_luy"));
                    return kh;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean add(KhachHang kh) {
        String sql = "INSERT INTO KHACH_HANG (ten_kh, so_dien_thoai, diem_tich_luy) VALUES (?, ?, ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getSDT());
            ps.setInt(3, kh.getDiemTichLuy());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean update(KhachHang kh) {
        String sql = "UPDATE KHACH_HANG SET ten_kh = ?, so_dien_thoai = ? WHERE ma_kh = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getSDT());
            ps.setString(3, kh.getMaKH());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(String maKH) {
        String sql = "DELETE FROM KHACH_HANG WHERE ma_kh = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean congDiemTichLuy(String maKh, int diemCongThem) {
        String sql = "UPDATE KHACH_HANG SET diem_tich_luy = diem_tich_luy + ? WHERE ma_kh = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, diemCongThem);
            ps.setString(2, maKh);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}