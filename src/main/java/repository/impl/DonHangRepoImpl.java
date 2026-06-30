package repository.impl;

import model.ChiTietDonHang;
import model.ChiTietTopping;
import model.DonHang;
import repository.DBConnect;
import repository.IDonHangRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DonHangRepoImpl implements IDonHangRepository {

    @Override
    public boolean taoDonHang(DonHang dh, int diemSuDung, int diemCongThem) throws SQLException {
        String maDHMoi = "";
        String checkKhuyenMai = "SELECT so_luong, (SELECT COUNT(*) FROM DON_HANG WHERE ma_km = ?) as so_luong_da_dung FROM CHUONG_TRINH_KHUYEN_MAI WITH (UPDLOCK, ROWLOCK) WHERE ma_km = ?";

        // FIX CHÍ MẠNG: Bổ sung thêm biến lấy trạng thái và loại đơn hàng linh động
        String trangThai = (dh.getTrangThaiDon() != null && !dh.getTrangThaiDon().isEmpty()) ? dh.getTrangThaiDon() : "Hoàn thành";
        String loaiDH = (dh.getLoaiDH() != null && !dh.getLoaiDH().isEmpty()) ? dh.getLoaiDH() : "POS";

        String sqlDH = "INSERT INTO DON_HANG (tong_tien_hang, tien_giam_gia, tong_phai_tra, diem_su_dung, tien_tru_diem, tien_khach_dua, trang_thai_don, loai_dh, thoi_gian_tt, ma_nv, ma_kh, ma_km, ma_pt) " +
                "OUTPUT INSERTED.ma_dh VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?, ?, ?, ?)";

        String sqlCT = "INSERT INTO CHI_TIET_DON_HANG (so_luong, muc_duong, muc_da, ghi_chu, gia_chot_mon, ma_dh, ma_bt) " +
                "OUTPUT INSERTED.ma_ctdh VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlTopping = "INSERT INTO CHI_TIET_TOPPING (so_luong_tp, gia_chot_tp, ma_ctdh, ma_tp) VALUES (?, ?, ?, ?)";
        String updateDiem = "UPDATE KHACH_HANG SET diem_tich_luy = diem_tich_luy - ? + ? WHERE ma_kh = ?";

        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                if (dh.getKhuyenMai() != null && dh.getKhuyenMai().getMaKM() != null && !dh.getKhuyenMai().getMaKM().isEmpty()) {
                    try (PreparedStatement psCheck = con.prepareStatement(checkKhuyenMai)) {
                        psCheck.setString(1, dh.getKhuyenMai().getMaKM());
                        psCheck.setString(2, dh.getKhuyenMai().getMaKM());
                        try (ResultSet rs = psCheck.executeQuery()) {
                            if (rs.next() && rs.getInt("so_luong_da_dung") >= rs.getInt("so_luong")) {
                                throw new SQLException("Mã khuyến mãi đã hết lượt sử dụng!");
                            }
                        }
                    }
                }

                try (PreparedStatement psDH = con.prepareStatement(sqlDH)) {
                    psDH.setInt(1, dh.getTongTienHang());
                    psDH.setInt(2, dh.getTienGiamGia());
                    psDH.setInt(3, dh.getTongPhaiTra());
                    psDH.setInt(4, diemSuDung);
                    psDH.setInt(5, dh.getTienTruDiem());
                    psDH.setInt(6, dh.getTienKhachDua());
                    psDH.setString(7, trangThai);
                    psDH.setString(8, loaiDH);

                    // FIX CHÍ MẠNG: Kiểm tra cực kỳ cẩn thận nếu NhanVien bị Null (Đơn O2O)
                    if (dh.getNhanVien() != null && dh.getNhanVien().getMaNV() != null && !dh.getNhanVien().getMaNV().isEmpty()) {
                        psDH.setString(9, dh.getNhanVien().getMaNV());
                    } else {
                        psDH.setNull(9, java.sql.Types.VARCHAR);
                    }

                    if (dh.getKhachHang() != null && dh.getKhachHang().getMaKH() != null) {
                        psDH.setString(10, dh.getKhachHang().getMaKH());
                    } else {
                        psDH.setNull(10, java.sql.Types.VARCHAR);
                    }

                    if (dh.getKhuyenMai() != null && dh.getKhuyenMai().getMaKM() != null && !dh.getKhuyenMai().getMaKM().isEmpty()) {
                        psDH.setString(11, dh.getKhuyenMai().getMaKM());
                    } else {
                        psDH.setNull(11, java.sql.Types.VARCHAR);
                    }

                    psDH.setString(12, dh.getPhuongThucThanhToan().getMaPTTT());

                    try (ResultSet rsDH = psDH.executeQuery()) {
                        if (rsDH.next()) {
                            maDHMoi = rsDH.getString("ma_dh");
                            dh.setMaDH(maDHMoi);
                        }
                    }
                }

                if (maDHMoi == null || maDHMoi.isEmpty()) throw new SQLException("Lỗi sinh mã hóa đơn!");

                try (PreparedStatement psCT = con.prepareStatement(sqlCT);
                     PreparedStatement psTopping = con.prepareStatement(sqlTopping)) {
                    for (ChiTietDonHang ct : dh.getDanhSachChiTiet()) {
                        psCT.setInt(1, ct.getSoLuong());
                        psCT.setString(2, ct.getMucDuong());
                        psCT.setString(3, ct.getMucDa());
                        psCT.setString(4, ct.getGhiChu());
                        psCT.setInt(5, ct.getGiaChotMon());
                        psCT.setString(6, maDHMoi);
                        psCT.setString(7, ct.getBienThe().getMaBienThe());

                        String maCTMoi = "";
                        try (ResultSet rsCT = psCT.executeQuery()) {
                            if (rsCT.next()) maCTMoi = rsCT.getString("ma_ctdh");
                        }
                        for (ChiTietTopping ctt : ct.getDanhSachTopping()) {
                            psTopping.setInt(1, ctt.getSoLuongTp());
                            psTopping.setInt(2, ctt.getGiaChotTp());
                            psTopping.setString(3, maCTMoi);
                            psTopping.setString(4, ctt.getTopping().getMaTopping());
                            psTopping.executeUpdate();
                        }
                    }
                }

                if (dh.getKhachHang() != null && dh.getKhachHang().getMaKH() != null) {
                    try (PreparedStatement psDiem = con.prepareStatement(updateDiem)) {
                        psDiem.setInt(1, diemSuDung);
                        psDiem.setInt(2, diemCongThem);
                        psDiem.setString(3, dh.getKhachHang().getMaKH());
                        psDiem.executeUpdate();
                    }
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}