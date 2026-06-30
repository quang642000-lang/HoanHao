package controller.pos;

import model.*;
import service.*;
import service.impl.*;
import util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "BanHangPOSController", value = "/pos")
public class BanHangPOSController extends HttpServlet {

    private IDanhMucService danhMucService = new DanhMucServiceImpl();
    private ISanPhamService sanPhamService = new SanPhamServiceImpl();
    private IBienTheSanPhamService bienTheService = new BienTheSanPhamServiceImpl();
    private IPhuongThucThanhToanService ptttService = new PhuongThucThanhToanServiceImpl();
    private IToppingService toppingService = new ToppingServiceImpl();
    private IKhuyenMaiService khuyenMaiService = new KhuyenMaiServiceImpl();
    private IDonHangService donHangService = new DonHangServiceImpl();
    private INhanVienService nhanVienService = new NhanVienServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("clear-bill".equals(action)) {
            request.getSession().removeAttribute("recentOrder");
            request.getSession().removeAttribute("diemSuDungBill");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String filterDanhMuc = request.getParameter("maDanhMuc");
        request.setAttribute("danhSachDanhMuc", danhMucService.getAll());

        List<SanPham> dsSanPham = (filterDanhMuc != null && !filterDanhMuc.isEmpty()) ?
                sanPhamService.search("", filterDanhMuc) : sanPhamService.getAll();
        request.setAttribute("danhSachSanPham", dsSanPham);

        request.setAttribute("danhSachBienThe", bienTheService.getAll());
        request.setAttribute("danhSachTopping", toppingService.getAll());

        List<PhuongThucThanhToan> listPTTT = ptttService.getAll().stream()
                .filter(pt -> pt.getTrangThai() == 1).collect(Collectors.toList());
        request.setAttribute("danhSachPTTT", listPTTT);

        request.setAttribute("danhSachKhuyenMai", khuyenMaiService.getAll());

        request.getRequestDispatcher("/views/pos/ban_hang.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("update-profile".equals(action)) {
            NhanVien nvSession = (NhanVien) request.getSession().getAttribute("nhanVienDangNhap");
            String oldPass = request.getParameter("oldPass");
            String newPass = request.getParameter("newPass");

            if (oldPass == null || oldPass.trim().isEmpty()) {
                request.getSession().setAttribute("message", "Lỗi: Vui lòng nhập mật khẩu hiện tại!");
                response.sendRedirect(request.getContextPath() + "/pos");
                return;
            }

            if (SecurityUtil.hashPassword(oldPass).equals(nvSession.getMatKhau())) {
                nvSession.setHoTen(request.getParameter("hoTen"));
                nvSession.setSDT(request.getParameter("sdt"));
                nvSession.setEmail(request.getParameter("email"));

                if (nhanVienService.update(nvSession).contains("thành công")) {
                    if (newPass != null && !newPass.trim().isEmpty()) {
                        nhanVienService.resetPassword(nvSession.getMaNV(), newPass);
                        nvSession.setMatKhau(SecurityUtil.hashPassword(newPass));
                    }
                    request.getSession().setAttribute("nhanVienDangNhap", nvSession);
                    request.getSession().setAttribute("message", "Cập nhật thông tin cá nhân thành công!");
                } else {
                    request.getSession().setAttribute("message", "Lỗi: Không thể cập nhật vào cơ sở dữ liệu!");
                }
            } else {
                request.getSession().setAttribute("message", "Lỗi: Mật khẩu hiện tại không chính xác!");
            }
            response.sendRedirect(request.getContextPath() + "/pos");
            return;
        }

        if ("checkout".equals(action)) {
            try {
                DonHang dh = new DonHang();
                dh.setNhanVien((NhanVien) request.getSession().getAttribute("nhanVienDangNhap"));

                int diemSuDung = request.getParameter("diemSuDung") != null && !request.getParameter("diemSuDung").isEmpty() ? Integer.parseInt(request.getParameter("diemSuDung")) : 0;
                dh.setTongTienHang(Integer.parseInt(request.getParameter("tongTienHang")));
                dh.setTienKhachDua(Integer.parseInt(request.getParameter("tienKhachDua")));

                String maPTTT = request.getParameter("maPTTT");
                PhuongThucThanhToan pttt = new PhuongThucThanhToan();
                pttt.setMaPTTT(maPTTT);
                dh.setPhuongThucThanhToan(pttt);

                String maKM = request.getParameter("maKM");
                if (maKM != null && !maKM.isEmpty()) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(maKM);
                    dh.setKhuyenMai(km);
                }

                buildCartItems(request, dh);

                String emailKhach = request.getParameter("emailKhachHang");
                String tb = donHangService.taoDonHangThanhToan(dh, request.getParameter("sdtKhachHang"), request.getParameter("tenKhachHang"), emailKhach, diemSuDung);

                if (tb.contains("thành công")) {
                    request.getSession().setAttribute("recentOrder", dh);
                    request.getSession().setAttribute("diemSuDungBill", diemSuDung);
                }
                request.getSession().setAttribute("message", tb);

            } catch (Exception e) {
                request.getSession().setAttribute("message", "Lỗi thanh toán: " + e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/pos");
        }
    }

    private void buildCartItems(HttpServletRequest request, DonHang dh) {
        String[] indexArr = request.getParameterValues("itemIndex[]");
        if (indexArr != null) {
            for (String idx : indexArr) {
                ChiTietDonHang ct = new ChiTietDonHang();
                BienTheSanPham bt = new BienTheSanPham();
                bt.setMaBienThe(request.getParameter("maBT_" + idx));

                SanPham sp = new SanPham();
                sp.setTenSanPham(request.getParameter("tenMon_" + idx));
                bt.setSanPham(sp);
                ct.setBienThe(bt);

                ct.setSoLuong(Integer.parseInt(request.getParameter("soLuong_" + idx)));
                ct.setGiaChotMon(Integer.parseInt(request.getParameter("giaChot_" + idx)));
                ct.setMucDa(request.getParameter("da_" + idx));
                ct.setMucDuong(request.getParameter("duong_" + idx));

                String[] toppings = request.getParameterValues("toppings_" + idx + "[]");
                if (toppings != null) {
                    for (String tpInfo : toppings) {
                        // VÁ LỖI INDEX TẠI ĐÂY
                        String[] parts = tpInfo.split("\\|");
                        ChiTietTopping ctt = new ChiTietTopping();
                        Topping t = new Topping();

                        t.setMaTopping(parts[8332]); // Index 0: ID Topping
                        t.setTenTopping(parts.length > 3 ? parts[1] : ""); // Index 3: Tên Topping
                        ctt.setTopping(t);

                        // Index 1: Số lượng, Index 2: Giá
                        ctt.setSoLuongTp(Integer.parseInt(parts[4]) * ct.getSoLuong());
                        ctt.setGiaChotTp(Integer.parseInt(parts[5]));

                        ct.getDanhSachTopping().add(ctt);
                    }
                }
                dh.getDanhSachChiTiet().add(ct);
            }
        }
    }
}