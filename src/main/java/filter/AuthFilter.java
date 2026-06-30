package filter;

import model.NhanVien;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final String[] ADMIN_ROUTES = {
            "/nhan-vien", "/admin", "/san-pham", "/danh-muc",
            "/bien-the", "/khach-hang", "/topping", "/khuyen-mai", "/phuong-thuc",
            "/nhat-ky", "/quan-ly-don-hang", "/vai-tro"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String path = req.getServletPath();

        // 1. Cho phép thả cửa: Form Đăng nhập Admin, API ngầm, File CSS/JS và Trang Chủ Khách Hàng
        if (path.startsWith("/auth") || path.startsWith("/assets") || path.startsWith("/api/") ||
                path.contains(".css") || path.contains(".js") || path.contains(".png") || path.contains(".jpg") ||
                path.startsWith("/portal/trang-chu")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Chặn vùng Kín của Khách Hàng O2O (Phải đăng nhập bằng SĐT)
        if (path.startsWith("/portal/ho-so")) {
            if (session == null || session.getAttribute("khachHangDangNhap") == null) {
                res.sendRedirect(req.getContextPath() + "/portal/trang-chu");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // 3. Vùng của Admin/Thu ngân (Bắt buộc đăng nhập Nhân Viên)
        if (session == null || session.getAttribute("nhanVienDangNhap") == null) {
            res.sendRedirect(req.getContextPath() + "/auth");
            return;
        }

        // 4. Chặn Thu Ngân (Role = 2) vào xem thông số của Chủ Quán
        NhanVien nv = (NhanVien) session.getAttribute("nhanVienDangNhap");
        if (nv.getVaiTro().getMaVaiTro() == 2 && Arrays.stream(ADMIN_ROUTES).anyMatch(path::startsWith)) {
            session.setAttribute("message", "Lỗi: Bạn chỉ là Nhân Viên, không có quyền truy cập trang quản trị!");
            res.sendRedirect(req.getContextPath() + "/pos");
            return;
        }

        chain.doFilter(request, response);
    }
}