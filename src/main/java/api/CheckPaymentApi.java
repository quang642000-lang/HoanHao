package api;

import util.MemoryCacheStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "CheckPaymentApi", value = "/api/check-payment")
public class CheckPaymentApi extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String code = request.getParameter("code");
        if (code == null || code.trim().isEmpty()) {
            out.print("{\"status\":\"error\", \"message\":\"Thiếu mã giao dịch\"}");
            return;
        }

        // TỐI ƯU: Ép in hoa và xóa khoảng trắng thừa để khớp 100% với Webhook [12]
        String cleanCode = code.trim().toUpperCase();

        // Đọc từ Local Store của Webhook, tránh spam/polling API của SePay [12]
        if (MemoryCacheStore.transactions.containsKey(cleanCode)) {
            MemoryCacheStore.transactions.remove(cleanCode); // Giải phóng bộ nhớ ngay sau khi check xong [13]
            out.print("{\"status\":\"success\"}");
        } else {
            out.print("{\"status\":\"pending\"}");
        }
    }
}