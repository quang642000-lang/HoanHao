package repository.impl;


import model.KhuyenMai;
import repository.DBConnect;
import repository.IKhuyenMaiRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiRepoImpl implements IKhuyenMaiRepository {

    @Override
    public List<KhuyenMai> getAll() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT km.*, (SELECT COUNT(*) FROM DON_HANG dh WHERE dh.ma_km = km.ma_km) as so_luong_da_dung FROM CHUONG_TRINH_KHUYEN_MAI km";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai();
                km.setMaKM(rs.getString("ma_km"));
                km.setTenKM(rs.getString("ten_km"));
                km.setLoaiGiamGia(rs.getString("loai_giam"));
                km.setGiaTriGiam(rs.getInt("gia_tri_giam"));
                km.setDieuKienToiThieu(rs.getInt("don_toi_thieu"));
                km.setSoLuong(rs.getInt("so_luong"));
                km.setNgayBatDau(rs.getDate("ngay_bat_dau"));
                km.setNgayKetThuc(rs.getDate("ngay_ket_thuc"));
                km.setMaCode(rs.getString("ma_code"));
                km.setSoLuongDaDung(rs.getInt("so_luong_da_dung"));
                try { km.setTrangThai(rs.getInt("trang_thai")); } catch (Exception ignored) {}
                list.add(km);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<KhuyenMai> getAll(int offset, int limit) {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT km.*, (SELECT COUNT(*) FROM DON_HANG dh WHERE dh.ma_km = km.ma_km) as so_luong_da_dung " +
                "FROM CHUONG_TRINH_KHUYEN_MAI km ORDER BY km.ma_km DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(rs.getString("ma_km"));
                    km.setTenKM(rs.getString("ten_km"));
                    km.setLoaiGiamGia(rs.getString("loai_giam"));
                    km.setGiaTriGiam(rs.getInt("gia_tri_giam"));
                    km.setDieuKienToiThieu(rs.getInt("don_toi_thieu"));
                    km.setSoLuong(rs.getInt("so_luong"));
                    km.setNgayBatDau(rs.getDate("ngay_bat_dau"));
                    km.setNgayKetThuc(rs.getDate("ngay_ket_thuc"));
                    km.setMaCode(rs.getString("ma_code"));
                    km.setSoLuongDaDung(rs.getInt("so_luong_da_dung"));
                    try { km.setTrangThai(rs.getInt("trang_thai")); } catch (Exception ignored) {}
                    list.add(km);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM CHUONG_TRINH_KHUYEN_MAI";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public KhuyenMai getById(String maKM) {
        String sql = "SELECT km.*, (SELECT COUNT(*) FROM DON_HANG dh WHERE dh.ma_km = km.ma_km) as so_luong_da_dung FROM CHUONG_TRINH_KHUYEN_MAI km WHERE km.ma_km = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(rs.getString("ma_km"));
                    km.setTenKM(rs.getString("ten_km"));
                    km.setLoaiGiamGia(rs.getString("loai_giam"));
                    km.setGiaTriGiam(rs.getInt("gia_tri_giam"));
                    km.setDieuKienToiThieu(rs.getInt("don_toi_thieu"));
                    km.setSoLuong(rs.getInt("so_luong"));
                    km.setNgayBatDau(rs.getDate("ngay_bat_dau"));
                    km.setNgayKetThuc(rs.getDate("ngay_ket_thuc"));
                    km.setMaCode(rs.getString("ma_code"));
                    km.setSoLuongDaDung(rs.getInt("so_luong_da_dung"));
                    try { km.setTrangThai(rs.getInt("trang_thai")); } catch (Exception ignored) {}
                    return km;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean add(KhuyenMai km) {
        String sql = "INSERT INTO CHUONG_TRINH_KHUYEN_MAI (ten_km, loai_giam, gia_tri_giam, don_toi_thieu, so_luong, ngay_bat_dau, ngay_ket_thuc, ma_code, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, km.getTenKM());
            ps.setString(2, km.getLoaiGiamGia());
            ps.setInt(3, km.getGiaTriGiam());
            ps.setInt(4, km.getDieuKienToiThieu());
            ps.setInt(5, km.getSoLuong());
            ps.setDate(6, new java.sql.Date(km.getNgayBatDau().getTime()));
            ps.setDate(7, new java.sql.Date(km.getNgayKetThuc().getTime()));
            ps.setString(8, km.getMaCode());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean update(KhuyenMai km) {
        String sql = "UPDATE CHUONG_TRINH_KHUYEN_MAI SET ten_km = ?, loai_giam = ?, gia_tri_giam = ?, don_toi_thieu = ?, so_luong = ?, ngay_bat_dau = ?, ngay_ket_thuc = ?, ma_code = ? WHERE ma_km = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, km.getTenKM());
            ps.setString(2, km.getLoaiGiamGia());
            ps.setInt(3, km.getGiaTriGiam());
            ps.setInt(4, km.getDieuKienToiThieu());
            ps.setInt(5, km.getSoLuong());
            ps.setDate(6, new java.sql.Date(km.getNgayBatDau().getTime()));
            ps.setDate(7, new java.sql.Date(km.getNgayKetThuc().getTime()));
            ps.setString(8, km.getMaCode());
            ps.setString(9, km.getMaKM());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(String maKM) {
        String sql = "DELETE FROM CHUONG_TRINH_KHUYEN_MAI WHERE ma_km = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public List<KhuyenMai> search(String keyword) {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT km.*, (SELECT COUNT(*) FROM DON_HANG dh WHERE dh.ma_km = km.ma_km) as so_luong_da_dung FROM CHUONG_TRINH_KHUYEN_MAI km WHERE km.ten_km LIKE ? OR km.ma_code LIKE ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(rs.getString("ma_km"));
                    km.setTenKM(rs.getString("ten_km"));
                    km.setLoaiGiamGia(rs.getString("loai_giam"));
                    km.setGiaTriGiam(rs.getInt("gia_tri_giam"));
                    km.setDieuKienToiThieu(rs.getInt("don_toi_thieu"));
                    km.setSoLuong(rs.getInt("so_luong"));
                    km.setNgayBatDau(rs.getDate("ngay_bat_dau"));
                    km.setNgayKetThuc(rs.getDate("ngay_ket_thuc"));
                    km.setMaCode(rs.getString("ma_code"));
                    km.setSoLuongDaDung(rs.getInt("so_luong_da_dung"));
                    try { km.setTrangThai(rs.getInt("trang_thai")); } catch (Exception ignored) {}
                    list.add(km);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean updateTrangThai(String maKM, int trangThai) {
        String sql = "UPDATE CHUONG_TRINH_KHUYEN_MAI SET trang_thai = ? WHERE ma_km = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trangThai);
            ps.setString(2, maKM);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
