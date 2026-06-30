package service;

import model.KhachHang;
import java.util.List;

public interface IKhachHangService {
    List<KhachHang> getAll();
    List<KhachHang> getAllByPage(int page);
    int getTotalPages();
    KhachHang timKiemTheoSdt(String sdt);
    String add(KhachHang kh);
    String update(KhachHang kh);
    String delete(String maKH);
    boolean congDiemTichLuy(String maKh, int diemCongThem);
}