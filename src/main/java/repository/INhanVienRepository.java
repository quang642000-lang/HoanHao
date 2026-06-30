package repository;

import model.NhanVien;
import java.util.List;
public interface INhanVienRepository {
    List<NhanVien> getAll(); List<NhanVien> getAll(int offset, int limit); int getTotalCount();
    boolean add(NhanVien nv); boolean update(NhanVien nv); boolean updateTrangThai(String maNV, int trangThaiMoi);
    boolean delete(String maNV); boolean resetPassword(String maNV, String matKhauMoi);
}
