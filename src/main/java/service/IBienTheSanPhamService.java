package service;

import model.BienTheSanPham;
import java.util.List;

public interface IBienTheSanPhamService {
    List<BienTheSanPham> getAll();
    List<BienTheSanPham> getAllByPage(int page);
    int getTotalPages();
    String add(BienTheSanPham bt);
    String update(BienTheSanPham bt);
    String updateTrangThai(String maBienThe, int trangThai);
    String delete(String maBienThe);
    List<BienTheSanPham> search(String keyword, String maSp);
}