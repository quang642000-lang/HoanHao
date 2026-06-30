package controller.portal;

import model.KhachHang;
import service.IKhachHangService;
import service.impl.KhachHangServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "HoSoKhachHangController", value = "/portal/ho-so")
public class HoSoKhachHangController extends HttpServlet {
    private IKhachHangService khachHangService = new KhachHangServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        KhachHang kh = (KhachHang) session.getAttribute("khachHangDangNhap");

        if (kh == null) {
            request.getSession().setAttribute("message", "Vui lòng đăng nhập bằng SĐT để xem hồ sơ!");
            response.sendRedirect(request.getContextPath() + "/portal/trang-chu");
            return;
        }

        // Cập nhật lại thông tin điểm tích lũy mới nhất từ CSDL
        KhachHang freshKh = khachHangService.timKiemTheoSdt(kh.getSDT());
        if(freshKh != null) {
            session.setAttribute("khachHangDangNhap", freshKh);
        }

        request.getRequestDispatcher("/views/portal/ho_so_khach_hang.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("login".equals(action)) {
            String sdt = request.getParameter("sdt");
            KhachHang kh = khachHangService.timKiemTheoSdt(sdt);

            if (kh != null) {
                request.getSession().setAttribute("khachHangDangNhap", kh);
                request.getSession().setAttribute("message", "Đăng nhập thành công! Chào mừng " + kh.getTenKH());
            } else {
                request.getSession().setAttribute("message", "Lỗi: Số điện thoại chưa được mở thẻ thành viên!");
            }
            response.sendRedirect(request.getContextPath() + "/portal/trang-chu");

        } else if ("logout".equals(action)) {
            request.getSession().removeAttribute("khachHangDangNhap");
            response.sendRedirect(request.getContextPath() + "/portal/trang-chu");
        }
    }
}