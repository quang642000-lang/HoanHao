package repository.impl;

import model.DanhMuc;
import repository.DBConnect;
import repository.IDanhMucRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DanhMucRepoImpl implements IDanhMucRepository {
    @Override
    public List<DanhMuc> getAll() {
        List<DanhMuc> list = new ArrayList<>();
        String sql = "SELECT ma_dm, ten_dm, trang_thai FROM DANH_MUC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DanhMuc dm = new DanhMuc();
                dm.setMaDanhMuc(rs.getString("ma_dm"));
                dm.setTenDanhMuc(rs.getString("ten_dm"));
                dm.setTrangThai(rs.getInt("trang_thai"));
                list.add(dm);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<DanhMuc> getAll(int offset, int limit) {
        List<DanhMuc> list = new ArrayList<>();
        String sql = "SELECT ma_dm, ten_dm, trang_thai FROM DANH_MUC ORDER BY ma_dm DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset); ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DanhMuc dm = new DanhMuc();
                    dm.setMaDanhMuc(rs.getString("ma_dm"));
                    dm.setTenDanhMuc(rs.getString("ten_dm"));
                    dm.setTrangThai(rs.getInt("trang_thai"));
                    list.add(dm);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM DANH_MUC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); } return 0;
    }

    @Override
    public boolean add(DanhMuc dm) {
        String sql = "INSERT INTO DANH_MUC (ten_dm) VALUES (?)";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dm.getTenDanhMuc()); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override
    public boolean update(DanhMuc dm) {
        String sql = "UPDATE DANH_MUC SET ten_dm = ? WHERE ma_dm = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dm.getTenDanhMuc()); ps.setString(2, dm.getMaDanhMuc());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override
    public boolean delete(String maDM) {
        String sql = "DELETE FROM DANH_MUC WHERE ma_dm = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDM); return ps.executeUpdate() > 0;
        } catch (Exception e) { System.err.println("Lỗi xóa: Danh mục có chứa SP!"); } return false;
    }
}