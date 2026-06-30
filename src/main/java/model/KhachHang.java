package model;

public class KhachHang {
    private String maKH;
    private String tenKH;
    private String SDT;
    private String email;
    private String maPin;
    private String matKhau;
    private String hangThanhVien;
    private int diemTichLuy;
    private int trangThai;

    public KhachHang() {}

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }

    public String getSDT() { return SDT; }
    public void setSDT(String SDT) { this.SDT = SDT; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMaPin() { return maPin; }
    public void setMaPin(String maPin) { this.maPin = maPin; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getHangThanhVien() { return hangThanhVien; }
    public void setHangThanhVien(String hangThanhVien) { this.hangThanhVien = hangThanhVien; }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
}