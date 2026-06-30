package controller.admin;

import model.DanhMuc;
import service.IDanhMucService;
import service.impl.DanhMucServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "DanhMucController", value = "/danh-muc")
public class DanhMucController extends HttpServlet {
    private IDanhMucService danhMucService = new DanhMucServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        if ("delete".equals(action)) {
            try {
                String maDanhMuc = request.getParameter("id");
                String thongBao = danhMucService.delete(maDanhMuc);
                request.getSession().setAttribute("message", thongBao);
            } catch (Exception e) {
                request.getSession().setAttribute("message", "Lỗi: ID không hợp lệ!");
            }
            response.sendRedirect(request.getContextPath() + "/danh-muc?action=list");
        } else {
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
            }

            List<DanhMuc> listDM = danhMucService.getAllByPage(page);
            int totalPages = danhMucService.getTotalPages();

            request.setAttribute("danhSach", listDM);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/views/admin/danh_muc.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String ten = request.getParameter("tenDanhMuc");
            DanhMuc dm = new DanhMuc();
            dm.setTenDanhMuc(ten);
            String thongBao = danhMucService.add(dm);
            request.getSession().setAttribute("message", thongBao);
        } else if ("update".equals(action)) {
            try {
                String id = request.getParameter("maDanhMuc");
                String ten = request.getParameter("tenDanhMuc");
                DanhMuc dm = new DanhMuc();
                dm.setMaDanhMuc(id);
                dm.setTenDanhMuc(ten);
                String thongBao = danhMucService.update(dm);
                request.getSession().setAttribute("message", thongBao);
            } catch (Exception e) {
                request.getSession().setAttribute("message", "Lỗi định dạng mã danh mục!");
            }
        }
        response.sendRedirect(request.getContextPath() + "/danh-muc?action=list");
    }
}