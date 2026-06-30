package controller.portal;

import model.*;
import service.*;
import service.impl.*;

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
    private IBienTheSanPhamService bienTheService = new BienTheSanPhamServiceImpl();
    private IGioHangService gioHangService = new GioHangServiceImpl();
    private IDonHangService donHangService = new DonHangServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        KhachHang khSession = (KhachHang) request.getSession().getAttribute("khachHangDangNhap");

        if ("cart".equals(action)) {
            if (khSession == null) {
                request.getSession().setAttribute("message", "Vui lòng đăng nhập để xem giỏ hàng!");
                response.sendRedirect(request.getContextPath() + "/portal/trang-chu");
                return;
            }
            // Load giỏ hàng thật từ CSDL
            GioHang cart = gioHangService.getCart(khSession.getMaKH());
            request.setAttribute("myCart", cart);
            request.getRequestDispatcher("/views/portal/gio_hang.jsp").forward(request, response);
            return;
        }

        // Render trang chủ (Tải kèm list Biến thể để hiện Modal Size)
        request.setAttribute("danhSachDanhMuc", danhMucService.getAll());
        request.setAttribute("danhSachSanPham", sanPhamService.getAll());
        request.setAttribute("danhSachBienThe", bienTheService.getAll());
        request.getRequestDispatcher("/views/portal/trang_chu.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("checkout".equals(action)) {
            KhachHang khSession = (KhachHang) request.getSession().getAttribute("khachHangDangNhap");
            GioHang cart = gioHangService.getCart(khSession.getMaKH());

            if (cart == null || cart.getDanhSachChiTiet().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/portal/trang-chu");
                return;
            }

            // Chuyển GioHang thành DonHang
            DonHang dh = new DonHang();
            dh.setKhachHang(khSession);
            dh.setLoaiDH("O2O_PORTAL"); // Đánh dấu đơn Online

            // Gán NV rỗng (vì tự đặt)
            NhanVien nvBot = new NhanVien(); nvBot.setMaNV("NV10001"); dh.setNhanVien(nvBot);

            // Phương thức TT
            PhuongThucThanhToan pt = new PhuongThucThanhToan(); pt.setMaPTTT("PT10"); dh.setPhuongThucThanhToan(pt);

            for(ChiTietGioHang ctgh : cart.getDanhSachChiTiet()) {
                ChiTietDonHang ctdh = new ChiTietDonHang();
                ctdh.setBienThe(ctgh.getBienThe());
                ctdh.setSoLuong(ctgh.getSoLuong());
                ctdh.setGiaChotMon(ctgh.getBienThe().getGiaBan());
                ctdh.setGhiChu(ctgh.getMucDaDuong());
                dh.getDanhSachChiTiet().add(ctdh);
            }

            // Gọi logic Service chốt đơn (Sẽ gán trạng thái 'Hoàn Thành' mặc định, nhưng ta đè lại ở Repository nếu cần, ở đây tái sử dụng logic POS cho lẹ)
            donHangService.taoDonHangThanhToan(dh, khSession.getSDT(), khSession.getTenKH(), khSession.getEmail(), 0);

            // Sửa trạng thái thành 'Chờ pha chế' để Barista nhận (Do hàm taoDonHangThanhToan gán cứng là 'Hoàn Thành')
            try (java.sql.Connection con = repository.DBConnect.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement("UPDATE DON_HANG SET trang_thai_don = N'Chờ pha chế' WHERE ma_dh = ?")) {
                ps.setString(1, dh.getMaDH());
                ps.executeUpdate();
            } catch(Exception ignored) {}

            gioHangService.clearCart(khSession.getMaKH());
            request.getSession().setAttribute("message", "Đã đặt hàng hẹn giờ! Đơn của bạn đang được pha chế.");
            response.sendRedirect(request.getContextPath() + "/portal/ho-so");
        }
    }
}