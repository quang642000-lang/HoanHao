package api;

import model.KhachHang;
import service.IKhachHangService;
import service.impl.KhachHangServiceImpl;
import util.EmailUtil;
import util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

@WebServlet(name = "PortalAuthAjaxApi", value = "/api/portal/auth")
public class PortalAuthAjaxApi extends HttpServlet {

    // FIX WARNING: Đã thêm từ khóa 'final'
    private final IKhachHangService khachHangService = new KhachHangServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            if ("check-phone".equals(action)) {
                String phone = request.getParameter("phone");
                KhachHang kh = khachHangService.timKiemTheoSdt(phone);

                if (kh != null) {
                    if (kh.getMatKhau() != null && !kh.getMatKhau().isEmpty()) {
                        out.print("{\"status\":\"has_pass\"}");
                    } else {
                        if (kh.getEmail() == null || kh.getEmail().trim().isEmpty()) {
                            out.print("{\"status\":\"error\", \"message\":\"Tài khoản tạo tại quầy chưa liên kết Email. Vui lòng liên hệ cửa hàng để kích hoạt thẻ O2O!\"}");
                            return;
                        }
                        String otp = String.valueOf(100000 + new Random().nextInt(900000));
                        if (EmailUtil.sendOtpEmail(kh.getEmail(), otp)) {
                            session.setAttribute("portal_otp", otp);
                            session.setAttribute("portal_phone", phone);
                            session.setAttribute("portal_otp_expiry", System.currentTimeMillis() + 300000);

                            // FIX ERROR 1 CHÍ MẠNG (SUBSTRING TRÊN MẢNG):
                            // Tách Email thành 2 phần: Tên và Tên miền
                            String[] emailParts = kh.getEmail().split("@");
                            String namePart = emailParts[0];
                            String domainPart = emailParts.length > 1 ? emailParts[2] : "";

                            // Che giấu phần tên (Nếu tên dài hơn 2 ký tự thì cắt, nếu ngắn thì giữ nguyên + ***)
                            String maskedName = namePart.length() > 2 ? namePart.substring(0, 2) + "***" : namePart + "***";
                            String maskedEmail = maskedName + "@" + domainPart;

                            out.print("{\"status\":\"no_pass\", \"email\":\"" + maskedEmail + "\"}");
                        } else {
                            out.print("{\"status\":\"error\", \"message\":\"Hệ thống lỗi SMTP. Không thể gửi Email!\"}");
                        }
                    }
                } else {
                    out.print("{\"status\":\"not_found\"}");
                }
            }
            else if ("login".equals(action)) {
                String phone = request.getParameter("phone");
                String pass = request.getParameter("password");
                KhachHang kh = khachHangService.timKiemTheoSdt(phone);

                if (kh != null && kh.getMatKhau() != null && kh.getMatKhau().equals(SecurityUtil.hashPassword(pass))) {
                    session.setAttribute("khachHangDangNhap", kh);
                    out.print("{\"status\":\"success\"}");
                } else {
                    out.print("{\"status\":\"error\", \"message\":\"Mật khẩu không chính xác!\"}");
                }
            }
            else if ("verify-set-pass".equals(action)) {
                String phone = (String) session.getAttribute("portal_phone");
                String otpInput = request.getParameter("otp");
                String newPass = request.getParameter("password");
                String savedOtp = (String) session.getAttribute("portal_otp");
                Long expiry = (Long) session.getAttribute("portal_otp_expiry");

                if (expiry == null || System.currentTimeMillis() > expiry) {
                    out.print("{\"status\":\"error\", \"message\":\"Mã OTP đã hết hạn 5 phút!\"}"); return;
                }
                if (savedOtp != null && savedOtp.equals(otpInput)) {
                    try (java.sql.Connection con = repository.DBConnect.getConnection();
                         java.sql.PreparedStatement ps = con.prepareStatement("UPDATE KHACH_HANG SET mat_khau = ? WHERE so_dien_thoai = ?")) {
                        ps.setString(1, SecurityUtil.hashPassword(newPass));
                        ps.setString(2, phone);
                        ps.executeUpdate();
                    }
                    session.removeAttribute("portal_otp");
                    session.setAttribute("khachHangDangNhap", khachHangService.timKiemTheoSdt(phone));
                    out.print("{\"status\":\"success\"}");
                } else {
                    out.print("{\"status\":\"error\", \"message\":\"Mã OTP không đúng!\"}");
                }
            }
            else if ("register".equals(action)) {
                String phone = request.getParameter("phone");
                String name = request.getParameter("name");
                String email = request.getParameter("email");
                String pass = request.getParameter("password");

                if(khachHangService.timKiemTheoSdt(phone) != null) {
                    out.print("{\"status\":\"error\", \"message\":\"Số điện thoại này đã được đăng ký!\"}");
                    return;
                }

                KhachHang kh = new KhachHang();
                kh.setSDT(phone);
                kh.setTenKH(name);
                kh.setEmail(email);
                kh.setDiemTichLuy(0);
                kh.setMatKhau(SecurityUtil.hashPassword(pass));

                String resultMessage = khachHangService.add(kh);

                if (resultMessage.contains("thành công")) {
                    session.setAttribute("khachHangDangNhap", khachHangService.timKiemTheoSdt(phone));
                    out.print("{\"status\":\"success\"}");
                } else {
                    out.print("{\"status\":\"error\", \"message\":\"" + resultMessage + "\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Có thể giữ lại trong quá trình phát triển
            out.print("{\"status\":\"error\", \"message\":\"Lỗi Máy Chủ: " + e.getMessage() + "\"}");
        }
    }
}