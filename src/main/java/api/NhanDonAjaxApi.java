package api;

import repository.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "NhanDonAjaxApi", value = "/api/nhan-don/poll")
public class NhanDonAjaxApi extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonArray orders = new JsonArray();

        // Chỉ lấy các đơn khách đặt qua Portal đang có trạng thái "Chờ pha chế"
        String sql = "SELECT dh.ma_dh, kh.ten_kh, kh.so_dien_thoai, dh.tong_phai_tra, dh.thoi_gian_hen_lay " +
                "FROM DON_HANG dh JOIN KHACH_HANG kh ON dh.ma_kh = kh.ma_kh " +
                "WHERE dh.trang_thai_don = N'Chờ pha chế' ORDER BY dh.thoi_gian_tao ASC";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("maDH", rs.getString("ma_dh"));
                obj.addProperty("tenKH", rs.getString("ten_kh"));
                obj.addProperty("sdt", rs.getString("so_dien_thoai"));
                obj.addProperty("tongTien", rs.getInt("tong_phai_tra"));
                obj.addProperty("henLay", rs.getTimestamp("thoi_gian_hen_lay") != null ? rs.getTimestamp("thoi_gian_hen_lay").toString() : "Lấy ngay");
                orders.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.print(orders.toString());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Cập nhật trạng thái đơn (Hoàn thành hoặc Hủy)
        String maDH = request.getParameter("maDH");
        String status = request.getParameter("status"); // 'Hoàn thành' hoặc 'Đã hủy'

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE DON_HANG SET trang_thai_don = ? WHERE ma_dh = ?")) {
            ps.setString(1, status);
            ps.setString(2, maDH);
            ps.executeUpdate();
            response.setStatus(200);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}