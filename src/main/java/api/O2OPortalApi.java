package api;

import service.IGioHangService;
import service.impl.GioHangServiceImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet(name = "O2OPortalApi", value = "/api/portal/cart")
public class O2OPortalApi extends HttpServlet {
    private IGioHangService gioHangService = new GioHangServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) { sb.append(line); }
            JsonObject payload = JsonParser.parseString(sb.toString()).getAsJsonObject();

            String action = payload.has("action") ? payload.get("action").getAsString() : "add";

            // 1. NGHIỆP VỤ: Đổi số lượng
            if ("update_qty".equals(action)) {
                String maCTGH = payload.get("maCTGH").getAsString();
                int change = payload.get("change").getAsInt();
                try (Connection con = repository.DBConnect.getConnection();
                     PreparedStatement ps = con.prepareStatement("UPDATE CHI_TIET_GIO_HANG SET so_luong = so_luong + ? WHERE ma_ctgh = ?")) {
                    ps.setInt(1, change); ps.setString(2, maCTGH); ps.executeUpdate();
                    PreparedStatement psDel = con.prepareStatement("DELETE FROM CHI_TIET_GIO_HANG WHERE ma_ctgh = ? AND so_luong <= 0");
                    psDel.setString(1, maCTGH); psDel.executeUpdate();
                    out.print("{\"status\":\"success\"}");
                }
                return;
            }

            // 2. NGHIỆP VỤ: Xóa món
            if ("delete".equals(action)) {
                String maCTGH = payload.get("maCTGH").getAsString();
                try (Connection con = repository.DBConnect.getConnection();
                     PreparedStatement ps = con.prepareStatement("DELETE FROM CHI_TIET_GIO_HANG WHERE ma_ctgh = ?")) {
                    ps.setString(1, maCTGH); ps.executeUpdate(); out.print("{\"status\":\"success\"}");
                }
                return;
            }

            // 3. NGHIỆP VỤ (MỚI): Bật/tắt Checkbox "Chọn Mua" giống Shopee
            if ("toggle_check".equals(action)) {
                String maCTGH = payload.get("maCTGH").getAsString();
                boolean isChecked = payload.get("isChecked").getAsBoolean();
                try (Connection con = repository.DBConnect.getConnection();
                     PreparedStatement ps = con.prepareStatement("UPDATE CHI_TIET_GIO_HANG SET is_chon_mua = ? WHERE ma_ctgh = ?")) {
                    ps.setBoolean(1, isChecked); ps.setString(2, maCTGH); ps.executeUpdate(); out.print("{\"status\":\"success\"}");
                }
                return;
            }

            // 4. NGHIỆP VỤ (MỚI): Sửa Size, Đá, Đường, Topping trong Giỏ Hàng
            if ("update_full_item".equals(action)) {
                String maCTGH = payload.get("maCTGH").getAsString();
                String maBT = payload.get("maBT").getAsString();
                String mucDaDuong = payload.get("mucDaDuong").getAsString();
                String toppingsJson = payload.get("toppingsJson").getAsString();

                try (Connection con = repository.DBConnect.getConnection();
                     PreparedStatement ps = con.prepareStatement("UPDATE CHI_TIET_GIO_HANG SET ma_bt = ?, muc_da_duong = ?, toppings_json = ? WHERE ma_ctgh = ?")) {
                    ps.setString(1, maBT); ps.setString(2, mucDaDuong); ps.setString(3, toppingsJson); ps.setString(4, maCTGH);
                    ps.executeUpdate(); out.print("{\"status\":\"success\"}");
                }
                return;
            }

            // 5. MẶC ĐỊNH LÀ ACTION = ADD (Thêm món mới từ Trang Chủ)
            String maKH = payload.has("maKH") ? payload.get("maKH").getAsString() : "";
            String maBT = payload.has("maBT") ? payload.get("maBT").getAsString() : "";
            int soLuong = payload.has("soLuong") ? payload.get("soLuong").getAsInt() : 1;
            String mucDaDuong = payload.has("mucDaDuong") ? payload.get("mucDaDuong").getAsString() : "100% Đá, 100% Đường";
            String toppingsJson = payload.has("toppingsJson") ? payload.get("toppingsJson").getAsString() : "[]";

            if (maKH.isEmpty() || maBT.isEmpty()) {
                out.print("{\"status\":\"error\", \"message\":\"Dữ liệu gửi lên không hợp lệ!\"}"); return;
            }

            String result = gioHangService.addToCart(maKH, maBT, soLuong, mucDaDuong, toppingsJson);
            if (result.contains("thành công")) {
                out.print("{\"status\":\"success\", \"message\":\"" + result + "\"}");
            } else { out.print("{\"status\":\"error\", \"message\":\"" + result + "\"}"); }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Lỗi máy chủ khi xử lý giỏ hàng!\"}");
        }
    }
}