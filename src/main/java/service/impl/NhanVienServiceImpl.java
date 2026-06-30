package service.impl;

import model.NhanVien;
import repository.INhanVienRepository;
import repository.impl.NhanVienRepoImpl;
import service.INhanVienService;
import  util.SecurityUtil;
import java.util.List;

public class NhanVienServiceImpl implements INhanVienService {
    private INhanVienRepository repo = new NhanVienRepoImpl();
    private final int LIMIT = 5;

    @Override public List<NhanVien> getAll() { return repo.getAll(); }
    @Override public List<NhanVien> getAllByPage(int page) { return repo.getAll((page - 1) * LIMIT, LIMIT); }
    @Override public int getTotalPages() { return (int) Math.ceil((double) repo.getTotalCount() / LIMIT); }

    @Override public String add(NhanVien nv) {
        if (nv.getTenDangNhap() == null || nv.getHoTen() == null) return "Lỗi: Thiếu dữ liệu!";
        nv.setMatKhau(SecurityUtil.hashPassword(nv.getMatKhau())); nv.setTrangThai(1);
        return repo.add(nv) ? "Thêm nhân viên thành công!" : "Lỗi hệ thống!";
    }
    @Override public String update(NhanVien nv) { return repo.update(nv) ? "Cập nhật thành công!" : "Lỗi hệ thống!"; }
    @Override public String updateTrangThai(String maNV, int trangThai) { return repo.updateTrangThai(maNV, trangThai) ? "Thành công!" : "Lỗi!"; }
    @Override public String delete(String maNV) { return repo.delete(maNV) ? "Đã xóa!" : "Không thể xóa do dính Hóa Đơn!"; }
    @Override public String resetPassword(String maNV, String mkMoi) { return repo.resetPassword(maNV, SecurityUtil.hashPassword(mkMoi)) ? "Đổi mật khẩu thành công!" : "Lỗi!"; }
}