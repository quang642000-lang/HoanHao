package controller.admin;

import model.KhuyenMai;
import service.IKhuyenMaiService;
import service.impl.KhuyenMaiServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "KhuyenMaiController", value = "/khuyen-mai")
public class KhuyenMaiController extends HttpServlet {
    private IKhuyenMaiService khuyenMaiService = new KhuyenMaiServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "delete":
                String maKmDel = request.getParameter("id");
                request.getSession().setAttribute("message", khuyenMaiService.delete(maKmDel));
                response.sendRedirect(request.getContextPath() + "/khuyen-mai?action=list");
                break;
            case "toggle-status":
                String maKmToggle = request.getParameter("id");
                int status = Integer.parseInt(request.getParameter("status"));
                request.getSession().setAttribute("message", khuyenMaiService.updateTrangThai(maKmToggle, status));
                response.sendRedirect(request.getContextPath() + "/khuyen-mai?action=list");
                break;
            case "search":
                String keyword = request.getParameter("keyword");
                request.setAttribute("danhSach", khuyenMaiService.search(keyword));
                request.setAttribute("selectedKeyword", keyword);
                request.getRequestDispatcher("/views/admin/khuyen_mai.jsp").forward(request, response);
                break;
            case "list":
            default:
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
                }
                request.setAttribute("danhSach", khuyenMaiService.getAllByPage(page));
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", khuyenMaiService.getTotalPages());
                request.getRequestDispatcher("/views/admin/khuyen_mai.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            KhuyenMai km = new KhuyenMai();
            String ten = request.getParameter("tenKm");
            if (ten == null || ten.trim().isEmpty()) ten = request.getParameter("tenKhuyenMai");
            km.setTenKM(ten);

            String maCode = request.getParameter("maCode");
            km.setMaCode(maCode != null ? maCode.trim().toUpperCase() : "");
            km.setLoaiGiamGia(request.getParameter("loaiGiamGia"));
            km.setGiaTriGiam(Integer.parseInt(request.getParameter("giaTriGiam")));
            km.setDieuKienToiThieu(Integer.parseInt(request.getParameter("dieuKienToiThieu")));
            km.setSoLuong(Integer.parseInt(request.getParameter("soLuong")));
            km.setNgayBatDau(Date.valueOf(request.getParameter("ngayBatDau")));
            km.setNgayKetThuc(Date.valueOf(request.getParameter("ngayKetThuc")));

            if ("add".equals(action)) {
                request.getSession().setAttribute("message", khuyenMaiService.add(km));
            } else if ("update".equals(action)) {
                String idUpdate = request.getParameter("maKm");
                if (idUpdate == null || idUpdate.isEmpty()) idUpdate = request.getParameter("id");
                km.setMaKM(idUpdate);
                request.getSession().setAttribute("message", khuyenMaiService.update(km));
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("message", "Lỗi dữ liệu đầu vào!");
        }
        response.sendRedirect(request.getContextPath() + "/khuyen-mai?action=list");
    }
}
