package service.impl;

import com.google.gson.Gson;
import dto.response.*;
import repository.IThongKeRepository;
import repository.impl.ThongKeRepoImpl;
import service.IThongKeService;

import java.util.List;
import java.util.Map;

public class ThongKeServiceImpl implements IThongKeService {
    private IThongKeRepository repo = new ThongKeRepoImpl();
    private final int LIMIT = 10;

    @Override public int getLimit() { return LIMIT; }
    @Override public int getTotalDonHang(String tuNgay, String denNgay, String maNV) { return repo.getTotalDonHang(tuNgay, denNgay, maNV); }
    @Override public List<DashboardKpiDTO> getDonHangTheoNgayByPage(String tuNgay, String denNgay, String maNV, int page) { return repo.getDonHangTheoNgay(tuNgay, denNgay, maNV, (page - 1) * LIMIT, LIMIT); }
    @Override public ThongKeDTO getThongKeTongQuan(String tuNgay, String denNgay, String maNV) { return repo.getThongKeTongQuan(tuNgay, denNgay, maNV); }
    @Override public List<TopSanPhamDTO> getTopSanPham(String tuNgay, String denNgay, String maNV) { return repo.getTopSanPham(tuNgay, denNgay, maNV); }
    @Override public Map<String, Integer> getDoanhThu7NgayQua(String tuNgay, String denNgay, String maNV) { return repo.getDoanhThu7NgayQua(tuNgay, denNgay, maNV); }

    @Override
    public String getReceiptJson(String maDH) {
        Map<String, Object> data = repo.getReceiptData(maDH);
        if (data == null || data.isEmpty()) return "{\"error\":\"Không tìm thấy đơn hàng trong Database.\"}";

        java.sql.Timestamp ts = (java.sql.Timestamp) data.get("thoi_gian_tho");
        if(ts != null) data.put("ngay", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ts));
        data.put("khachHang", data.get("ten_kh_tho") != null ? data.get("ten_kh_tho") : "Khách vãng lai");
        data.put("phuongThuc", data.get("ten_pttt_tho") != null ? data.get("ten_pttt_tho") : "Tiền mặt");
        return new Gson().toJson(data);
    }
}
