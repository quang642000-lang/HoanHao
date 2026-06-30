package repository.impl;

import model.PhuongThucThanhToan;
import repository.DBConnect;
import repository.IPhuongThucThanhToanRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PhuongThucThanhToanRepoImpl implements IPhuongThucThanhToanRepository {
    @Override
    public List<PhuongThucThanhToan> getAll() {
        List<PhuongThucThanhToan> list = new ArrayList<>();
        String sql = "SELECT * FROM PHUONG_THUC_THANH_TOAN";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhuongThucThanhToan pt = new PhuongThucThanhToan();
                pt.setMaPTTT(rs.getString("ma_pt"));
                pt.setTenPhuongThuc(rs.getString("ten_pt"));
                pt.setTrangThai(rs.getInt("trang_thai"));
                list.add(pt);
            }
        } catch (Exception e) { e.printStackTrace(); } return list;
    }

    @Override
    public List<PhuongThucThanhToan> getAll(int offset, int limit) {
        List<PhuongThucThanhToan> list = new ArrayList<>();
        String sql = "SELECT * FROM PHUONG_THUC_THANH_TOAN ORDER BY ma_pt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset); ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhuongThucThanhToan pt = new PhuongThucThanhToan();
                    pt.setMaPTTT(rs.getString("ma_pt")); pt.setTenPhuongThuc(rs.getString("ten_pt")); pt.setTrangThai(rs.getInt("trang_thai"));
                    list.add(pt);
                }
            }
        } catch (Exception e) { e.printStackTrace(); } return list;
    }

    @Override public int getTotalCount() {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM PHUONG_THUC_THANH_TOAN"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); } return 0;
    }

    @Override public boolean add(PhuongThucThanhToan pt) {
        String sql = "INSERT INTO PHUONG_THUC_THANH_TOAN (ten_pt, trang_thai) VALUES (?, ?)";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pt.getTenPhuongThuc()); ps.setInt(2, pt.getTrangThai()); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override public boolean update(PhuongThucThanhToan pt) {
        String sql = "UPDATE PHUONG_THUC_THANH_TOAN SET ten_pt = ? WHERE ma_pt = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pt.getTenPhuongThuc()); ps.setString(2, pt.getMaPTTT()); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override public boolean updateTrangThai(String maPT, int trangThai) {
        String sql = "UPDATE PHUONG_THUC_THANH_TOAN SET trang_thai = ? WHERE ma_pt = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trangThai); ps.setString(2, maPT); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override public boolean delete(String maPT) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM PHUONG_THUC_THANH_TOAN WHERE ma_pt = ?")) {
            ps.setString(1, maPT); return ps.executeUpdate() > 0;
        } catch (Exception e) { System.err.println("Lỗi Xóa: Đã có trong Hóa Đơn!"); } return false;
    }

    @Override public List<PhuongThucThanhToan> search(String keyword) {
        List<PhuongThucThanhToan> list = new ArrayList<>();
        String sql = "SELECT * FROM PHUONG_THUC_THANH_TOAN WHERE ten_pt LIKE ? OR ma_pt LIKE ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String searchPattern = "%" + keyword.trim() + "%";
            ps.setString(1, searchPattern); ps.setString(2, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhuongThucThanhToan pt = new PhuongThucThanhToan();
                    pt.setMaPTTT(rs.getString("ma_pt")); pt.setTenPhuongThuc(rs.getString("ten_pt")); pt.setTrangThai(rs.getInt("trang_thai"));
                    list.add(pt);
                }
            }
        } catch (Exception e) { e.printStackTrace(); } return list;
    }
}
