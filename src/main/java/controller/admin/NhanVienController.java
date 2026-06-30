package controller.admin;

import model.NhanVien;
import model.VaiTro;
import service.INhanVienService;
import service.IVaiTroService;
import service.impl.NhanVienServiceImpl;
import service.impl.VaiTroServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "NhanVienController", value = "/nhan-vien")
public class NhanVienController extends HttpServlet {
    private INhanVienService nhanVienService = new NhanVienServiceImpl();
    private IVaiTroService vaiTroService = new VaiTroServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "toggle-status":
                String idToggle = request.getParameter("id");
                int status = Integer.parseInt(request.getParameter("status"));
                NhanVien currentNVToggle = (NhanVien) request.getSession().getAttribute("nhanVienDangNhap");
                // Tự bảo vệ: Chặn Admin tự khóa tài khoản của chính mình
                if (currentNVToggle != null && currentNVToggle.getMaNV().equals(idToggle) && status == 0) {
                    request.getSession().setAttribute("message", "Lỗi: Bạn không thể tự khóa tài khoản của chính mình!");
                } else {
                    request.getSession().setAttribute("message", nhanVienService.updateTrangThai(idToggle, status));
                }
                response.sendRedirect(request.getContextPath() + "/nhan-vien?action=list");
                break;

            case "delete":
                String idDel = request.getParameter("id");
                NhanVien currentNVDel = (NhanVien) request.getSession().getAttribute("nhanVienDangNhap");
                if (currentNVDel != null && currentNVDel.getMaNV().equals(idDel)) {
                    request.getSession().setAttribute("message", "Lỗi: Dại dột! Bạn không thể tự xóa tài khoản của chính mình!");
                } else {
                    request.getSession().setAttribute("message", nhanVienService.delete(idDel));
                }
                response.sendRedirect(request.getContextPath() + "/nhan-vien?action=list");
                break;

            case "list":
            default:
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
                }
                request.setAttribute("danhSach", nhanVienService.getAllByPage(page));
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", nhanVienService.getTotalPages());
                // Nạp danh sách Vai trò để hiển thị trên Modal Thêm mới
                request.setAttribute("danhSachVaiTro", vaiTroService.getAll());

                request.getRequestDispatcher("/views/admin/nhan_vien.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("add".equals(request.getParameter("action"))) {
            NhanVien nv = new NhanVien();
            nv.setHoTen(request.getParameter("hoTen"));
            nv.setTenDangNhap(request.getParameter("tenDangNhap"));
            nv.setMatKhau(request.getParameter("matKhau"));
            nv.setSDT(request.getParameter("SDT"));
            nv.setEmail(request.getParameter("email"));

            VaiTro vt = new VaiTro();
            vt.setMaVaiTro(Integer.parseInt(request.getParameter("maVaiTro")));
            nv.setVaiTro(vt);

            request.getSession().setAttribute("message", nhanVienService.add(nv));
        }
        response.sendRedirect(request.getContextPath() + "/nhan-vien?action=list");
    }
}
