package service.impl;

import model.NhanVien;
import repository.IAuthRepository;
import repository.impl.AuthRepoImpl;
import service.IAuthService;
import util.SecurityUtil;

import java.util.concurrent.ConcurrentHashMap;

public class AuthServiceImpl implements IAuthService {
    // Gọi DAO theo chuẩn Interface
    private IAuthRepository authRepo = new AuthRepoImpl();

    // CƠ CHẾ CHỐNG BRUTE FORCE TẠI TẦNG SERVICE
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 5 * 60 * 1000; // 5 phút (tính bằng mili-giây)
    private ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> lockTimeouts = new ConcurrentHashMap<>();

    @Override
    public NhanVien login(String tenDangNhap, String matKhauPlain) throws Exception {
        if (tenDangNhap == null || matKhauPlain == null) return null;

        // 1. KIỂM TRA XEM TÀI KHOẢN CÓ ĐANG BỊ PHẠT KHÓA 5 PHÚT KHÔNG
        if (lockTimeouts.containsKey(tenDangNhap)) {
            long lockUntil = lockTimeouts.get(tenDangNhap);
            if (System.currentTimeMillis() < lockUntil) {
                long timeLeft = (lockUntil - System.currentTimeMillis()) / 1000;
                long minutes = timeLeft / 60;
                long seconds = timeLeft % 60;
                throw new Exception("Tài khoản đang bị tạm khóa. Thử lại sau " + minutes + " phút " + seconds + " giây!");
            } else {
                // Đã hết 5 phút phạt -> Xóa án phạt
                lockTimeouts.remove(tenDangNhap);
                failedAttempts.remove(tenDangNhap);
            }
        }

        // 2. TIẾN HÀNH BĂM MẬT KHẨU VÀ KIỂM TRA ĐĂNG NHẬP
        String hashedMatKhau = SecurityUtil.hashPassword(matKhauPlain);
        NhanVien nv = authRepo.login(tenDangNhap, matKhauPlain, hashedMatKhau);

        if (nv != null) {
            // Đăng nhập đúng -> Xóa sạch lịch sử nhập sai
            failedAttempts.remove(tenDangNhap);
            return nv;
        } else {
            // Đăng nhập sai -> Tăng bộ đếm
            int attempts = failedAttempts.getOrDefault(tenDangNhap, 0) + 1;
            failedAttempts.put(tenDangNhap, attempts);
            if (attempts >= MAX_ATTEMPTS) {
                // Đạt 5 lần sai -> Đưa vào danh sách phạt khóa 5 phút
                lockTimeouts.put(tenDangNhap, System.currentTimeMillis() + LOCK_TIME);
                throw new Exception("Nhập sai 5 lần! Tài khoản bị khóa 5 phút để bảo mật.");
            }
            throw new Exception("Tên đăng nhập hoặc mật khẩu sai! (Bạn còn " + (MAX_ATTEMPTS - attempts) + " lần thử)");
        }
    }

    @Override
    public String checkEmailAndGetUsername(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return authRepo.findUsernameByEmail(email);
    }

    @Override
    public boolean resetPassword(String tenDangNhap, String matKhauMoi) {
        if (tenDangNhap == null || matKhauMoi == null || matKhauMoi.trim().isEmpty()) return false;
        // Băm mật khẩu mới trước khi lưu
        String hashedMatKhauMoi = SecurityUtil.hashPassword(matKhauMoi);

        // Nếu đổi mật khẩu thành công, xóa luôn án phạt khóa tài khoản (nếu có)
        failedAttempts.remove(tenDangNhap);
        lockTimeouts.remove(tenDangNhap);

        return authRepo.updatePassword(tenDangNhap, hashedMatKhauMoi);
    }
}