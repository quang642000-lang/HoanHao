package api;

import util.MemoryCacheStore;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "SePayWebhookApi", value = "/api/sepay-webhook")
public class SePayWebhookApi extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject payload = JsonParser.parseString(sb.toString()).getAsJsonObject();
            // Đổi tất cả thành IN HOA để chống phân biệt hoa/thường [10]
            String content = payload.get("content").getAsString().toUpperCase();

            // Tìm đoạn mã bắt đầu bằng chữ TEA và theo sau là các chữ số [10]
            Matcher matcher = Pattern.compile("TEA\\d+").matcher(content);
            while (matcher.find()) {
                MemoryCacheStore.transactions.put(matcher.group(), true); // Bắt dính mã và đưa vào bộ nhớ tạm [11]
            }

            response.setStatus(200);
            response.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            response.setStatus(400);
        }
    }
}