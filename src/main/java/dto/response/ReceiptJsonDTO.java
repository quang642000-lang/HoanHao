package dto.response;

import java.util.List;
import java.util.Map;

public class ReceiptJsonDTO {
    private String maDH;
    private String nhanVien;
    private String khachHang;
    private String ngay;
    private String phuongThuc;
    private int tongTienHang;
    private int tienGiamGia;
    private int tongPhaiTra;
    private int tienKhachDua;
    private List<Map<String, Object>> items;

    public ReceiptJsonDTO() {}
    public String getMaDH() { return maDH; } public void setMaDH(String maDH) { this.maDH = maDH; }
    public String getNhanVien() { return nhanVien; } public void setNhanVien(String nhanVien) { this.nhanVien = nhanVien; }
    public String getKhachHang() { return khachHang; } public void setKhachHang(String khachHang) { this.khachHang = khachHang; }
    public String getNgay() { return ngay; } public void setNgay(String ngay) { this.ngay = ngay; }
    public String getPhuongThuc() { return phuongThuc; } public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }
    public int getTongTienHang() { return tongTienHang; } public void setTongTienHang(int tongTienHang) { this.tongTienHang = tongTienHang; }
    public int getTienGiamGia() { return tienGiamGia; } public void setTienGiamGia(int tienGiamGia) { this.tienGiamGia = tienGiamGia; }
    public int getTongPhaiTra() { return tongPhaiTra; } public void setTongPhaiTra(int tongPhaiTra) { this.tongPhaiTra = tongPhaiTra; }
    public int getTienKhachDua() { return tienKhachDua; } public void setTienKhachDua(int tienKhachDua) { this.tienKhachDua = tienKhachDua; }
    public List<Map<String, Object>> getItems() { return items; } public void setItems(List<Map<String, Object>> items) { this.items = items; }
}