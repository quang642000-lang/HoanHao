package service;

import model.NhanVien;
import java.util.List;
public interface INhanVienService {
    List<NhanVien> getAll(); List<NhanVien> getAllByPage(int page); int getTotalPages();
    String add(NhanVien nv); String update(NhanVien nv); String updateTrangThai(String maNV, int trangThaiMoi);
    String delete(String maNV); String resetPassword(String maNV, String matKhauMoi);
}