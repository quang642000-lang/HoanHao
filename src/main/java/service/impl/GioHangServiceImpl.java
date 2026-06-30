package service.impl;

import model.GioHang;
import repository.IGioHangRepository;
import repository.impl.GioHangRepoImpl;
import service.IGioHangService;

public class GioHangServiceImpl implements IGioHangService {

    private IGioHangRepository repo = new GioHangRepoImpl();

    @Override
    public GioHang getCart(String maKH) {
        return repo.getCartByMaKH(maKH);
    }

    @Override
    public String addToCart(String maKH, String maBT, int soLuong, String mucDaDuong, String toppingsJson) {
        try {
            GioHang gh = repo.getCartByMaKH(maKH);

            // Khởi tạo phiên giỏ hàng mới nếu khách chưa có
            if (gh == null) {
                repo.createCart(maKH);
                gh = repo.getCartByMaKH(maKH);
                if (gh == null) return "Lỗi: Không thể lấy lại giỏ hàng vừa tạo từ Server!";
            }

            boolean success = repo.upsertCartItem(gh.getMaGH(), maBT, soLuong, mucDaDuong, toppingsJson);
            return success ? "Thêm vào giỏ thành công!" : "Lỗi không xác định khi lưu chi tiết giỏ!";

        } catch (Exception e) {
            // NẾU CÓ LỖI SQL, CHỮ ĐỎ SẼ IN THẲNG RA MÀN HÌNH KHÁCH HÀNG
            return e.getMessage();
        }
    }

    @Override
    public String clearCart(String maKH) {
        GioHang gh = repo.getCartByMaKH(maKH);
        if (gh != null && repo.clearCart(gh.getMaGH())) return "Đã dọn sạch giỏ hàng!";
        return "Lỗi dọn giỏ hàng!";
    }
}