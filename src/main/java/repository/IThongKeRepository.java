package repository;

import dto.response.*;
import java.util.*;
public interface IThongKeRepository {
    int getTotalDonHang(String tuNgay, String denNgay, String maNV);
    List<DashboardKpiDTO> getDonHangTheoNgay(String tuNgay, String denNgay, String maNV, int offset, int limit);
    ThongKeDTO getThongKeTongQuan(String tuNgay, String denNgay, String maNV);
    List<TopSanPhamDTO> getTopSanPham(String tuNgay, String denNgay, String maNV);
    Map<String, Integer> getDoanhThu7NgayQua(String tuNgay, String denNgay, String maNV);
    Map<String, Object> getReceiptData(String maDH);
}
