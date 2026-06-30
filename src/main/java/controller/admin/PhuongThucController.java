package controller.admin;

import model.PhuongThucThanhToan;
import service.IPhuongThucThanhToanService;
import service.impl.PhuongThucThanhToanServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "PhuongThucController", value = "/phuong-thuc")
public class PhuongThucController extends HttpServlet {
    private IPhuongThucThanhToanService ptService = new PhuongThucThanhToanServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "delete":
                request.getSession().setAttribute("message", ptService.delete(request.getParameter("id")));
                response.sendRedirect(request.getContextPath() + "/phuong-thuc?action=list");
                break;
            case "toggle-status":
                request.getSession().setAttribute("message", ptService.updateTrangThai(request.getParameter("id"), Integer.parseInt(request.getParameter("status"))));
                response.sendRedirect(request.getContextPath() + "/phuong-thuc?action=list");
                break;
            case "search":
                request.setAttribute("danhSach", ptService.search(request.getParameter("keyword")));
                request.setAttribute("selectedKeyword", request.getParameter("keyword"));
                request.getRequestDispatcher("/views/admin/phuong_thuc.jsp").forward(request, response);
                break;
            case "list":
            default:
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
                }
                request.setAttribute("danhSach", ptService.getAllByPage(page));
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", ptService.getTotalPages());
                request.getRequestDispatcher("/views/admin/phuong_thuc.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        PhuongThucThanhToan pt = new PhuongThucThanhToan();
        pt.setTenPhuongThuc(request.getParameter("tenPhuongThuc"));

        if ("add".equals(action)) {
            request.getSession().setAttribute("message", ptService.add(pt));
        } else if ("update".equals(action)) {
            pt.setMaPTTT(request.getParameter("maPTTT"));
            request.getSession().setAttribute("message", ptService.update(pt));
        }
        response.sendRedirect(request.getContextPath() + "/phuong-thuc?action=list");
    }
}
