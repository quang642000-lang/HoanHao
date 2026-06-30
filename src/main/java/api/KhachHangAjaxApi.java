package api;

import model.KhachHang;
import service.IKhachHangService;
import service.impl.KhachHangServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "KhachHangAjaxApi", value = "/api/khach-hang/search")
public class KhachHangAjaxApi extends HttpServlet {
    private IKhachHangService khachHangService = new KhachHangServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String phone = request.getParameter("phone");
        KhachHang kh = khachHangService.timKiemTheoSdt(phone);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (kh != null) {
            out.print("{\"found\":true, \"tenKH\":\"" + kh.getTenKH() + "\", \"diem\":" + kh.getDiemTichLuy() + "}");
        } else {
            out.print("{\"found\":false}");
        }
        out.flush();
    }
}
