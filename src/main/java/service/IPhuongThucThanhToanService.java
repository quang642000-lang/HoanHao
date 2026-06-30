package service;

import model.PhuongThucThanhToan;
import java.util.List;
public interface IPhuongThucThanhToanService {
    List<PhuongThucThanhToan> getAll(); List<PhuongThucThanhToan> getAllByPage(int page); int getTotalPages();
    String add(PhuongThucThanhToan pt); String update(PhuongThucThanhToan pt); String updateTrangThai(String maPT, int trangThai);
    String delete(String maPT); List<PhuongThucThanhToan> search(String keyword);
}