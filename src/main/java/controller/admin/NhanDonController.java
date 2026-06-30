package controller.admin;

import model.DonHang;
import service.IDonHangService;
import service.IThongKeService;
import service.impl.DonHangServiceImpl;
import service.impl.ThongKeServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "NhanDonController", value = "/nhan-don")
public class NhanDonController extends HttpServlet {
    private IThongKeService thongKeService = new ThongKeServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chỉ load giao diện. Việc load dữ liệu sẽ do AJAX làm để auto-refresh 5s/lần
        request.getRequestDispatcher("/views/admin/nhan_don.jsp").forward(request, response);
    }
}