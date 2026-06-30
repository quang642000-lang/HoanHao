package service.impl;

import model.KhuyenMai;
import repository.IKhuyenMaiRepository;
import repository.impl.KhuyenMaiRepoImpl;
import service.IKhuyenMaiService;

import java.util.List;

public class KhuyenMaiServiceImpl implements IKhuyenMaiService {
    private IKhuyenMaiRepository repo = new KhuyenMaiRepoImpl();
    private final int LIMIT = 5;

    @Override
    public List<KhuyenMai> getAll() { return repo.getAll(); }

    @Override
    public List<KhuyenMai> getAllByPage(int page) {
        int offset = (page - 1) * LIMIT;
        return repo.getAll(offset, LIMIT);
    }

    @Override
    public int getTotalPages() {
        int totalRecords = repo.getTotalCount();
        return (int) Math.ceil((double) totalRecords / LIMIT);
    }

    @Override
    public KhuyenMai getById(String maKM) { return repo.getById(maKM); }

    @Override
    public String add(KhuyenMai km) {
        boolean isDuplicate = repo.getAll().stream().anyMatch(v -> v.getMaCode().equalsIgnoreCase(km.getMaCode()));
        if (isDuplicate) return "Lỗi: Mã Code này đã tồn tại trong hệ thống. Vui lòng đặt mã khác!";
        if ("Phần Trăm".equals(km.getLoaiGiamGia()) && km.getGiaTriGiam() > 100) return "Lỗi: Mức giảm phần trăm không được vượt quá 100%!";
        return repo.add(km) ? "Thêm chương trình khuyến mãi thành công!" : "Thêm thất bại!";
    }

    @Override
    public String update(KhuyenMai km) {
        if ("Phần Trăm".equals(km.getLoaiGiamGia()) && km.getGiaTriGiam() > 100) return "Lỗi: Mức giảm phần trăm không được vượt quá 100%!";

        KhuyenMai kmCu = repo.getById(km.getMaKM());
        if (kmCu != null && km.getSoLuong() < kmCu.getSoLuongDaDung()) {
            return "Lỗi: Số lượng tổng không được nhỏ hơn số lượng mã đã phát hành (" + kmCu.getSoLuongDaDung() + ")!";
        }
        return repo.update(km) ? "Cập nhật chương trình khuyến mãi thành công!" : "Cập nhật thất bại!";
    }

    @Override
    public String delete(String maKM) {
        return repo.delete(maKM) ? "Xóa chương trình khuyến mãi thành công!" : "Xóa thất bại! Đã phát sinh giao dịch";
    }

    @Override
    public List<KhuyenMai> search(String keyword) { return repo.search(keyword); }

    @Override
    public String updateTrangThai(String maKM, int trangThai) {
        return repo.updateTrangThai(maKM, trangThai) ? "Cập nhật trạng thái thành công!" : "Lỗi: Không thể cập nhật trạng thái!";
    }
}