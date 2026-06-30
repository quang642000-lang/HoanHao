package model;

import java.util.Date;

public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private String maCode;
    private String loaiGiamGia;
    private int giaTriGiam;
    private int giamToiDa;
    private int dieuKienToiThieu; // Đơn tối thiểu
    private String hangApDung;
    private int limitTrenUser;
    private int soLuong;

    // Biến phụ trợ dùng để thống kê số voucher ĐÃ DÙNG từ bảng Đơn Hàng
    private int soLuongDaDung;

    private Date ngayBatDau;
    private Date ngayKetThuc;
    private int trangThai;

    public KhuyenMai() {}

    // Getters and Setters
    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }

    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKM) { this.tenKM = tenKM; }

    public String getMaCode() { return maCode; }
    public void setMaCode(String maCode) { this.maCode = maCode; }

    public String getLoaiGiamGia() { return loaiGiamGia; }
    public void setLoaiGiamGia(String loaiGiamGia) { this.loaiGiamGia = loaiGiamGia; }

    public int getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(int giaTriGiam) { this.giaTriGiam = giaTriGiam; }

    public int getGiamToiDa() { return giamToiDa; }
    public void setGiamToiDa(int giamToiDa) { this.giamToiDa = giamToiDa; }

    public int getDieuKienToiThieu() { return dieuKienToiThieu; }
    public void setDieuKienToiThieu(int dieuKienToiThieu) { this.dieuKienToiThieu = dieuKienToiThieu; }

    public String getHangApDung() { return hangApDung; }
    public void setHangApDung(String hangApDung) { this.hangApDung = hangApDung; }

    public int getLimitTrenUser() { return limitTrenUser; }
    public void setLimitTrenUser(int limitTrenUser) { this.limitTrenUser = limitTrenUser; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public int getSoLuongDaDung() { return soLuongDaDung; }
    public void setSoLuongDaDung(int soLuongDaDung) { this.soLuongDaDung = soLuongDaDung; }

    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) { this.trangThai = trangThai; }
}