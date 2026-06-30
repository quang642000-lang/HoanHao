package repository;

import model.BienTheSanPham;
import java.util.List;

public interface IBienTheSanPhamRepository {
    List<BienTheSanPham> getAll();
    List<BienTheSanPham> getAll(int offset, int limit);
    int getTotalCount();
    BienTheSanPham findById(String maBT);
    boolean add(BienTheSanPham bt);
    boolean update(BienTheSanPham bt);
    boolean delete(String maBT);
    List<BienTheSanPham> search(String keyword, String maSP);
    boolean updateTrangThai(String maBT, int trangThai);
}