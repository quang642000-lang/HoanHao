package repository.impl;


import dto.response.*;
import repository.DBConnect;
import repository.IThongKeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class ThongKeRepoImpl implements IThongKeRepository {
    private String[] getDefaultDates(String tuNgay, String denNgay) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        if ((tuNgay == null || tuNgay.isEmpty()) && (denNgay == null || denNgay.isEmpty())) { tuNgay = today; denNgay = today; }
        if (tuNgay == null || tuNgay.isEmpty()) tuNgay = "2000-01-01";
        if (denNgay == null || denNgay.isEmpty()) denNgay = "2099-12-31";
        return new String[]{tuNgay, denNgay};
    }

    @Override
    public int getTotalDonHang(String tuNgay, String denNgay, String maNV) {
        String[] dates = getDefaultDates(tuNgay, denNgay);
        String sql = "SELECT COUNT(*) FROM DON_HANG WHERE CAST(thoi_gian_tao AS DATE) >= ? AND CAST(thoi_gian_tao AS DATE) <= ? " + ((maNV != null && !maNV.isEmpty()) ? " AND ma_nv = ?" : "");
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dates[0]); ps.setString(2, dates[1]);
            if (maNV != null && !maNV.isEmpty()) ps.setString(3, maNV);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (Exception e) { e.printStackTrace(); } return 0;
    }

    @Override
    public List<DashboardKpiDTO> getDonHangTheoNgay(String tuNgay, String denNgay, String maNV, int offset, int limit) {
        List<DashboardKpiDTO> list = new ArrayList<>();
        String[] dates = getDefaultDates(tuNgay, denNgay);
        String sql = "SELECT dh.ma_dh, dh.thoi_gian_tao, dh.tong_phai_tra, dh.trang_thai_don, nv.ho_ten FROM DON_HANG dh JOIN NHAN_VIEN nv ON dh.ma_nv = nv.ma_nv " +
                "WHERE CAST(dh.thoi_gian_tao AS DATE) >= ? AND CAST(dh.thoi_gian_tao AS DATE) <= ? " + ((maNV != null && !maNV.isEmpty()) ? " AND dh.ma_nv = ?" : "") +
                " ORDER BY dh.thoi_gian_tao DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            int p = 1; ps.setString(p++, dates[0]); ps.setString(p++, dates[1]);
            if (maNV != null && !maNV.isEmpty()) ps.setString(p++, maNV);
            ps.setInt(p++, offset); ps.setInt(p++, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DashboardKpiDTO dh = new DashboardKpiDTO();
                    dh.setMaDH(rs.getString("ma_dh")); dh.setThoiGian(rs.getTimestamp("thoi_gian_tao"));
                    dh.setTongTien(rs.getInt("tong_phai_tra")); dh.setTrangThai(rs.getString("trang_thai_don"));
                    dh.setTenNhanVien(rs.getString("ho_ten")); list.add(dh);
                }
            }
        } catch (Exception e) { e.printStackTrace(); } return list;
    }

    @Override
    public ThongKeDTO getThongKeTongQuan(String tuNgay, String denNgay, String maNV) {
        ThongKeDTO tk = new ThongKeDTO();
        String[] dates = getDefaultDates(tuNgay, denNgay);
        String sql = "WITH Params AS (SELECT CAST(? AS DATE) as StartDate, CAST(? AS DATE) as EndDate, CAST(? AS VARCHAR(20)) as MaNV) " +
                "SELECT " +
                "(SELECT ISNULL(SUM(tong_phai_tra), 0) FROM DON_HANG CROSS JOIN Params WHERE CAST(thoi_gian_tao AS DATE) BETWEEN StartDate AND EndDate AND trang_thai_don = N'Hoàn thành' AND (MaNV IS NULL OR ma_nv = MaNV)) AS doanh_thu, " +
                "(SELECT ISNULL(SUM(tong_phai_tra), 0) FROM DON_HANG CROSS JOIN Params WHERE MONTH(thoi_gian_tao) = MONTH(GETDATE()) AND YEAR(thoi_gian_tao) = YEAR(GETDATE()) AND trang_thai_don = N'Hoàn thành' AND (MaNV IS NULL OR ma_nv = MaNV)) AS doanh_thu_thang, " +
                "(SELECT COUNT(*) FROM DON_HANG CROSS JOIN Params WHERE CAST(thoi_gian_tao AS DATE) BETWEEN StartDate AND EndDate AND (MaNV IS NULL OR ma_nv = MaNV)) AS don_hang_moi, " +
                "(SELECT COUNT(*) FROM SAN_PHAM WHERE trang_thai = 1) AS tong_san_pham, " +
                "(SELECT COUNT(*) FROM KHACH_HANG) AS tong_khach_hang";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dates[0]); ps.setString(2, dates[1]);
            if (maNV == null || maNV.isEmpty()) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tk.setDoanhThuHomNay(rs.getInt("doanh_thu")); tk.setDoanhThuThangNay(rs.getInt("doanh_thu_thang"));
                    tk.setDonHangMoi(rs.getInt("don_hang_moi")); tk.setTongSanPham(rs.getInt("tong_san_pham"));
                    tk.setTongKhachHang(rs.getInt("tong_khach_hang"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); } return tk;
    }

    @Override
    public List<TopSanPhamDTO> getTopSanPham(String tuNgay, String denNgay, String maNV) {
        List<TopSanPhamDTO> list = new ArrayList<>();
        String[] dates = getDefaultDates(tuNgay, denNgay);
        String sql = "SELECT TOP 5 sp.ten_sp, SUM(ct.so_luong) as tong_so_luong FROM CHI_TIET_DON_HANG ct " +
                "JOIN BIEN_THE_SAN_PHAM bt ON ct.ma_bt = bt.ma_bt JOIN SAN_PHAM sp ON bt.ma_sp = sp.ma_sp " +
                "JOIN DON_HANG dh ON ct.ma_dh = dh.ma_dh WHERE CAST(dh.thoi_gian_tao AS DATE) BETWEEN ? AND ? " +
                "AND dh.trang_thai_don != N'Đã hủy' " + ((maNV != null && !maNV.isEmpty()) ? " AND dh.ma_nv = ? " : "") +
                "GROUP BY sp.ten_sp ORDER BY tong_so_luong DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            int p = 1; ps.setString(p++, dates[0]); ps.setString(p++, dates[1]);
            if (maNV != null && !maNV.isEmpty()) ps.setString(p++, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopSanPhamDTO top = new TopSanPhamDTO();
                    top.setTenSanPham(rs.getString("ten_sp")); top.setSoLuongBan(rs.getInt("tong_so_luong")); list.add(top);
                }
            }
        } catch (Exception e) { e.printStackTrace(); } return list;
    }

    @Override
    public Map<String, Integer> getDoanhThu7NgayQua(String tuNgay, String denNgay, String maNV) {
        Map<String, Integer> chartData = new LinkedHashMap<>();
        String[] dates = getDefaultDates(tuNgay, denNgay);
        String sql = "SELECT FORMAT(thoi_gian_tao, 'dd/MM') as ngay, SUM(tong_phai_tra) as tong_doanh_thu FROM DON_HANG " +
                "WHERE CAST(thoi_gian_tao AS DATE) BETWEEN ? AND ? AND trang_thai_don = N'Hoàn thành' " + ((maNV != null && !maNV.isEmpty()) ? " AND ma_nv = ?" : "") +
                " GROUP BY FORMAT(thoi_gian_tao, 'dd/MM'), CAST(thoi_gian_tao AS DATE) ORDER BY CAST(thoi_gian_tao AS DATE) ASC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            int p = 1; ps.setString(p++, dates[0]); ps.setString(p++, dates[1]);
            if (maNV != null && !maNV.isEmpty()) ps.setString(p++, maNV);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) chartData.put(rs.getString("ngay"), rs.getInt("tong_doanh_thu")); }
        } catch (Exception e) { e.printStackTrace(); } return chartData;
    }

    @Override
    public Map<String, Object> getReceiptData(String maDH) {
        Map<String, Object> receipt = new LinkedHashMap<>();
        String sqlDH = "SELECT dh.ma_dh, dh.thoi_gian_tao, dh.tong_tien_hang, dh.tien_giam_gia, dh.tong_phai_tra, dh.tien_khach_dua, " +
                "nv.ho_ten AS ten_nv, kh.ten_kh AS ten_kh, pt.ten_pt FROM DON_HANG dh " +
                "LEFT JOIN NHAN_VIEN nv ON dh.ma_nv = nv.ma_nv LEFT JOIN KHACH_HANG kh ON dh.ma_kh = kh.ma_kh " +
                "LEFT JOIN PHUONG_THUC_THANH_TOAN pt ON dh.ma_pt = pt.ma_pt WHERE dh.ma_dh = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sqlDH)) {
            ps.setString(1, maDH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    receipt.put("maDH", rs.getString("ma_dh")); receipt.put("nhanVien", rs.getString("ten_nv"));
                    receipt.put("thoi_gian_tho", rs.getTimestamp("thoi_gian_tao")); receipt.put("ten_kh_tho", rs.getString("ten_kh"));
                    receipt.put("ten_pttt_tho", rs.getString("ten_pt")); receipt.put("tongTienHang", rs.getInt("tong_tien_hang"));
                    receipt.put("tienGiamGia", rs.getInt("tien_giam_gia")); receipt.put("tongPhaiTra", rs.getInt("tong_phai_tra"));
                    receipt.put("tienKhachDua", rs.getInt("tien_khach_dua"));
                    List<Map<String, Object>> items = new ArrayList<>();
                    String sqlCT = "SELECT ct.ma_ctdh, ct.so_luong, ct.gia_chot_mon, ct.muc_da, ct.muc_duong, sp.ten_sp, bt.kich_co FROM CHI_TIET_DON_HANG ct " +
                            "JOIN BIEN_THE_SAN_PHAM bt ON ct.ma_bt = bt.ma_bt JOIN SAN_PHAM sp ON bt.ma_sp = sp.ma_sp WHERE ct.ma_dh = ?";
                    try (PreparedStatement psCT = con.prepareStatement(sqlCT)) {
                        psCT.setString(1, maDH);
                        try (ResultSet rsCT = psCT.executeQuery()) {
                            while (rsCT.next()) {
                                Map<String, Object> item = new LinkedHashMap<>();
                                String maCTDH = rsCT.getString("ma_ctdh");
                                item.put("tenMon", rsCT.getString("ten_sp")); item.put("size", rsCT.getString("kich_co"));
                                item.put("da", rsCT.getString("muc_da")); item.put("duong", rsCT.getString("muc_duong"));
                                item.put("soLuong", rsCT.getInt("so_luong")); item.put("giaChot", rsCT.getInt("gia_chot_mon"));
                                List<Map<String, Object>> toppings = new ArrayList<>();
                                String sqlTP = "SELECT tp.ten_tp, ctt.so_luong_tp, ctt.gia_chot_tp FROM CHI_TIET_TOPPING ctt JOIN TOPPING tp ON ctt.ma_tp = tp.ma_tp WHERE ctt.ma_ctdh = ?";
                                try (PreparedStatement psTP = con.prepareStatement(sqlTP)) {
                                    psTP.setString(1, maCTDH);
                                    try (ResultSet rsTP = psTP.executeQuery()) {
                                        while (rsTP.next()) {
                                            Map<String, Object> topping = new LinkedHashMap<>();
                                            topping.put("ten", rsTP.getString("ten_tp")); topping.put("sl", rsTP.getInt("so_luong_tp"));
                                            topping.put("gia", rsTP.getInt("gia_chot_tp")); toppings.add(topping);
                                        }
                                    }
                                }
                                item.put("toppings", toppings); items.add(item);
                            }
                        }
                    }
                    receipt.put("items", items);
                }
            }
        } catch (Exception e) { e.printStackTrace(); } return receipt;
    }
}