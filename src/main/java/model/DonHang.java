package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonHang {
    private String maDH;
    private String loaiDH;
    private int tongTienHang;
    private int tienGiamGia;
    private int tongPhaiTra;
    private int diemSuDung;
    private int tienTruDiem;
    private int tienKhachDua;
    private Date thoiGianHenLay;
    private Date thoiGianTT;
    private String trangThaiDon;
    private Date thoiGianTao;

    // Khóa ngoại (Mối quan hệ)
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private KhuyenMai khuyenMai;
    private PhuongThucThanhToan phuongThucThanhToan;

    // Danh sách các món trong hóa đơn (1-N)
    private List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

    public DonHang() {}

    // Getters & Setters
    public String getMaDH() { return maDH; }
    public void setMaDH(String maDH) { this.maDH = maDH; }

    public String getLoaiDH() { return loaiDH; }
    public void setLoaiDH(String loaiDH) { this.loaiDH = loaiDH; }

    public int getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(int tongTienHang) { this.tongTienHang = tongTienHang; }

    public int getTienGiamGia() { return tienGiamGia; }
    public void setTienGiamGia(int tienGiamGia) { this.tienGiamGia = tienGiamGia; }

    public int getTongPhaiTra() { return tongPhaiTra; }
    public void setTongPhaiTra(int tongPhaiTra) { this.tongPhaiTra = tongPhaiTra; }

    public int getDiemSuDung() { return diemSuDung; }
    public void setDiemSuDung(int diemSuDung) { this.diemSuDung = diemSuDung; }

    public int getTienTruDiem() { return tienTruDiem; }
    public void setTienTruDiem(int tienTruDiem) { this.tienTruDiem = tienTruDiem; }

    public int getTienKhachDua() { return tienKhachDua; }
    public void setTienKhachDua(int tienKhachDua) { this.tienKhachDua = tienKhachDua; }

    public Date getThoiGianHenLay() { return thoiGianHenLay; }
    public void setThoiGianHenLay(Date thoiGianHenLay) { this.thoiGianHenLay = thoiGianHenLay; }

    public Date getThoiGianTT() { return thoiGianTT; }
    public void setThoiGianTT(Date thoiGianTT) { this.thoiGianTT = thoiGianTT; }

    public String getTrangThaiDon() { return trangThaiDon; }
    public void setTrangThaiDon(String trangThaiDon) { this.trangThaiDon = trangThaiDon; }

    public Date getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(Date thoiGianTao) { this.thoiGianTao = thoiGianTao; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(KhuyenMai khuyenMai) { this.khuyenMai = khuyenMai; }

    public PhuongThucThanhToan getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }

    public List<ChiTietDonHang> getDanhSachChiTiet() { return danhSachChiTiet; }
    public void setDanhSachChiTiet(List<ChiTietDonHang> danhSachChiTiet) { this.danhSachChiTiet = danhSachChiTiet; }
}