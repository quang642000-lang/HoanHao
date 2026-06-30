package service;

import dto.response.*;
import java.util.*;
public interface IThongKeService {
    int getLimit(); int getTotalDonHang(String tuNgay, String denNgay, String maNV);
    List<DashboardKpiDTO> getDonHangTheoNgayByPage(String tuNgay, String denNgay, String maNV, int page);
    ThongKeDTO getThongKeTongQuan(String tuNgay, String denNgay, String maNV);
    List<TopSanPhamDTO> getTopSanPham(String tuNgay, String denNgay, String maNV);
    Map<String, Integer> getDoanhThu7NgayQua(String tuNgay, String denNgay, String maNV);
    String getReceiptJson(String maDH);
}
