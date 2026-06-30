package model;

public class ChiTietGioHang {
    private String maCTGH;
    private int soLuong;
    private String mucDaDuong;
    private String toppingsJson;
    private boolean isChonMua;

    private GioHang gioHang;
    private BienTheSanPham bienThe;

    public ChiTietGioHang() {}

    public String getMaCTGH() { return maCTGH; }
    public void setMaCTGH(String maCTGH) { this.maCTGH = maCTGH; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public String getMucDaDuong() { return mucDaDuong; }
    public void setMucDaDuong(String mucDaDuong) { this.mucDaDuong = mucDaDuong; }

    public String getToppingsJson() { return toppingsJson; }
    public void setToppingsJson(String toppingsJson) { this.toppingsJson = toppingsJson; }

    public boolean isChonMua() { return isChonMua; }
    public void setChonMua(boolean isChonMua) { this.isChonMua = isChonMua; }

    public GioHang getGioHang() { return gioHang; }
    public void setGioHang(GioHang gioHang) { this.gioHang = gioHang; }

    public BienTheSanPham getBienThe() { return bienThe; }
    public void setBienThe(BienTheSanPham bienThe) { this.bienThe = bienThe; }
}