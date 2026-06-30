package controller.portal;

import model.*;
import service.*;
import service.impl.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "O2OWebController", value = "/portal/trang-chu")
public class O2OWebController extends HttpServlet {
    private ISanPhamService sanPhamService = new SanPhamServiceImpl();
    private IDanhMucService danhMucService = new DanhMucServiceImpl();
    private IBienTheSanPhamService bienTheService = new BienTheSanPhamServiceImpl();
    private IGioHangService gioHangService = new GioHangServiceImpl();
    private IDonHangService donHangService = new DonHangServiceImpl();
    private IToppingService toppingService = new ToppingServiceImpl();
    private IPhuongThucThanhToanService ptttService = new PhuongThucThanhToanServiceImpl();
    private INhanVienService nhanVienService = new NhanVienServiceImpl();
    // BỔ SUNG KHUYẾN MÃI
    private IKhuyenMaiService khuyenMaiService = new KhuyenMaiServiceImpl();

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
            GioHang cart = gioHangService.getCart(khSession.getMaKH());
            request.setAttribute("myCart", cart);
            request.setAttribute("danhSachBienThe", bienTheService.getAll());
            request.setAttribute("danhSachTopping", toppingService.getAll());

            // ĐẨY DANH SÁCH MÃ GIẢM GIÁ RA TRANG GIỎ HÀNG THỰC HIỆN LOGIC SHOPEE
            request.setAttribute("danhSachKhuyenMai", khuyenMaiService.getAll());

            request.getRequestDispatcher("/views/portal/gio_hang.jsp").forward(request, response);
            return;
        }

        request.setAttribute("danhSachDanhMuc", danhMucService.getAll());
        request.setAttribute("danhSachSanPham", sanPhamService.getAll());
        request.setAttribute("danhSachBienThe", bienTheService.getAll());
        request.setAttribute("danhSachTopping", toppingService.getAll());
        request.getRequestDispatcher("/views/portal/trang_chu.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("checkout".equals(action)) {
            try {
                KhachHang khSession = (KhachHang) request.getSession().getAttribute("khachHangDangNhap");
                GioHang cart = gioHangService.getCart(khSession.getMaKH());

                if (cart == null || cart.getDanhSachChiTiet().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/portal/trang-chu");
                    return;
                }

                DonHang dh = new DonHang();
                dh.setKhachHang(khSession);
                dh.setLoaiDH("O2O_PORTAL");
                dh.setTrangThaiDon("Chờ pha chế");

                List<NhanVien> nvs = nhanVienService.getAll();
                if (!nvs.isEmpty()) dh.setNhanVien(nvs.get(0));
                else { NhanVien nvBot = new NhanVien(); nvBot.setMaNV("NV1000"); dh.setNhanVien(nvBot); }

                List<PhuongThucThanhToan> pts = ptttService.getAll();
                if (pts.isEmpty()) {
                    request.getSession().setAttribute("message", "Lỗi Cấu Hình: Chưa thiết lập phương thức thanh toán!");
                    response.sendRedirect(request.getContextPath() + "/portal/trang-chu?action=cart");
                    return;
                }

                PhuongThucThanhToan ptChuyenKhoan = null;
                for (PhuongThucThanhToan pt : pts) {
                    if (pt.getTenPhuongThuc().toLowerCase().contains("chuyển khoản") || pt.getTenPhuongThuc().toLowerCase().contains("qr")) {
                        ptChuyenKhoan = pt; break;
                    }
                }
                if(ptChuyenKhoan == null) ptChuyenKhoan = pts.get(0);
                dh.setPhuongThucThanhToan(ptChuyenKhoan);

                // BẮT MÃ KHUYẾN MÃI TỪ WEBSITE GỬI XUỐNG
                String maKM = request.getParameter("maKM");
                if (maKM != null && !maKM.trim().isEmpty()) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(maKM);
                    dh.setKhuyenMai(km);
                }

                int tongTienHang = 0;
                Gson gson = new Gson();

                for(ChiTietGioHang ctgh : cart.getDanhSachChiTiet()) {
                    if(!ctgh.isChonMua()) continue;

                    ChiTietDonHang ctdh = new ChiTietDonHang();
                    ctdh.setBienThe(ctgh.getBienThe());
                    ctdh.setSoLuong(ctgh.getSoLuong());
                    ctdh.setGiaChotMon(ctgh.getBienThe().getGiaBan());
                    ctdh.setGhiChu(ctgh.getMucDaDuong() + " | Giờ hẹn lấy: " + request.getParameter("gioHenLay"));

                    int tongTopping = 0;
                    if (ctgh.getToppingsJson() != null && !ctgh.getToppingsJson().isEmpty() && !ctgh.getToppingsJson().equals("[]")) {
                        try {
                            List<Map<String, Object>> toppings = gson.fromJson(ctgh.getToppingsJson(), new TypeToken<List<Map<String, Object>>>(){}.getType());
                            for (Map<String, Object> tp : toppings) {
                                ChiTietTopping ctt = new ChiTietTopping();
                                Topping t = new Topping();
                                t.setMaTopping((String) tp.get("id"));
                                ctt.setTopping(t);
                                int sl = ((Double) tp.get("qty")).intValue() * ctgh.getSoLuong();
                                int gia = ((Double) tp.get("price")).intValue();
                                ctt.setSoLuongTp(sl); ctt.setGiaChotTp(gia);
                                tongTopping += (sl * gia); ctdh.getDanhSachTopping().add(ctt);
                            }
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    dh.getDanhSachChiTiet().add(ctdh);
                    tongTienHang += (ctgh.getBienThe().getGiaBan() * ctgh.getSoLuong()) + tongTopping;
                }

                if(dh.getDanhSachChiTiet().isEmpty()) {
                    request.getSession().setAttribute("message", "Lỗi: Bạn chưa chọn món nào trong giỏ hàng để mua!");
                    response.sendRedirect(request.getContextPath() + "/portal/trang-chu?action=cart");
                    return;
                }

                dh.setTongTienHang(tongTienHang);
                dh.setTienKhachDua(tongTienHang);

                // TẦNG SERVICE SẼ TỰ ĐỘNG TÍNH TIỀN GIẢM VÀ VALIDATE VOUCHER
                String msg = donHangService.taoDonHangThanhToan(dh, khSession.getSDT(), khSession.getTenKH(), khSession.getEmail(), 0);

                if (msg.contains("thành công")) {
                    try (java.sql.Connection con = repository.DBConnect.getConnection();
                         java.sql.PreparedStatement ps = con.prepareStatement("DELETE FROM CHI_TIET_GIO_HANG WHERE ma_gh = ? AND is_chon_mua = 1")) {
                        ps.setString(1, cart.getMaGH()); ps.executeUpdate();
                    } catch(Exception ignored){}

                    try (java.sql.Connection con = repository.DBConnect.getConnection();
                         java.sql.PreparedStatement ps = con.prepareStatement("UPDATE DON_HANG SET trang_thai_don = N'Chờ pha chế' WHERE ma_dh = ?")) {
                        ps.setString(1, dh.getMaDH()); ps.executeUpdate();
                    } catch(Exception ignored){}

                    request.getSession().setAttribute("message", "Thanh toán thành công! Món của bạn đang được pha chế.");
                    response.sendRedirect(request.getContextPath() + "/portal/ho-so");
                } else {
                    request.getSession().setAttribute("message", "Lỗi thanh toán: " + msg);
                    response.sendRedirect(request.getContextPath() + "/portal/trang-chu?action=cart");
                }

            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("message", "Lỗi Hệ Thống: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/portal/trang-chu?action=cart");
            }
        }
    }
}