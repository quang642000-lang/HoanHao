package controller.portal;

import service.ISanPhamService;
import service.impl.SanPhamServiceImpl;
import service.IDanhMucService;
import service.impl.DanhMucServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "O2OWebController", value = "/portal/trang-chu")
public class O2OWebController extends HttpServlet {
    private ISanPhamService sanPhamService = new SanPhamServiceImpl();
    private IDanhMucService danhMucService = new DanhMucServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Điều hướng sang màn hình thanh toán / giỏ hàng online
        if ("cart".equals(action)) {
            request.getRequestDispatcher("/views/portal/gio_hang.jsp").forward(request, response);
            return;
        }

        // Tải danh sách Danh mục lên thanh Menu trên
        request.setAttribute("danhSachDanhMuc", danhMucService.getAll());

        // Tải danh sách Món ăn (hỗ trợ lọc theo danh mục)
        String maDanhMuc = request.getParameter("danhMuc");
        if (maDanhMuc != null && !maDanhMuc.isEmpty() && !maDanhMuc.equals("all")) {
            request.setAttribute("danhSachSanPham", sanPhamService.search("", maDanhMuc));
            request.setAttribute("selectedDanhMuc", maDanhMuc);
        } else {
            request.setAttribute("danhSachSanPham", sanPhamService.getAll());
            request.setAttribute("selectedDanhMuc", "all");
        }

        request.getRequestDispatcher("/views/portal/trang_chu.jsp").forward(request, response);
    }
}