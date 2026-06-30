package dto.response;

import java.util.Date;

public class DashboardKpiDTO {
    private String maDH;
    private Date thoiGian;
    private String tenNhanVien;
    private int tongTien;
    private String trangThai;

    public DashboardKpiDTO() {}

    public String getMaDH() { return maDH; }
    public void setMaDH(String maDH) { this.maDH = maDH; }

    public Date getThoiGian() { return thoiGian; }
    public void setThoiGian(Date thoiGian) { this.thoiGian = thoiGian; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public int getTongTien() { return tongTien; }
    public void setTongTien(int tongTien) { this.tongTien = tongTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}