package repository;

import model.DonHang;
import java.sql.SQLException;

public interface IDonHangRepository {
    // Hàm cốt lõi bọc Transaction xử lý Hóa đơn, Chi tiết, Topping và Tích điểm
    boolean taoDonHang(DonHang dh, int diemSuDung, int diemCongThem) throws SQLException;
}