package service.impl;

import model.DanhMuc;
import repository.IDanhMucRepository;
import repository.impl.DanhMucRepoImpl;
import service.IDanhMucService;
import java.util.List;

public class DanhMucServiceImpl implements IDanhMucService {
    private IDanhMucRepository repo = new DanhMucRepoImpl();
    private final int LIMIT = 5;

    @Override public List<DanhMuc> getAll() { return repo.getAll(); }
    @Override public List<DanhMuc> getAllByPage(int page) { return repo.getAll((page - 1) * LIMIT, LIMIT); }
    @Override public int getTotalPages() { return (int) Math.ceil((double) repo.getTotalCount() / LIMIT); }

    @Override public String add(DanhMuc dm) {
        if (dm.getTenDanhMuc() == null || dm.getTenDanhMuc().trim().isEmpty()) return "Tên không được để trống!";
        return repo.add(dm) ? "Thêm danh mục thành công!" : "Lỗi hệ thống!";
    }
    @Override public String update(DanhMuc dm) {
        if (dm.getTenDanhMuc() == null || dm.getMaDanhMuc() == null) return "Dữ liệu không hợp lệ!";
        return repo.update(dm) ? "Cập nhật thành công!" : "Cập nhật thất bại!";
    }
    @Override public String delete(String maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc.isEmpty()) return "Mã danh mục không hợp lệ!";
        return repo.delete(maDanhMuc) ? "Đã xóa danh mục!" : "Không thể xóa! Danh mục đang chứa sản phẩm.";
    }
}
