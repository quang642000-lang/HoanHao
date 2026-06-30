package controller.auth;

import service.IAuthService;
import service.impl.AuthServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ResetPasswordController", value = "/auth/reset")
public class ResetPasswordController extends HttpServlet {
    private IAuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("otp_verified") != null && (boolean) session.getAttribute("otp_verified")) {
            request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/forgot");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String tenDangNhap = (String) session.getAttribute("reset_user");
        String matKhauMoi = request.getParameter("newPassword");
        Boolean isVerified = (Boolean) session.getAttribute("otp_verified");

        if (isVerified != null && isVerified && tenDangNhap != null && authService.resetPassword(tenDangNhap, matKhauMoi)) {
            session.removeAttribute("reset_user");
            session.removeAttribute("reset_email");
            session.removeAttribute("otp_verified");
            request.setAttribute("message", "Mật khẩu đã được đổi thành công. Vui lòng đăng nhập lại!");
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Phiên đăng nhập hết hạn. Vui lòng thử lại từ đầu!");
            request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);
        }
    }
}