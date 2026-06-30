package controller.admin;

import model.BienTheSanPham;
import model.SanPham;
import service.IBienTheSanPhamService;
import service.ISanPhamService;
import service.impl.BienTheSanPhamServiceImpl;
import service.impl.SanPhamServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "BienTheSanPhamController", value = "/bien-the")
public class BienTheSanPhamController extends HttpServlet {
    private IBienTheSanPhamService bienTheService = new BienTheSanPhamServiceImpl();
    private ISanPhamService sanPhamService = new SanPhamServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "delete":
                String idDel = request.getParameter("id");
                request.getSession().setAttribute("message", bienTheService.delete(idDel));
                response.sendRedirect(request.getContextPath() + "/bien-the?action=list");
                break;
            case "toggle-status":
                String idToggle = request.getParameter("id");
                int status = Integer.parseInt(request.getParameter("status"));
                request.getSession().setAttribute("message", bienTheService.updateTrangThai(idToggle, status));
                response.sendRedirect(request.getContextPath() + "/bien-the?action=list");
                break;
            case "search":
                String keyword = request.getParameter("keyword");
                String filterSanPham = request.getParameter("filterSanPham");
                request.setAttribute("danhSach", bienTheService.search(keyword, filterSanPham));
                request.setAttribute("danhSachSP", sanPhamService.getAll());
                request.setAttribute("selectedKeyword", keyword);
                request.setAttribute("selectedSanPham", filterSanPham);
                request.getRequestDispatcher("/views/admin/bien_the.jsp").forward(request, response);
                break;
            case "list":
            default:
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
                }
                request.setAttribute("danhSach", bienTheService.getAllByPage(page));
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", bienTheService.getTotalPages());
                request.setAttribute("danhSachSP", sanPhamService.getAll());
                request.getRequestDispatcher("/views/admin/bien_the.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            BienTheSanPham bt = new BienTheSanPham();
            bt.setKichCo(request.getParameter("kichCo"));
            try { bt.setGiaBan(Integer.parseInt(request.getParameter("giaBan"))); } catch (Exception e) { bt.setGiaBan(0); }

            SanPham sp = new SanPham();
            sp.setMaSP(request.getParameter("maSP"));
            bt.setSanPham(sp);

            request.getSession().setAttribute("message", bienTheService.add(bt));
        } else if ("update".equals(action)) {
            BienTheSanPham bt = new BienTheSanPham();
            bt.setMaBienThe(request.getParameter("maBienThe"));
            bt.setKichCo(request.getParameter("kichCo"));
            try { bt.setGiaBan(Integer.parseInt(request.getParameter("giaBan"))); } catch (Exception e) { bt.setGiaBan(0); }

            SanPham sp = new SanPham();
            sp.setMaSP(request.getParameter("maSP"));
            bt.setSanPham(sp);

            request.getSession().setAttribute("message", bienTheService.update(bt));
        }
        response.sendRedirect(request.getContextPath() + "/bien-the?action=list");
    }
}