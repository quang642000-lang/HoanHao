package api;

import service.IThongKeService;
import service.impl.ThongKeServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ThongKeAjaxApi", value = "/api/thong-ke/receipt")
public class ThongKeAjaxApi extends HttpServlet {
    private IThongKeService thongKeService = new ThongKeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String maDH = request.getParameter("maDH");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // Gọi Service lấy JSON và in thẳng ra Response
        response.getWriter().write(thongKeService.getReceiptJson(maDH));
    }
}
