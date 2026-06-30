package dto.response;

public class TopSanPhamDTO {
    private String tenSanPham;
    private int soLuongBan;

    public TopSanPhamDTO() {}
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    public int getSoLuongBan() { return soLuongBan; }
    public void setSoLuongBan(int soLuongBan) { this.soLuongBan = soLuongBan; }
}