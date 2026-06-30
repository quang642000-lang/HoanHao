package model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GioHang {
    private String maGH;
    private String maGDTam;
    private Date thoiGianUp;

    private KhachHang khachHang; // Khóa ngoại

    private List<ChiTietGioHang> danhSachChiTiet = new ArrayList<>();

    public GioHang() {}

    public String getMaGH() { return maGH; }
    public void setMaGH(String maGH) { this.maGH = maGH; }

    public String getMaGDTam() { return maGDTam; }
    public void setMaGDTam(String maGDTam) { this.maGDTam = maGDTam; }

    public Date getThoiGianUp() { return thoiGianUp; }
    public void setThoiGianUp(Date thoiGianUp) { this.thoiGianUp = thoiGianUp; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public List<ChiTietGioHang> getDanhSachChiTiet() { return danhSachChiTiet; }
    public void setDanhSachChiTiet(List<ChiTietGioHang> danhSachChiTiet) { this.danhSachChiTiet = danhSachChiTiet; }
}