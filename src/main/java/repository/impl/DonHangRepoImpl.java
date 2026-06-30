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

        // Dùng UPDLOCK, ROWLOCK khóa cứng dòng Mã Khuyến Mãi để tránh bị 2 nhân viên dùng cùng 1 lúc
        String checkKhuyenMai = "SELECT so_luong, (SELECT COUNT(*) FROM DON_HANG WHERE ma_km = ?) as so_luong_da_dung " +
                "FROM CHUONG_TRINH_KHUYEN_MAI WITH (UPDLOCK, ROWLOCK) WHERE ma_km = ?";

        // Lệnh OUTPUT INSERTED giúp lấy ngay mã Tự sinh (Mã Đơn và Mã Chi tiết) để chèn xuống bảng con
        String sqlDH = "INSERT INTO DON_HANG (tong_tien_hang, tien_giam_gia, tong_phai_tra, diem_su_dung, tien_tru_diem, tien_khach_dua, trang_thai_don, thoi_gian_tt, ma_nv, ma_kh, ma_km, ma_pt) " +
                "OUTPUT INSERTED.ma_dh VALUES (?, ?, ?, ?, ?, ?, N'Hoàn thành', GETDATE(), ?, ?, ?, ?)";

        String sqlCT = "INSERT INTO CHI_TIET_DON_HANG (so_luong, muc_duong, muc_da, ghi_chu, gia_chot_mon, ma_dh, ma_bt) " +
                "OUTPUT INSERTED.ma_ctdh VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlTopping = "INSERT INTO CHI_TIET_TOPPING (so_luong_tp, gia_chot_tp, ma_ctdh, ma_tp) VALUES (?, ?, ?, ?)";

        String updateDiem = "UPDATE KHACH_HANG SET diem_tich_luy = diem_tich_luy - ? + ? WHERE ma_kh = ?";

        try (Connection con = DBConnect.getConnection()) {
            // BẮT ĐẦU TRANSACTION: Cấm tự động lưu. Lỗi 1 dòng là hủy toàn bộ!
            con.setAutoCommit(false);
            try {
                // 1. CHỐNG RACE CONDITION VOUCHER
                if (dh.getKhuyenMai() != null && dh.getKhuyenMai().getMaKM() != null && !dh.getKhuyenMai().getMaKM().isEmpty()) {
                    try (PreparedStatement psCheck = con.prepareStatement(checkKhuyenMai)) {
                        psCheck.setString(1, dh.getKhuyenMai().getMaKM());
                        psCheck.setString(2, dh.getKhuyenMai().getMaKM());
                        try (ResultSet rs = psCheck.executeQuery()) {
                            if (rs.next() && rs.getInt("so_luong_da_dung") >= rs.getInt("so_luong")) {
                                throw new SQLException("Mã khuyến mãi đã bị khách khác sử dụng hết trong tích tắc!");
                            }
                        }
                    }
                }

                // 2. TẠO HÓA ĐƠN CHÍNH
                try (PreparedStatement psDH = con.prepareStatement(sqlDH)) {
                    psDH.setInt(1, dh.getTongTienHang());
                    psDH.setInt(2, dh.getTienGiamGia());
                    psDH.setInt(3, dh.getTongPhaiTra());
                    psDH.setInt(4, diemSuDung);
                    psDH.setInt(5, dh.getTienTruDiem());
                    psDH.setInt(6, dh.getTienKhachDua());
                    psDH.setString(7, dh.getNhanVien().getMaNV());

                    if (dh.getKhachHang() != null && dh.getKhachHang().getMaKH() != null) {
                        psDH.setString(8, dh.getKhachHang().getMaKH());
                    } else {
                        psDH.setNull(8, java.sql.Types.VARCHAR);
                    }

                    if (dh.getKhuyenMai() != null && dh.getKhuyenMai().getMaKM() != null && !dh.getKhuyenMai().getMaKM().isEmpty()) {
                        psDH.setString(9, dh.getKhuyenMai().getMaKM());
                    } else {
                        psDH.setNull(9, java.sql.Types.VARCHAR);
                    }

                    psDH.setString(10, dh.getPhuongThucThanhToan().getMaPTTT());
                    try (ResultSet rsDH = psDH.executeQuery()) {
                        if (rsDH.next()) {
                            maDHMoi = rsDH.getString("ma_dh");
                            dh.setMaDH(maDHMoi);
                        }
                    }
                }
                if (maDHMoi == null || maDHMoi.isEmpty()) throw new SQLException("Lỗi sinh mã hóa đơn từ Database!");

                // 3. TẠO CHI TIẾT ĐƠN VÀ CHI TIẾT TOPPING
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

                // 4. TRỪ/CỘNG ĐIỂM HỘI VIÊN
                if (dh.getKhachHang() != null && dh.getKhachHang().getMaKH() != null) {
                    try (PreparedStatement psDiem = con.prepareStatement(updateDiem)) {
                        psDiem.setInt(1, diemSuDung);
                        psDiem.setInt(2, diemCongThem);
                        psDiem.setString(3, dh.getKhachHang().getMaKH());
                        psDiem.executeUpdate();
                    }
                }

                con.commit(); // TẤT CẢ OK -> CHỐT DỮ LIỆU
                return true;

            } catch (SQLException e) {
                con.rollback(); // NẾU LỖI 1 LỆNH NHỎ -> XÓA SẠCH VÀ LÀM LẠI
                throw e;
            }
        }
    }
}