package model;

public class ChiTietTopping {
    private String maCTT;
    private int soLuongTp;
    private int giaChotTp;

    // Khóa ngoại
    private ChiTietDonHang chiTietDonHang;
    private Topping topping;

    public ChiTietTopping() {}

    public String getMaCTT() { return maCTT; }
    public void setMaCTT(String maCTT) { this.maCTT = maCTT; }

    public int getSoLuongTp() { return soLuongTp; }
    public void setSoLuongTp(int soLuongTp) { this.soLuongTp = soLuongTp; }

    public int getGiaChotTp() { return giaChotTp; }
    public void setGiaChotTp(int giaChotTp) { this.giaChotTp = giaChotTp; }

    public ChiTietDonHang getChiTietDonHang() { return chiTietDonHang; }
    public void setChiTietDonHang(ChiTietDonHang chiTietDonHang) { this.chiTietDonHang = chiTietDonHang; }

    public Topping getTopping() { return topping; }
    public void setTopping(Topping topping) { this.topping = topping; }
}