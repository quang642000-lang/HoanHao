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
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // Dùng GSON bóc tách dữ liệu khách hàng chọn
            JsonObject payload = JsonParser.parseString(sb.toString()).getAsJsonObject();
            String maKH = payload.has("maKH") ? payload.get("maKH").getAsString() : "";
            String maBT = payload.has("maBT") ? payload.get("maBT").getAsString() : "";
            int soLuong = payload.has("soLuong") ? payload.get("soLuong").getAsInt() : 1;
            String mucDaDuong = payload.has("mucDaDuong") ? payload.get("mucDaDuong").getAsString() : "100% Đá, 100% Đường";
            String toppingsJson = payload.has("toppingsJson") ? payload.get("toppingsJson").getAsString() : "[]";

            if (maKH.isEmpty() || maBT.isEmpty()) {
                out.print("{\"status\":\"error\", \"message\":\"Dữ liệu gửi lên không hợp lệ!\"}");
                return;
            }

            // Gọi Service để Upsert (Gộp) vào bảng CHI_TIET_GIO_HANG
            String result = gioHangService.addToCart(maKH, maBT, soLuong, mucDaDuong, toppingsJson);

            if (result.contains("thành công")) {
                out.print("{\"status\":\"success\", \"message\":\"" + result + "\"}");
            } else {
                out.print("{\"status\":\"error\", \"message\":\"" + result + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"Lỗi hệ thống khi xử lý giỏ hàng!\"}");
        }
    }
}
