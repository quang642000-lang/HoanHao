package repository;

import model.GioHang;

public interface IGioHangRepository {
    GioHang getCartByMaKH(String maKH);

    // BỔ SUNG HÀM KHỞI TẠO GIỎ HÀNG
    boolean createCart(String maKH);

    boolean upsertCartItem(String maGH, String maBT, int soLuong, String mucDaDuong, String toppingsJson);
    boolean clearCart(String maGH);
}