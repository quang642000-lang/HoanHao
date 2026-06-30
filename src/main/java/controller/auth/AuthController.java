package controller.auth;

import model.NhanVien;
import service.IAuthService;
import service.impl.AuthServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AuthController", value = "/auth")
public class AuthController extends HttpServlet {
    private IAuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession();
            session.removeAttribute("nhanVienDangNhap");
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/auth");
        } else {
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("login".equals(action)) {
            String tenDangNhap = request.getParameter("username");
            String matKhau = request.getParameter("password");
            try {
                NhanVien nv = authService.login(tenDangNhap, matKhau);
                if (nv != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("nhanVienDangNhap", nv);
                    if (nv.getVaiTro().getMaVaiTro() == 1) {
                        response.sendRedirect(request.getContextPath() + "/admin");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/pos");
                    }
                } else {
                    request.setAttribute("error", "Tên đăng nhập không tồn tại hoặc đã bị khóa!");
                    request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                }
            } catch (Exception e) { // Hứng lỗi Brute-Force khóa 5 phút từ Service
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            }
        }
    }
}