package service.impl;

import model.SanPham;
import repository.DBConnect;
import repository.ISanPhamRepository;
import repository.impl.SanPhamRepoImpl;
import service.ISanPhamService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class SanPhamServiceImpl implements ISanPhamService {
    private ISanPhamRepository sanPhamRepo = new SanPhamRepoImpl();
    private final int LIMIT = 5;

    @Override
    public List<SanPham> getAll() { return sanPhamRepo.getAll(); }

    @Override
    public List<SanPham> getAllByPage(int page) {
        int offset = (page - 1) * LIMIT;
        return sanPhamRepo.getAll(offset, LIMIT);
    }

    @Override
    public int getTotalPages() {
        int totalRecords = sanPhamRepo.getTotalCount();
        return (int) Math.ceil((double) totalRecords / LIMIT);
    }

    @Override
    public String add(SanPham sp) {
        if (sp.getTenSanPham() == null || sp.getTenSanPham().trim().isEmpty()) return "Tên sản phẩm không được để trống!";
        if (sp.getDanhMuc() == null || sp.getDanhMuc().getMaDanhMuc() == null || sp.getDanhMuc().getMaDanhMuc().isEmpty()) {
            return "Vui lòng chọn danh mục cho sản phẩm!";
        }
        sp.setTrangThai(1);
        return sanPhamRepo.add(sp) ? "Thêm sản phẩm thành công!" : "Lỗi khi thêm sản phẩm!";
    }

    @Override
    public String update(SanPham sp) {
        if (sp.getMaSP() == null || sp.getMaSP().isEmpty()) return "Mã sản phẩm không hợp lệ!";
        if (sp.getTenSanPham() == null || sp.getTenSanPham().trim().isEmpty()) return "Tên sản phẩm không được trống!";
        return sanPhamRepo.update(sp) ? "Cập nhật sản phẩm thành công!" : "Lỗi khi cập nhật!";
    }

    @Override
    public String updateTrangThai(String maSP, int trangThai) {
        if (maSP == null || maSP.isEmpty()) return "Mã sản phẩm không hợp lệ!";

        // TỐI ƯU CASCADING: Cập nhật đồng bộ Sản phẩm và Biến thể bằng Transaction
        String sqlSP = "UPDATE SAN_PHAM SET trang_thai = ? WHERE ma_sp = ?";
        String sqlBT = "UPDATE BIEN_THE_SAN_PHAM SET trang_thai = ? WHERE ma_sp = ?";
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(sqlSP);
                 PreparedStatement ps2 = con.prepareStatement(sqlBT)) {

                ps1.setInt(1, trangThai);
                ps1.setString(2, maSP);
                int spUpdated = ps1.executeUpdate();

                if (spUpdated > 0) {
                    ps2.setInt(1, trangThai);
                    ps2.setString(2, maSP);
                    ps2.executeUpdate();
                } else {
                    con.rollback();
                    return "Lỗi: Không tìm thấy sản phẩm cần cập nhật!";
                }
                con.commit();
                return "Cập nhật trạng thái thành công!";
            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi kết nối cơ sở dữ liệu!";
        }
        return "Lỗi khi cập nhật trạng thái!";
    }

    @Override
    public String delete(String maSP) {
        if (maSP == null || maSP.isEmpty()) return "Mã không hợp lệ!";
        return sanPhamRepo.delete(maSP) ? "Đã xóa sản phẩm thành công!" : "Lỗi: Sản phẩm đang tồn tại trong hóa đơn!";
    }

    @Override
    public List<SanPham> search(String keyword, String maDanhMuc) {
        return sanPhamRepo.search(keyword, maDanhMuc);
    }
}
