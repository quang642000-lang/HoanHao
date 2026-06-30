package controller.admin;

import service.INhatKyHoatDongService;
import service.impl.NhatKyHoatDongServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "NhatKyController", value = "/nhat-ky")
public class NhatKyController extends HttpServlet {
    private INhatKyHoatDongService nhatKyService = new NhatKyHoatDongServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
        }

        request.setAttribute("danhSachLog", nhatKyService.getAllLogsByPage(page));
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", nhatKyService.getTotalPages());

        request.getRequestDispatcher("/views/admin/nhat_ky_he_thong.jsp").forward(request, response);
    }
}