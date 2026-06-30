package service;

import model.DonHang;

public interface IDonHangService {
    // Chuyển toàn bộ dữ liệu tạm thời thành 1 Đơn hàng hoàn chỉnh (Giá cứng, Điểm, Voucher)
    String taoDonHangThanhToan(DonHang dh, String sdtKhachHang, String tenKhachHang, String emailKhachHang, int diemSuDung);
}