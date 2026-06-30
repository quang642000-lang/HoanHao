package dto.request;

import java.util.List;

public class OrderRequestDTO {
    private String sdtKhachHang;
    private String tenKhachHang;
    private int diemSuDung;
    private String maPTTT;
    private String maKM;
    private int tongTienHang;
    private int tienKhachDua;
    private List<ItemDTO> items;

    public static class ItemDTO {
        private String maBT;
        private String tenMon;
        private int soLuong;
        private int giaChot;
        private String da;
        private String duong;
        private List<ToppingDTO> toppings;
        // Các getter setter cơ bản...
        public String getMaBT() { return maBT; } public void setMaBT(String maBT) { this.maBT = maBT; }
        public List<ToppingDTO> getToppings() { return toppings; } public void setToppings(List<ToppingDTO> toppings) { this.toppings = toppings; }
    }

    public static class ToppingDTO {
        private String id;
        private int qty;
        private int price;
        private String name;
        public String getId() { return id; } public void setId(String id) { this.id = id; }
    }

    // Getters and Setters cho OrderRequestDTO
    public String getSdtKhachHang() { return sdtKhachHang; } public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }
    public List<ItemDTO> getItems() { return items; } public void setItems(List<ItemDTO> items) { this.items = items; }
}
