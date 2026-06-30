package repository;

import model.KhachHang;
import java.util.List;

public interface IKhachHangRepository {
    List<KhachHang> getAll();
    List<KhachHang> getAll(int offset, int limit);
    int getTotalCount();
    KhachHang timKiemTheoSdt(String sdt);

    String add(KhachHang kh); // ĐÃ ĐỔI TỪ BOOLEAN SANG STRING

    boolean update(KhachHang kh);
    boolean delete(String maKH);
    boolean congDiemTichLuy(String maKh, int diemCongThem);
}