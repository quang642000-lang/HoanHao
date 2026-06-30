package controller.admin;

import service.IThongKeService;
import service.INhanVienService;
import service.impl.ThongKeServiceImpl;
import service.impl.NhanVienServiceImpl;
import dto.response.ThongKeDTO;
import dto.response.DashboardKpiDTO;
import dto.response.TopSanPhamDTO;
import model.NhanVien;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "TrangChuAdminController", value = "/admin")
public class TrangChuAdminController extends HttpServlet {
    private IThongKeService thongKeService = new ThongKeServiceImpl();
    private INhanVienService nhanVienService = new NhanVienServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("get-receipt".equals(action)) {
            String maDH = request.getParameter("maDH");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(thongKeService.getReceiptJson(maDH));
            return;
        }

        String tuNgay = request.getParameter("tuNgay");
        String denNgay = request.getParameter("denNgay");
        String maNV = request.getParameter("maNV");

        ThongKeDTO tk = thongKeService.getThongKeTongQuan(tuNgay, denNgay, maNV);
        request.setAttribute("thongKe", tk);

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
        }

        List<DashboardKpiDTO> listDonHang = thongKeService.getDonHangTheoNgayByPage(tuNgay, denNgay, maNV, page);
        int totalRecords = thongKeService.getTotalDonHang(tuNgay, denNgay, maNV);
        int totalPages = (int) Math.ceil((double) totalRecords / thongKeService.getLimit());

        request.setAttribute("listDonHang", listDonHang);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        List<TopSanPhamDTO> topSanPham = thongKeService.getTopSanPham(tuNgay, denNgay, maNV);
        request.setAttribute("topSanPham", topSanPham);

        List<NhanVien> danhSachNhanVien = nhanVienService.getAll();
        request.setAttribute("danhSachNhanVien", danhSachNhanVien);

        Map<String, Integer> chartData = thongKeService.getDoanhThu7NgayQua(tuNgay, denNgay, maNV);
        String chartLabels = "[]";
        String chartValues = "[]";

        if (!chartData.isEmpty()) {
            chartLabels = "[\"" + String.join("\",\"", chartData.keySet()) + "\"]";
            chartValues = "[" + chartData.values().stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
        }

        request.setAttribute("chartLabels", chartLabels);
        request.setAttribute("chartValues", chartValues);
        request.setAttribute("tuNgay", tuNgay);
        request.setAttribute("denNgay", denNgay);
        request.setAttribute("selectedNV", maNV);

        request.getRequestDispatcher("/views/admin/admin_dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}