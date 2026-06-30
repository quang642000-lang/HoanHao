package service;

import model.KhuyenMai;
import java.util.List;

public interface IKhuyenMaiService {
    List<KhuyenMai> getAll();
    List<KhuyenMai> getAllByPage(int page);
    int getTotalPages();
    KhuyenMai getById(String maKM);
    String add(KhuyenMai km);
    String update(KhuyenMai km);
    String delete(String maKM);
    List<KhuyenMai> search(String keyword);
    String updateTrangThai(String maKM, int trangThai);
}