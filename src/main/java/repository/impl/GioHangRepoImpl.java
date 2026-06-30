package repository.impl;


import model.GioHang;
import model.ChiTietGioHang;
import model.KhachHang;
import model.BienTheSanPham;
import model.SanPham;
import repository.DBConnect;
import repository.IGioHangRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GioHangRepoImpl implements IGioHangRepository {

    @Override
    public GioHang getCartByMaKH(String maKH) {
        GioHang gh = null;
        String sqlGH = "SELECT * FROM GIO_HANG WHERE ma_kh = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sqlGH)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    gh = new GioHang();
                    gh.setMaGH(rs.getString("ma_gh"));
                    gh.setMaGDTam(rs.getString("ma_gd_tam"));
                    gh.setThoiGianUp(rs.getTimestamp("thoi_gian_up"));
                    KhachHang kh = new KhachHang(); kh.setMaKH(maKH);
                    gh.setKhachHang(kh);

                    // Lấy chi tiết giỏ hàng
                    String sqlCT = "SELECT ct.*, bt.kich_co, bt.gia_ban, sp.ten_sp, sp.hinh_anh FROM CHI_TIET_GIO_HANG ct " +
                            "JOIN BIEN_THE_SAN_PHAM bt ON ct.ma_bt = bt.ma_bt " +
                            "JOIN SAN_PHAM sp ON bt.ma_sp = sp.ma_sp WHERE ct.ma_gh = ?";
                    try (PreparedStatement psCT = con.prepareStatement(sqlCT)) {
                        psCT.setString(1, gh.getMaGH());
                        try (ResultSet rsCT = psCT.executeQuery()) {
                            while (rsCT.next()) {
                                ChiTietGioHang ct = new ChiTietGioHang();
                                ct.setMaCTGH(rsCT.getString("ma_ctgh"));
                                ct.setSoLuong(rsCT.getInt("so_luong"));
                                ct.setMucDaDuong(rsCT.getString("muc_da_duong"));
                                ct.setToppingsJson(rsCT.getString("toppings_json"));
                                ct.setChonMua(rsCT.getBoolean("is_chon_mua"));

                                BienTheSanPham bt = new BienTheSanPham();
                                bt.setMaBienThe(rsCT.getString("ma_bt"));
                                bt.setKichCo(rsCT.getString("kich_co"));
                                bt.setGiaBan(rsCT.getInt("gia_ban"));
                                SanPham sp = new SanPham();
                                sp.setTenSanPham(rsCT.getString("ten_sp"));
                                sp.setHinhAnh(rsCT.getString("hinh_anh"));
                                bt.setSanPham(sp);
                                ct.setBienThe(bt);

                                gh.getDanhSachChiTiet().add(ct);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return gh;
    }

    @Override
    public boolean upsertCartItem(String maGH, String maBT, int soLuong, String mucDaDuong, String toppingsJson) {
        // Logic gộp món bằng chuỗi Toppings JSON
        String checkSql = "SELECT ma_ctgh, so_luong FROM CHI_TIET_GIO_HANG WHERE ma_gh = ? AND ma_bt = ? AND ISNULL(muc_da_duong,'') = ? AND ISNULL(toppings_json,'') = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement psCheck = con.prepareStatement(checkSql)) {
            psCheck.setString(1, maGH); psCheck.setString(2, maBT);
            psCheck.setString(3, mucDaDuong == null ? "" : mucDaDuong);
            psCheck.setString(4, toppingsJson == null ? "" : toppingsJson);

            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    String updateSql = "UPDATE CHI_TIET_GIO_HANG SET so_luong = so_luong + ? WHERE ma_ctgh = ?";
                    try (PreparedStatement psUp = con.prepareStatement(updateSql)) {
                        psUp.setInt(1, soLuong); psUp.setString(2, rs.getString("ma_ctgh"));
                        return psUp.executeUpdate() > 0;
                    }
                } else {
                    String insertSql = "INSERT INTO CHI_TIET_GIO_HANG (ma_gh, ma_bt, so_luong, muc_da_duong, toppings_json) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement psIn = con.prepareStatement(insertSql)) {
                        psIn.setString(1, maGH); psIn.setString(2, maBT); psIn.setInt(3, soLuong);
                        psIn.setString(4, mucDaDuong); psIn.setString(5, toppingsJson);
                        return psIn.executeUpdate() > 0;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); } return false;
    }

    @Override
    public boolean clearCart(String maGH) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM CHI_TIET_GIO_HANG WHERE ma_gh = ?")) {
            ps.setString(1, maGH); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); } return false;
    }
}
