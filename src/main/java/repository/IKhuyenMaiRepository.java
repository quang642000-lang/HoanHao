package repository;


import model.KhuyenMai;
import java.util.List;

public interface IKhuyenMaiRepository {
    List<KhuyenMai> getAll();
    List<KhuyenMai> getAll(int offset, int limit);
    int getTotalCount();
    KhuyenMai getById(String maKM);
    boolean add(KhuyenMai km);
    boolean update(KhuyenMai km);
    boolean delete(String maKM);
    List<KhuyenMai> search(String keyword);
    boolean updateTrangThai(String maKM, int trangThai);
}