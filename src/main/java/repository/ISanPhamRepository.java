package repository;


import model.SanPham;
import java.util.List;

public interface ISanPhamRepository {
    List<SanPham> getAll();
    List<SanPham> getAll(int offset, int limit);
    int getTotalCount();
    boolean add(SanPham sp);
    boolean update(SanPham sp);
    boolean updateTrangThai(String maSP, int trangThaiMoi);
    boolean delete(String maSP);
    List<SanPham> search(String keyword, String maDanhMuc);
}