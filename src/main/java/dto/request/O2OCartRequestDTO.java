package dto.request;

public class O2OCartRequestDTO {
    private String maKH;
    private String maBT;
    private int soLuong;
    private String mucDaDuong;
    private String toppingsJson;

    public O2OCartRequestDTO() {}
    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public String getMaBT() { return maBT; }
    public void setMaBT(String maBT) { this.maBT = maBT; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public String getMucDaDuong() { return mucDaDuong; }
    public void setMucDaDuong(String mucDaDuong) { this.mucDaDuong = mucDaDuong; }
    public String getToppingsJson() { return toppingsJson; }
    public void setToppingsJson(String toppingsJson) { this.toppingsJson = toppingsJson; }
}