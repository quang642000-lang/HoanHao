package repository.impl;


import model.DanhMuc;
import model.SanPham;
import repository.DBConnect;
import repository.ISanPhamRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SanPhamRepoImpl implements ISanPhamRepository {

    @Override
    public List<SanPham> getAll() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT sp.ma_sp, sp.ten_sp, sp.trang_thai, sp.hinh_anh, sp.ma_dm, dm.ten_dm " +
                "FROM SAN_PHAM sp LEFT JOIN DANH_MUC dm ON sp.ma_dm = dm.ma_dm";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sp"));
                sp.setTenSanPham(rs.getString("ten_sp"));
                sp.setHinhAnh(rs.getString("hinh_anh"));
                sp.setTrangThai(rs.getInt("trang_thai"));

                DanhMuc dm = new DanhMuc();
                dm.setMaDanhMuc(rs.getString("ma_dm"));
                dm.setTenDanhMuc(rs.getString("ten_dm"));
                sp.setDanhMuc(dm);
                list.add(sp);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<SanPham> getAll(int offset, int limit) {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT sp.ma_sp, sp.ten_sp, sp.trang_thai, sp.hinh_anh, sp.ma_dm, dm.ten_dm " +
                "FROM SAN_PHAM sp LEFT JOIN DANH_MUC dm ON sp.ma_dm = dm.ma_dm " +
                "ORDER BY sp.ma_sp DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaSP(rs.getString("ma_sp"));
                    sp.setTenSanPham(rs.getString("ten_sp"));
                    sp.setHinhAnh(rs.getString("hinh_anh"));
                    sp.setTrangThai(rs.getInt("trang_thai"));

                    DanhMuc dm = new DanhMuc();
                    dm.setMaDanhMuc(rs.getString("ma_dm"));
                    dm.setTenDanhMuc(rs.getString("ten_dm"));
                    sp.setDanhMuc(dm);
                    list.add(sp);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM SAN_PHAM";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public boolean add(SanPham sp) {
        String sql = "INSERT INTO SAN_PHAM (ten_sp, trang_thai, hinh_anh, ma_dm) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setInt(2, sp.getTrangThai());
            ps.setString(3, sp.getHinhAnh());
            ps.setString(4, sp.getDanhMuc().getMaDanhMuc());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean update(SanPham sp) {
        String sql = "UPDATE SAN_PHAM SET ten_sp = ?, hinh_anh = ?, ma_dm = ? WHERE ma_sp = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setString(2, sp.getHinhAnh());
            ps.setString(3, sp.getDanhMuc().getMaDanhMuc());
            ps.setString(4, sp.getMaSP());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean updateTrangThai(String maSP, int trangThaiMoi) {
        String sql = "UPDATE SAN_PHAM SET trang_thai = ? WHERE ma_sp = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trangThaiMoi);
            ps.setString(2, maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(String maSP) {
        String sql = "DELETE FROM SAN_PHAM WHERE ma_sp = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public List<SanPham> search(String keyword, String maDanhMuc) {
        List<SanPham> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sp.ma_sp, sp.ten_sp, sp.trang_thai, sp.hinh_anh, sp.ma_dm, dm.ten_dm " +
                        "FROM SAN_PHAM sp LEFT JOIN DANH_MUC dm ON sp.ma_dm = dm.ma_dm WHERE 1=1 "
        );
        if (keyword != null && !keyword.trim().isEmpty()) sql.append("AND (sp.ten_sp LIKE ? OR sp.ma_sp LIKE ?) ");
        if (maDanhMuc != null && !maDanhMuc.trim().isEmpty() && !maDanhMuc.equals("all")) sql.append("AND sp.ma_dm = ? ");

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeValue = "%" + keyword.trim() + "%";
                ps.setString(index++, likeValue);
                ps.setString(index++, likeValue);
            }
            if (maDanhMuc != null && !maDanhMuc.trim().isEmpty() && !maDanhMuc.equals("all")) {
                ps.setString(index++, maDanhMuc);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaSP(rs.getString("ma_sp"));
                    sp.setTenSanPham(rs.getString("ten_sp"));
                    sp.setHinhAnh(rs.getString("hinh_anh"));
                    sp.setTrangThai(rs.getInt("trang_thai"));

                    DanhMuc dm = new DanhMuc();
                    dm.setMaDanhMuc(rs.getString("ma_dm"));
                    dm.setTenDanhMuc(rs.getString("ten_dm"));
                    sp.setDanhMuc(dm);
                    list.add(sp);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}