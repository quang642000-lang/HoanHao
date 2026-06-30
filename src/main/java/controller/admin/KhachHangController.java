package controller.admin;

import model.KhachHang;
import service.IKhachHangService;
import service.impl.KhachHangServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "KhachHangController", value = "/khach-hang")
public class KhachHangController extends HttpServlet {
    private IKhachHangService khachHangService = new KhachHangServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "delete":
                String maKHDelete = request.getParameter("maKH");
                String tbXoa = khachHangService.delete(maKHDelete);
                request.getSession().setAttribute("message", tbXoa);
                response.sendRedirect(request.getContextPath() + "/khach-hang?action=list");
                break;
            case "search":
                String sdt = request.getParameter("sdt");
                KhachHang kh = khachHangService.timKiemTheoSdt(sdt);
                if (kh != null) {
                    List<KhachHang> listSearch = new ArrayList<>();
                    listSearch.add(kh);
                    request.setAttribute("danhSach", listSearch);
                }
                request.setAttribute("currentPage", 1);
                request.setAttribute("totalPages", 1);
                request.getRequestDispatcher("/views/admin/khach_hang.jsp").forward(request, response);
                break;
            case "list":
            default:
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
                }
                request.setAttribute("danhSach", khachHangService.getAllByPage(page));
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", khachHangService.getTotalPages());
                request.getRequestDispatcher("/views/admin/khach_hang.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            KhachHang kh = new KhachHang();
            kh.setTenKH(request.getParameter("tenKhachHang"));
            kh.setSDT(request.getParameter("soDienThoai"));
            String thongBao = khachHangService.add(kh);
            request.getSession().setAttribute("message", thongBao);
        } else if ("update".equals(action)) {
            KhachHang kh = new KhachHang();
            kh.setMaKH(request.getParameter("maKH"));
            kh.setTenKH(request.getParameter("tenKhachHang"));
            kh.setSDT(request.getParameter("soDienThoai"));
            String thongBao = khachHangService.update(kh);
            request.getSession().setAttribute("message", thongBao);
        }
        response.sendRedirect(request.getContextPath() + "/khach-hang?action=list");
    }
}