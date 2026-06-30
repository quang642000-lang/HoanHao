package model;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String SDT;
    private String email;
    private String tenDangNhap;
    private String matKhau;
    private int trangThai;
    private VaiTro vaiTro; // Khóa ngoại kết nối Vai Trò

    public NhanVien() {}

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSDT() { return SDT; }
    public void setSDT(String SDT) { this.SDT = SDT; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }

    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }
}