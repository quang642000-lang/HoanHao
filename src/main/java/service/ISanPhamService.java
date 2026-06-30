package service;

import model.SanPham;
import java.util.List;

public interface ISanPhamService {
    List<SanPham> getAll();
    List<SanPham> getAllByPage(int page);
    int getTotalPages();
    String add(SanPham sp);
    String update(SanPham sp);
    String updateTrangThai(String maSP, int trangThai);
    String delete(String maSP);
    List<SanPham> search(String keyword, String maDanhMuc);
}