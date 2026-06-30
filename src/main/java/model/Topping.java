package model;

public class Topping {
    private String maTopping;
    private String tenTopping;
    private int giaBan;
    private String hinhAnh;
    private int trangThai;

    public Topping() {}

    public String getMaTopping() { return maTopping; }
    public void setMaTopping(String maTopping) { this.maTopping = maTopping; }

    public String getTenTopping() { return tenTopping; }
    public void setTenTopping(String tenTopping) { this.tenTopping = tenTopping; }

    public int getGiaBan() { return giaBan; }
    public void setGiaBan(int giaBan) { this.giaBan = giaBan; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
}