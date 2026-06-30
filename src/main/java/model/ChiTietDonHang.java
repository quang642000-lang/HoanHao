package model;

import java.util.ArrayList;
import java.util.List;

public class ChiTietDonHang {
    private String maCTDH;
    private int soLuong;
    private int giaChotMon;
    private String mucDa;
    private String mucDuong;
    private String ghiChu;

    // Khóa ngoại
    private DonHang donHang;
    private BienTheSanPham bienThe;

    // Danh sách topping nằm trong ly nước này
    private List<ChiTietTopping> danhSachTopping = new ArrayList<>();

    public ChiTietDonHang() {}

    // Getters & Setters
    public String getMaCTDH() { return maCTDH; }
    public void setMaCTDH(String maCTDH) { this.maCTDH = maCTDH; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public int getGiaChotMon() { return giaChotMon; }
    public void setGiaChotMon(int giaChotMon) { this.giaChotMon = giaChotMon; }

    public String getMucDa() { return mucDa; }
    public void setMucDa(String mucDa) { this.mucDa = mucDa; }

    public String getMucDuong() { return mucDuong; }
    public void setMucDuong(String mucDuong) { this.mucDuong = mucDuong; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }

    public BienTheSanPham getBienThe() { return bienThe; }
    public void setBienThe(BienTheSanPham bienThe) { this.bienThe = bienThe; }

    public List<ChiTietTopping> getDanhSachTopping() { return danhSachTopping; }
    public void setDanhSachTopping(List<ChiTietTopping> danhSachTopping) { this.danhSachTopping = danhSachTopping; }
}