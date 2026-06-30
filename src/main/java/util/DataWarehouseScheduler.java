package util;

import repository.DBConnect;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class DataWarehouseScheduler implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Tính thời gian từ lúc Start Server đến 23:59 đêm nay
        long delay = Duration.between(LocalTime.now(), LocalTime.of(23, 59)).toMinutes();
        if (delay < 0) delay += 1440; // Nếu đã qua 23:59, hẹn sang ngày mai

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("⏳ [CRON-JOB] ĐANG CHỐT SỔ DOANH THU NGÀY VÀO DATA WAREHOUSE...");
            String sql = "INSERT INTO THONG_KE_NGAY (ngay_tk, doanh_thu, so_don_hang, so_kh_moi) " +
                    "SELECT CAST(GETDATE() AS DATE), ISNULL(SUM(tong_phai_tra),0), COUNT(*), " +
                    "(SELECT COUNT(*) FROM KHACH_HANG WHERE CAST(thoi_gian_tao AS DATE) = CAST(GETDATE() AS DATE)) " +
                    "FROM DON_HANG WHERE CAST(thoi_gian_tao AS DATE) = CAST(GETDATE() AS DATE) AND trang_thai_don = N'Hoàn thành'";
            try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.executeUpdate();
                System.out.println("✅ [CRON-JOB] CHỐT SỔ THÀNH CÔNG VÀO BẢNG THONG_KE_NGAY!");
            } catch (Exception e) {
                System.err.println("❌ Lỗi Data Warehouse: " + e.getMessage());
            }
        }, delay, 1440, TimeUnit.MINUTES); // Lặp lại mỗi 24 tiếng (1440 phút)
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) scheduler.shutdownNow();
    }
}