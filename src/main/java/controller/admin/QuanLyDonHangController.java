package controller.admin;

import service.IThongKeService;
import service.impl.ThongKeServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "QuanLyDonHangController", value = "/quan-ly-don-hang")
public class QuanLyDonHangController extends HttpServlet {
    private IThongKeService thongKeService = new ThongKeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // API In lại hóa đơn
        if ("get-receipt".equals(action)) {
            String maDH = request.getParameter("maDH");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(thongKeService.getReceiptJson(maDH));
            return;
        }

        String tuNgay = request.getParameter("tuNgay");
        String denNgay = request.getParameter("denNgay");

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
        }

        // Tận dụng sức mạnh của Service Thống Kê để load danh sách Hóa đơn lớn
        request.setAttribute("listDonHang", thongKeService.getDonHangTheoNgayByPage(tuNgay, denNgay, null, page));
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", (int) Math.ceil((double) thongKeService.getTotalDonHang(tuNgay, denNgay, null) / thongKeService.getLimit()));
        request.setAttribute("tuNgay", tuNgay);
        request.setAttribute("denNgay", denNgay);

        request.getRequestDispatcher("/views/admin/quan_ly_don_hang.jsp").forward(request, response);
    }
}
