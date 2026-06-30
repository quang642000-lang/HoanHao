package service.impl;

import model.GioHang;
import repository.IGioHangRepository;
import repository.impl.GioHangRepoImpl;
import service.IGioHangService;

public class GioHangServiceImpl implements IGioHangService {
    private IGioHangRepository repo = new GioHangRepoImpl();

    @Override public GioHang getCart(String maKH) { return repo.getCartByMaKH(maKH); }

    @Override
    public String addToCart(String maKH, String maBT, int soLuong, String mucDaDuong, String toppingsJson) {
        GioHang gh = repo.getCartByMaKH(maKH);
        if (gh == null) {
            // Logic tự tạo vỏ giỏ hàng mới cho Khách nếu chưa có sẽ được gọi qua Repository
            return "Lỗi: Khách hàng chưa có phiên giỏ hàng!";
        }
        return repo.upsertCartItem(gh.getMaGH(), maBT, soLuong, mucDaDuong, toppingsJson) ? "Thêm vào giỏ thành công!" : "Lỗi hệ thống!";
    }

    @Override
    public String clearCart(String maKH) {
        GioHang gh = repo.getCartByMaKH(maKH);
        if (gh != null && repo.clearCart(gh.getMaGH())) return "Đã dọn sạch giỏ hàng!";
        return "Lỗi dọn giỏ hàng!";
    }
}