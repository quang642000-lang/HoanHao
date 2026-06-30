package service.impl;

import model.BienTheSanPham;
import repository.DBConnect;
import repository.IBienTheSanPhamRepository;
import repository.impl.BienTheSanPhamRepoImpl;
import service.IBienTheSanPhamService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class BienTheSanPhamServiceImpl implements IBienTheSanPhamService {
    private IBienTheSanPhamRepository bienTheRepo = new BienTheSanPhamRepoImpl();
    private final int LIMIT = 5;

    @Override
    public List<BienTheSanPham> getAll() { return bienTheRepo.getAll(); }

    @Override
    public List<BienTheSanPham> getAllByPage(int page) {
        int offset = (page - 1) * LIMIT;
        return bienTheRepo.getAll(offset, LIMIT);
    }

    @Override
    public int getTotalPages() {
        int totalRecords = bienTheRepo.getTotalCount();
        return (int) Math.ceil((double) totalRecords / LIMIT);
    }

    @Override
    public String add(BienTheSanPham bt) {
        if (bt.getKichCo() == null || bt.getKichCo().trim().isEmpty()) return "Lỗi: Kích cỡ không được để trống!";
        if (bt.getGiaBan() < 0) return "Lỗi: Giá bán không được là số âm!";
        if (bt.getSanPham() == null || bt.getSanPham().getMaSP() == null || bt.getSanPham().getMaSP().isEmpty()) {
            return "Lỗi: Vui lòng chọn sản phẩm!";
        }
        bt.setTrangThai(1);
        return bienTheRepo.add(bt) ? "Thêm kích cỡ thành công!" : "Lỗi hệ thống khi thêm!";
    }

    @Override
    public String update(BienTheSanPham bt) {
        if (bt.getMaBienThe() == null || bt.getMaBienThe().isEmpty()) return "Lỗi: Mã không hợp lệ!";
        if (bt.getKichCo() == null || bt.getKichCo().trim().isEmpty()) return "Lỗi: Kích cỡ không được trống!";
        return bienTheRepo.update(bt) ? "Cập nhật thành công!" : "Lỗi hệ thống khi cập nhật!";
    }

    @Override
    public String updateTrangThai(String maBienThe, int trangThai) {
        if (maBienThe == null || maBienThe.isEmpty()) return "Lỗi: Mã không hợp lệ!";

        // Kiểm tra logic ràng buộc: Không cho bật Kích cỡ nếu Sản phẩm mẹ đang bị tắt
        if (trangThai == 1) {
            String sql = "SELECT sp.trang_thai FROM SAN_PHAM sp INNER JOIN BIEN_THE_SAN_PHAM bt ON sp.ma_sp = bt.ma_sp WHERE bt.ma_bt = ?";
            try (Connection con = DBConnect.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maBienThe);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int spTrangThai = rs.getInt("trang_thai");
                        if (spTrangThai == 0) {
                            return "Không thể bật! Sản phẩm gốc đang ngừng bán. Vui lòng sang trang Sản Phẩm để bật hoạt động trước.";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Lỗi hệ thống khi kiểm tra trạng thái sản phẩm gốc!";
            }
        }
        return bienTheRepo.updateTrangThai(maBienThe, trangThai) ? "Cập nhật trạng thái thành công!" : "Lỗi khi cập nhật trạng thái!";
    }

    @Override
    public String delete(String maBienThe) {
        if (maBienThe == null || maBienThe.isEmpty()) return "Lỗi: Mã không hợp lệ!";
        return bienTheRepo.delete(maBienThe) ? "Đã xóa kích cỡ!" : "Lỗi: Không thể xóa vì kích cỡ này đã phát sinh trong đơn hàng!";
    }

    @Override
    public List<BienTheSanPham> search(String keyword, String maSp) {
        return bienTheRepo.search(keyword, maSp);
    }
}
