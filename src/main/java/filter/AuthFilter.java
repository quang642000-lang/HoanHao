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
    // Chỉ Admin mới được vào các trang này
    private static final String[] ADMIN_ROUTES = {
            "/nhan-vien", "/admin", "/san-pham", "/danh-muc",
            "/bien-the", "/khach-hang", "/topping", "/khuyen-mai", "/phuong-thuc"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String path = req.getServletPath();

        // 1. Cho phép thả cửa phần Đăng nhập, API ngầm và File tĩnh
        if (path.startsWith("/auth") || path.startsWith("/assets") || path.startsWith("/api/") || path.contains(".css") || path.contains(".js") || path.contains(".png") || path.contains(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Chặn nếu chưa đăng nhập
        boolean isLoggedIn = (session != null && session.getAttribute("nhanVienDangNhap") != null);
        if (!isLoggedIn) {
            res.sendRedirect(req.getContextPath() + "/auth");
            return;
        }

        // 3. Chặn Nhân viên (Role = 2) vào trang của Admin
        NhanVien nv = (NhanVien) session.getAttribute("nhanVienDangNhap");
        int role = nv.getVaiTro().getMaVaiTro();

        boolean isManagementPage = Arrays.stream(ADMIN_ROUTES).anyMatch(path::startsWith);
        if (role == 2 && isManagementPage) {
            session.setAttribute("message", "Lỗi: Bạn chỉ là Nhân Viên, không có quyền truy cập trang quản trị!");
            res.sendRedirect(req.getContextPath() + "/pos");
            return;
        }

        chain.doFilter(request, response);
    }
}
