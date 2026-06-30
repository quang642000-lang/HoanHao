package controller.auth;


import service.IAuthService;
import service.impl.AuthServiceImpl;
import util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

@WebServlet(name = "ForgotPasswordController", value = "/auth/forgot")
public class ForgotPasswordController extends HttpServlet {
    private IAuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/views/auth/forgot_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("send-otp".equals(action)) {
            String email = request.getParameter("email");
            String tenDangNhap = authService.checkEmailAndGetUsername(email);
            if (tenDangNhap != null) {
                Random random = new Random();
                String otp = String.valueOf(100000 + random.nextInt(900000));
                if (EmailUtil.sendOtpEmail(email, otp)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("reset_user", tenDangNhap);
                    session.setAttribute("reset_email", email);
                    session.setAttribute("saved_otp", otp);
                    session.setAttribute("otp_expiry", System.currentTimeMillis() + 300000); // Tồn tại 5 phút
                    request.getRequestDispatcher("/views/auth/verify_otp.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Lỗi: Không thể gửi email OTP lúc này!");
                    request.getRequestDispatcher("/views/auth/forgot_password.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Email không tồn tại hoặc tài khoản đã bị khóa!");
                request.getRequestDispatcher("/views/auth/forgot_password.jsp").forward(request, response);
            }
        } else if ("verify-otp".equals(action)) {
            HttpSession session = request.getSession();
            String userOtp = request.getParameter("otpInput");
            String savedOtp = (String) session.getAttribute("saved_otp");
            Long expiry = (Long) session.getAttribute("otp_expiry");

            if (expiry != null && System.currentTimeMillis() > expiry) {
                session.removeAttribute("saved_otp");
                request.setAttribute("error", "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới!");
                request.getRequestDispatcher("/views/auth/verify_otp.jsp").forward(request, response);
            } else if (savedOtp != null && savedOtp.equals(userOtp)) {
                session.setAttribute("otp_verified", true);
                session.removeAttribute("saved_otp");
                response.sendRedirect(request.getContextPath() + "/auth/reset");
            } else {
                request.setAttribute("error", "Mã OTP không chính xác. Vui lòng thử lại!");
                request.getRequestDispatcher("/views/auth/verify_otp.jsp").forward(request, response);
            }
        }
    }
}