package service.impl;

import model.KhachHang;
import repository.IKhachHangRepository;
import repository.impl.KhachHangRepoImpl;
import service.IKhachHangService;

import java.util.List;

public class KhachHangServiceImpl implements IKhachHangService {
    private IKhachHangRepository khachHangRepo = new KhachHangRepoImpl();
    private final int LIMIT = 5;

    @Override
    public List<KhachHang> getAll() { return khachHangRepo.getAll(); }

    @Override
    public List<KhachHang> getAllByPage(int page) {
        int offset = (page - 1) * LIMIT;
        return khachHangRepo.getAll(offset, LIMIT);
    }

    @Override
    public int getTotalPages() {
        int totalRecords = khachHangRepo.getTotalCount();
        return (int) Math.ceil((double) totalRecords / LIMIT);
    }

    @Override
    public KhachHang timKiemTheoSdt(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) return null;
        return khachHangRepo.timKiemTheoSdt(sdt.trim());
    }

    @Override
    public String add(KhachHang kh) {
        if (kh.getTenKH() == null || kh.getTenKH().trim().isEmpty()) return "Tên khách hàng không được để trống!";
        if (kh.getSDT() == null || !kh.getSDT().matches("\\d{10,11}")) return "Số điện thoại không hợp lệ!";

        if (khachHangRepo.timKiemTheoSdt(kh.getSDT()) != null) {
            return "Số điện thoại này đã tồn tại trong hệ thống!";
        }

        kh.setDiemTichLuy(0);
        String result = khachHangRepo.add(kh);

        // Nếu Database trả về chữ SUCCESS thì thành công. Ngược lại in thẳng câu LỖI SQL ra màn hình
        if ("SUCCESS".equals(result)) return "Thêm khách hàng thành công!";
        return result;
    }

    @Override
    public String update(KhachHang kh) {
        if (kh.getTenKH() == null || kh.getTenKH().trim().isEmpty()) return "Tên khách hàng không được để trống!";
        if (kh.getSDT() == null || !kh.getSDT().matches("\\d{10,11}")) return "Số điện thoại không hợp lệ!";
        return khachHangRepo.update(kh) ? "Cập nhật thành công!" : "Cập nhật thất bại!";
    }

    @Override
    public String delete(String maKH) {
        if (maKH == null || maKH.isEmpty()) return "Mã khách hàng không hợp lệ!";
        return khachHangRepo.delete(maKH) ? "Đã xóa khách hàng thành công!" : "Không thể xóa! Khách hàng này có thể đã phát sinh đơn hàng.";
    }

    @Override
    public boolean congDiemTichLuy(String maKh, int diemCongThem) {
        if (maKh == null || diemCongThem <= 0) return false;
        return khachHangRepo.congDiemTichLuy(maKh, diemCongThem);
    }
}