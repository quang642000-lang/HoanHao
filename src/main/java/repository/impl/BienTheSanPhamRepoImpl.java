package repository.impl;

import model.BienTheSanPham;
import model.SanPham;
import repository.DBConnect;
import repository.IBienTheSanPhamRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BienTheSanPhamRepoImpl implements IBienTheSanPhamRepository {

    @Override
    public List<BienTheSanPham> getAll() {
        List<BienTheSanPham> list = new ArrayList<>();
        String sql = "SELECT bt.*, sp.ten_sp FROM BIEN_THE_SAN_PHAM bt JOIN SAN_PHAM sp ON bt.ma_sp = sp.ma_sp";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BienTheSanPham bt = new BienTheSanPham();
                bt.setMaBienThe(rs.getString("ma_bt"));
                bt.setKichCo(rs.getString("kich_co"));
                bt.setGiaBan(rs.getInt("gia_ban"));
                bt.setTrangThai(rs.getInt("trang_thai"));

                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sp"));
                sp.setTenSanPham(rs.getString("ten_sp"));
                bt.setSanPham(sp);
                list.add(bt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<BienTheSanPham> getAll(int offset, int limit) {
        List<BienTheSanPham> list = new ArrayList<>();
        String sql = "SELECT bt.*, sp.ten_sp FROM BIEN_THE_SAN_PHAM bt JOIN SAN_PHAM sp ON bt.ma_sp = sp.ma_sp " +
                "ORDER BY bt.ma_bt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BienTheSanPham bt = new BienTheSanPham();
                    bt.setMaBienThe(rs.getString("ma_bt"));
                    bt.setKichCo(rs.getString("kich_co"));
                    bt.setGiaBan(rs.getInt("gia_ban"));
                    bt.setTrangThai(rs.getInt("trang_thai"));

                    SanPham sp = new SanPham();
                    sp.setMaSP(rs.getString("ma_sp"));
                    sp.setTenSanPham(rs.getString("ten_sp"));
                    bt.setSanPham(sp);
                    list.add(bt);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM BIEN_THE_SAN_PHAM";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public BienTheSanPham findById(String maBT) {
        String sql = "SELECT bt.*, sp.ten_sp FROM BIEN_THE_SAN_PHAM bt JOIN SAN_PHAM sp ON bt.ma_sp = sp.ma_sp WHERE bt.ma_bt = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBT);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BienTheSanPham bt = new BienTheSanPham();
                    bt.setMaBienThe(rs.getString("ma_bt"));
                    bt.setKichCo(rs.getString("kich_co"));
                    bt.setGiaBan(rs.getInt("gia_ban"));
                    bt.setTrangThai(rs.getInt("trang_thai"));

                    SanPham sp = new SanPham();
                    sp.setMaSP(rs.getString("ma_sp"));
                    sp.setTenSanPham(rs.getString("ten_sp"));
                    bt.setSanPham(sp);
                    return bt;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean add(BienTheSanPham bt) {
        String sql = "INSERT INTO BIEN_THE_SAN_PHAM (kich_co, gia_ban, trang_thai, ma_sp) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bt.getKichCo());
            ps.setInt(2, bt.getGiaBan());
            ps.setInt(3, bt.getTrangThai());
            ps.setString(4, bt.getSanPham().getMaSP());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean update(BienTheSanPham bt) {
        String sql = "UPDATE BIEN_THE_SAN_PHAM SET kich_co = ?, gia_ban = ? WHERE ma_bt = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bt.getKichCo());
            ps.setInt(2, bt.getGiaBan());
            ps.setString(3, bt.getMaBienThe());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(String maBT) {
        String sql = "DELETE FROM BIEN_THE_SAN_PHAM WHERE ma_bt = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBT);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public List<BienTheSanPham> search(String keyword, String maSP) {
        List<BienTheSanPham> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT bt.*, sp.ten_sp FROM BIEN_THE_SAN_PHAM bt JOIN SAN_PHAM sp ON bt.ma_sp = sp.ma_sp WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (bt.ma_bt LIKE ? OR bt.kich_co LIKE ? OR sp.ten_sp LIKE ?) ");
        }
        if (maSP != null && !maSP.trim().isEmpty() && !maSP.equals("all")) {
            sql.append("AND bt.ma_sp = ? ");
        }

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeValue = "%" + keyword.trim() + "%";
                ps.setString(index++, likeValue);
                ps.setString(index++, likeValue);
                ps.setString(index++, likeValue);
            }
            if (maSP != null && !maSP.trim().isEmpty() && !maSP.equals("all")) {
                ps.setString(index++, maSP);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BienTheSanPham bt = new BienTheSanPham();
                    bt.setMaBienThe(rs.getString("ma_bt"));
                    bt.setKichCo(rs.getString("kich_co"));
                    bt.setGiaBan(rs.getInt("gia_ban"));
                    bt.setTrangThai(rs.getInt("trang_thai"));

                    SanPham sp = new SanPham();
                    sp.setMaSP(rs.getString("ma_sp"));
                    sp.setTenSanPham(rs.getString("ten_sp"));
                    bt.setSanPham(sp);
                    list.add(bt);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateTrangThai(String maBT, int trangThai) {
        String sql = "UPDATE BIEN_THE_SAN_PHAM SET trang_thai = ? WHERE ma_bt = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trangThai);
            ps.setString(2, maBT);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}