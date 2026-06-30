package model;

public class BienTheSanPham {
    private String maBienThe;
    private String kichCo;
    private int giaBan;
    private int trangThai;
    private SanPham sanPham; // Khóa ngoại kết nối Sản Phẩm mẹ

    public BienTheSanPham() {}

    public String getMaBienThe() { return maBienThe; }
    public void setMaBienThe(String maBienThe) { this.maBienThe = maBienThe; }

    public String getKichCo() { return kichCo; }
    public void setKichCo(String kichCo) { this.kichCo = kichCo; }

    public int getGiaBan() { return giaBan; }
    public void setGiaBan(int giaBan) { this.giaBan = giaBan; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }
}