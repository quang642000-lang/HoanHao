package repository;

import model.PhuongThucThanhToan;
import java.util.List;
public interface IPhuongThucThanhToanRepository {
    List<PhuongThucThanhToan> getAll(); List<PhuongThucThanhToan> getAll(int offset, int limit);
    int getTotalCount(); boolean add(PhuongThucThanhToan pt); boolean update(PhuongThucThanhToan pt);
    boolean updateTrangThai(String maPT, int trangThai); boolean delete(String maPT); List<PhuongThucThanhToan> search(String keyword);
}