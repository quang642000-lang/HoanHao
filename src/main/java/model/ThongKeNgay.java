package model;

import java.util.Date;

public class ThongKeNgay {
    private Date ngayTK;
    private long doanhThu;
    private int soDonHang;
    private int soKHMoi;

    public ThongKeNgay() {}

    public Date getNgayTK() { return ngayTK; }
    public void setNgayTK(Date ngayTK) { this.ngayTK = ngayTK; }

    public long getDoanhThu() { return doanhThu; }
    public void setDoanhThu(long doanhThu) { this.doanhThu = doanhThu; }

    public int getSoDonHang() { return soDonHang; }
    public void setSoDonHang(int soDonHang) { this.soDonHang = soDonHang; }

    public int getSoKHMoi() { return soKHMoi; }
    public void setSoKHMoi(int soKHMoi) { this.soKHMoi = soKHMoi; }
}