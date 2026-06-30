package service.impl;

import model.Topping;
import repository.IToppingRepository;
import repository.impl.ToppingRepoImpl;
import service.IToppingService;

import java.util.List;

public class ToppingServiceImpl implements IToppingService {
    private IToppingRepository toppingRepo = new ToppingRepoImpl();
    private final int LIMIT = 5;

    @Override
    public List<Topping> getAll() { return toppingRepo.getAll(); }

    @Override
    public List<Topping> getAllByPage(int page) {
        int offset = (page - 1) * LIMIT;
        return toppingRepo.getAll(offset, LIMIT);
    }

    @Override
    public int getTotalPages() {
        int totalRecords = toppingRepo.getTotalCount();
        return (int) Math.ceil((double) totalRecords / LIMIT);
    }

    @Override
    public String add(Topping tp) {
        if (tp.getTenTopping() == null || tp.getTenTopping().trim().isEmpty()) return "Lỗi: Tên Topping không được để trống!";
        if (tp.getGiaBan() < 0) return "Lỗi: Giá bán không được là số âm!";
        tp.setTrangThai(1);
        return toppingRepo.add(tp) ? "Thêm Topping thành công!" : "Thêm thất bại. Vui lòng kiểm tra lại!";
    }

    @Override
    public String update(Topping tp) {
        if (tp.getMaTopping() == null || tp.getMaTopping().trim().isEmpty()) return "Lỗi: Mã Topping không hợp lệ!";
        if (tp.getTenTopping() == null || tp.getTenTopping().trim().isEmpty()) return "Lỗi: Tên Topping không được để trống!";
        return toppingRepo.update(tp) ? "Cập nhật thành công!" : "Cập nhật thất bại!";
    }

    @Override
    public String updateTrangThai(String maTopping, int trangThai) {
        if (maTopping == null || maTopping.isEmpty()) return "Mã không hợp lệ!";
        return toppingRepo.updateTrangThai(maTopping, trangThai) ? "Cập nhật trạng thái thành công!" : "Lỗi!";
    }

    @Override
    public String delete(String maTopping) {
        if (maTopping == null || maTopping.isEmpty()) return "Mã không hợp lệ!";
        return toppingRepo.delete(maTopping) ? "Xóa vĩnh viễn Topping thành công!" : "Lỗi: Không thể xóa vì Topping này đã từng được đặt hàng!";
    }

    @Override
    public List<Topping> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return toppingRepo.getAll();
        return toppingRepo.search(keyword);
    }
}
