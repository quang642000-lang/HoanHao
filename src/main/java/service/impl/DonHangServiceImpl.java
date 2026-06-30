package service.impl;

import model.ChiTietDonHang;
import model.ChiTietTopping;
import model.DonHang;
import model.KhachHang;
import model.KhuyenMai;
import model.BienTheSanPham;
import model.Topping;

import repository.IDonHangRepository;
import repository.impl.DonHangRepoImpl;
// Các repository phụ trợ giả định
import repository.impl.BienTheSanPhamRepoImpl;
import repository.impl.ToppingRepoImpl;
import repository.impl.KhuyenMaiRepoImpl;
import repository.impl.KhachHangRepoImpl;
import service.IDonHangService;

import java.sql.SQLException;
import java.util.Date;

public class DonHangServiceImpl implements IDonHangService {
    private IDonHangRepository donHangRepo = new DonHangRepoImpl();

    // Khởi tạo các DAO phụ trợ để kéo dữ liệu từ SQL lên kiểm tra
    private BienTheSanPhamRepoImpl bienTheRepo = new BienTheSanPhamRepoImpl();
    private ToppingRepoImpl toppingRepo = new ToppingRepoImpl();
    private KhuyenMaiRepoImpl khuyenMaiRepo = new KhuyenMaiRepoImpl();
    private KhachHangRepoImpl khachHangRepo = new KhachHangRepoImpl();

    @Override
    public String taoDonHangThanhToan(DonHang dh, String sdtKhachHang, String tenKhachHang, int diemSuDung) {
        try {
            int tongTienHang = 0;

            for (ChiTietDonHang ct : dh.getDanhSachChiTiet()) {
                // 1. THUẬT TOÁN PRICE SNAPSHOT (ĐÓNG BĂNG GIÁ MÓN NƯỚC)
                BienTheSanPham btDb = bienTheRepo.findById(ct.getBienThe().getMaBienThe());
                if (btDb == null) return "Lỗi: Có món ăn không tồn tại trong hệ thống!";
                int giaLy = btDb.getGiaBan();
                ct.setGiaChotMon(giaLy);

                int tongTienTopping = 0;

                // 2. THUẬT TOÁN PRICE SNAPSHOT (ĐÓNG BĂNG GIÁ TOPPING)
                for (ChiTietTopping ctt : ct.getDanhSachTopping()) {
                    Topping tpDb = toppingRepo.findById(ctt.getTopping().getMaTopping());
                    if (tpDb == null) return "Lỗi: Topping không tồn tại!";
                    ctt.setGiaChotTp(tpDb.getGiaBan());
                    tongTienTopping += tpDb.getGiaBan() * ctt.getSoLuongTp();
                }

                tongTienHang += (giaLy * ct.getSoLuong()) + tongTienTopping;
            }
            dh.setTongTienHang(tongTienHang);

            int tienGiamVoucher = 0;
            Date now = new Date();

            // 3. XỬ LÝ ĐIỀU KIỆN MÃ KHUYẾN MÃI
            if (dh.getKhuyenMai() != null && dh.getKhuyenMai().getMaKM() != null && !dh.getKhuyenMai().getMaKM().isEmpty()) {
                KhuyenMai km = khuyenMaiRepo.getById(dh.getKhuyenMai().getMaKM());
                if (km != null) {
                    // Kiểm tra Ngày tháng hiệu lực
                    if (km.getNgayBatDau() != null && now.before(km.getNgayBatDau())) {
                        return "Lỗi: Mã giảm giá này chưa đến ngày bắt đầu sử dụng!";
                    }
                    if (km.getNgayKetThuc() != null && now.getTime() > (km.getNgayKetThuc().getTime() + 86399000)) {
                        return "Lỗi: Mã giảm giá này đã hết hạn sử dụng!";
                    }
                    // Kiểm tra Đơn tối thiểu
                    if (tongTienHang >= km.getDieuKienToiThieu()) {
                        if ("Phần Trăm".equalsIgnoreCase(km.getLoaiGiamGia())) {
                            tienGiamVoucher = (tongTienHang * km.getGiaTriGiam()) / 100;
                        } else {
                            tienGiamVoucher = km.getGiaTriGiam();
                        }
                        if (tienGiamVoucher > tongTienHang) tienGiamVoucher = tongTienHang;
                    } else {
                        return "Lỗi: Đơn hàng chưa đạt mức tối thiểu " + km.getDieuKienToiThieu() + "đ để dùng mã này!";
                    }
                } else {
                    dh.setKhuyenMai(null);
                }
            }

            // 4. XỬ LÝ KHÁCH HÀNG CRM (AUTO-ONBOARDING)
            KhachHang kh = null;
            int diemGiamGia = 0;
            int diemCongThem = 0;

            if (sdtKhachHang != null && !sdtKhachHang.trim().isEmpty()) {
                kh = khachHangRepo.timKiemTheoSdt(sdtKhachHang.trim());
                // Khách lạ -> Tự động sinh User
                if (kh == null) {
                    kh = new KhachHang();
                    kh.setSDT(sdtKhachHang.trim());
                    kh.setTenKH(tenKhachHang != null && !tenKhachHang.isEmpty() ? tenKhachHang.trim() : "Khách hàng mới");
                    kh.setDiemTichLuy(0);
                    khachHangRepo.add(kh); // Đẩy ngay xuống CSDL
                    kh = khachHangRepo.timKiemTheoSdt(sdtKhachHang.trim());
                    if (kh == null) {
                        return "Lỗi: Không thể tự động tạo thẻ khách hàng mới. Vui lòng thử lại!";
                    }
                }

                // Quy đổi điểm Loyalty: 1 Điểm = 1000 VNĐ
                if (diemSuDung > kh.getDiemTichLuy()) diemSuDung = kh.getDiemTichLuy();
                int tienSauVoucher = tongTienHang - tienGiamVoucher;
                int diemToiDa = tienSauVoucher / 1000;

                if (diemSuDung > diemToiDa) diemSuDung = diemToiDa;
                diemGiamGia = diemSuDung * 1000;
            } else {
                diemSuDung = 0;
            }

            // Gán dữ liệu cuối cùng vào Object Đơn Hàng
            dh.setKhachHang(kh);
            int tongPhaiTra = Math.max(tongTienHang - tienGiamVoucher - diemGiamGia, 0);
            dh.setTienGiamGia(tienGiamVoucher + diemGiamGia);
            dh.setTongPhaiTra(tongPhaiTra);
            dh.setThoiGianTao(now);

            // Khách chưa đưa đủ tiền -> Báo lỗi
            if (dh.getTienKhachDua() < dh.getTongPhaiTra()) return "Lỗi: Số tiền khách đưa không đủ!";
            // Giỏ hàng bị trống ảo do Hacker chỉnh sửa DOM HTML -> Báo lỗi
            if (dh.getDanhSachChiTiet() == null || dh.getDanhSachChiTiet().isEmpty()) return "Lỗi: Giỏ hàng trống!";

            // Tích điểm cho đơn hiện tại: 10,000đ = 1 Điểm cộng thêm
            diemCongThem = tongPhaiTra / 10000;

            // 5. NÉM XUỐNG TẦNG REPOSITORY ĐỂ CHẠY SQL TRANSACTION (Lệnh ROWLOCK)
            boolean ketQua = donHangRepo.taoDonHang(dh, diemSuDung, diemCongThem);
            if (ketQua) {
                return "Thanh toán thành công! Đã in hóa đơn.";
            } else {
                return "Lỗi hệ thống khi lưu hóa đơn!";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi giao dịch (Race-condition hoặc Deadlock): " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống không xác định: " + e.getMessage();
        }
    }
}