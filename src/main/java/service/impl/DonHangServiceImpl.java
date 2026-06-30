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
import repository.impl.BienTheSanPhamRepoImpl;
import repository.impl.ToppingRepoImpl;
import repository.impl.KhuyenMaiRepoImpl;
import repository.impl.KhachHangRepoImpl;
import service.IDonHangService;

import java.sql.SQLException;
import java.util.Date;

public class DonHangServiceImpl implements IDonHangService {

    private IDonHangRepository donHangRepo = new DonHangRepoImpl();
    private BienTheSanPhamRepoImpl bienTheRepo = new BienTheSanPhamRepoImpl();
    private ToppingRepoImpl toppingRepo = new ToppingRepoImpl();
    private KhuyenMaiRepoImpl khuyenMaiRepo = new KhuyenMaiRepoImpl();
    private KhachHangRepoImpl khachHangRepo = new KhachHangRepoImpl();

    @Override
    public String taoDonHangThanhToan(DonHang dh, String sdtKhachHang, String tenKhachHang, String emailKhachHang, int diemSuDung) {
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

            // Lấy thông tin Khách hàng trước để đối chiếu hạng thẻ
            KhachHang kh = null;
            if (sdtKhachHang != null && !sdtKhachHang.trim().isEmpty()) {
                kh = khachHangRepo.timKiemTheoSdt(sdtKhachHang.trim());
                if (kh == null) {
                    kh = new KhachHang();
                    kh.setSDT(sdtKhachHang.trim());
                    kh.setTenKH(tenKhachHang != null && !tenKhachHang.isEmpty() ? tenKhachHang.trim() : "Khách hàng mới");
                    kh.setEmail(emailKhachHang); // Lưu Email khách cung cấp tại POS
                    kh.setDiemTichLuy(0);
                    khachHangRepo.add(kh);
                    kh = khachHangRepo.timKiemTheoSdt(sdtKhachHang.trim());
                    if (kh == null) return "Lỗi: Không thể tự động tạo thẻ khách hàng mới.";
                }
            }

            // 3. XỬ LÝ ĐIỀU KIỆN MÃ KHUYẾN MÃI (ĐÃ VÁ LỖI)
            if (dh.getKhuyenMai() != null && dh.getKhuyenMai().getMaKM() != null && !dh.getKhuyenMai().getMaKM().isEmpty()) {
                KhuyenMai km = khuyenMaiRepo.getById(dh.getKhuyenMai().getMaKM());
                if (km != null) {
                    if (km.getNgayBatDau() != null && now.before(km.getNgayBatDau()))
                        return "Lỗi: Mã giảm giá này chưa đến ngày bắt đầu sử dụng!";
                    if (km.getNgayKetThuc() != null && now.getTime() > (km.getNgayKetThuc().getTime() + 86399000))
                        return "Lỗi: Mã giảm giá này đã hết hạn sử dụng!";

                    // BẢO VỆ 1: Kiểm tra Hạng thẻ áp dụng
                    if (!"Đồng".equalsIgnoreCase(km.getHangApDung())) {
                        if (kh == null) return "Lỗi: Mã này chỉ dành cho Hội viên hạng " + km.getHangApDung() + " trở lên!";
                        int reqRank = getRankValue(km.getHangApDung());
                        int cusRank = getRankValue(kh.getHangThanhVien());
                        if (cusRank < reqRank) return "Lỗi: Hạng thẻ của bạn không đủ điều kiện dùng mã này!";
                    }

                    // BẢO VỆ 2: Tính tiền và chặn Giảm Tối Đa
                    if (tongTienHang >= km.getDieuKienToiThieu()) {
                        if ("Phần Trăm".equalsIgnoreCase(km.getLoaiGiamGia())) {
                            tienGiamVoucher = (tongTienHang * km.getGiaTriGiam()) / 100;
                        } else {
                            tienGiamVoucher = km.getGiaTriGiam();
                        }

                        // Chốt chặn Giảm tối đa
                        if (km.getGiamToiDa() > 0 && tienGiamVoucher > km.getGiamToiDa()) {
                            tienGiamVoucher = km.getGiamToiDa();
                        }
                        if (tienGiamVoucher > tongTienHang) tienGiamVoucher = tongTienHang;
                    } else {
                        return "Lỗi: Đơn hàng chưa đạt mức tối thiểu " + km.getDieuKienToiThieu() + "đ để dùng mã này!";
                    }
                } else {
                    dh.setKhuyenMai(null);
                }
            }

            // 4. XỬ LÝ ĐIỂM LOYALTY
            int diemGiamGia = 0;
            int diemCongThem = 0;

            if (kh != null) {
                if (diemSuDung > kh.getDiemTichLuy()) diemSuDung = kh.getDiemTichLuy();
                int tienSauVoucher = tongTienHang - tienGiamVoucher;
                int diemToiDa = tienSauVoucher / 1000;
                if (diemSuDung > diemToiDa) diemSuDung = diemToiDa;
                diemGiamGia = diemSuDung * 1000;
            } else {
                diemSuDung = 0;
            }

            dh.setKhachHang(kh);
            int tongPhaiTra = Math.max(tongTienHang - tienGiamVoucher - diemGiamGia, 0);
            dh.setTienGiamGia(tienGiamVoucher + diemGiamGia);
            dh.setTongPhaiTra(tongPhaiTra);
            dh.setThoiGianTao(now);

            if (!"O2O_PORTAL".equals(dh.getLoaiDH()) && dh.getTienKhachDua() < dh.getTongPhaiTra()) {
                return "Lỗi: Số tiền khách đưa không đủ!";
            }
            if (dh.getDanhSachChiTiet() == null || dh.getDanhSachChiTiet().isEmpty()) {
                return "Lỗi: Giỏ hàng trống!";
            }

            diemCongThem = tongPhaiTra / 10000;
            // 5. GỌI REPOSITORY TRANSACTION
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

    // Hàm phụ trợ tính cấp bậc thẻ
    private int getRankValue(String rankName) {
        if (rankName == null) return 1;
        switch(rankName.toLowerCase()) {
            case "bạc": return 2;
            case "vàng": return 3;
            case "kim cương": return 4;
            default: return 1; // Đồng
        }
    }
}