package repository;

import model.GioHang;
public interface IGioHangRepository {
    GioHang getCartByMaKH(String maKH);
    boolean upsertCartItem(String maGH, String maBT, int soLuong, String mucDaDuong, String toppingsJson);
    boolean clearCart(String maGH);
}