package service;

import model.GioHang;
public interface IGioHangService {
    GioHang getCart(String maKH);
    String addToCart(String maKH, String maBT, int soLuong, String mucDaDuong, String toppingsJson);
    String clearCart(String maKH);
}
