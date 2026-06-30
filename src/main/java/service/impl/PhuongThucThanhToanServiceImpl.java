package service.impl;

import model.PhuongThucThanhToan;
import repository.IPhuongThucThanhToanRepository;
import repository.impl.PhuongThucThanhToanRepoImpl;
import service.IPhuongThucThanhToanService;
import java.util.List;

public class PhuongThucThanhToanServiceImpl implements IPhuongThucThanhToanService {
    private IPhuongThucThanhToanRepository repo = new PhuongThucThanhToanRepoImpl();
    private final int LIMIT = 5;

    @Override public List<PhuongThucThanhToan> getAll() { return repo.getAll(); }
    @Override public List<PhuongThucThanhToan> getAllByPage(int page) { return repo.getAll((page - 1) * LIMIT, LIMIT); }
    @Override public int getTotalPages() { return (int) Math.ceil((double) repo.getTotalCount() / LIMIT); }

    @Override public String add(PhuongThucThanhToan pt) {
        if (pt.getTenPhuongThuc() == null || pt.getTenPhuongThuc().trim().isEmpty()) return "Lỗi: Tên không được trống!";
        pt.setTrangThai(1); return repo.add(pt) ? "Thêm phương thức thành công!" : "Lỗi hệ thống!";
    }
    @Override public String update(PhuongThucThanhToan pt) {
        if (pt.getMaPTTT() == null || pt.getTenPhuongThuc() == null) return "Dữ liệu không hợp lệ!";
        return repo.update(pt) ? "Cập nhật thành công!" : "Cập nhật thất bại!";
    }
    @Override public String updateTrangThai(String maPT, int trangThai) { return repo.updateTrangThai(maPT, trangThai) ? "Cập nhật thành công!" : "Lỗi hệ thống!"; }
    @Override public String delete(String maPT) { return repo.delete(maPT) ? "Đã xóa!" : "Không thể xóa do đã phát sinh giao dịch!"; }
    @Override public List<PhuongThucThanhToan> search(String keyword) { return (keyword == null || keyword.isEmpty()) ? repo.getAll() : repo.search(keyword); }
}