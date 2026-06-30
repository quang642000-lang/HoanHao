package controller.admin;

import service.IVaiTroService;
import service.impl.VaiTroServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "VaiTroController", value = "/vai-tro")
public class VaiTroController extends HttpServlet {
    private IVaiTroService vaiTroService = new VaiTroServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("danhSachVaiTro", vaiTroService.getAll());
        request.getRequestDispatcher("/views/admin/vai_tro.jsp").forward(request, response);
    }
}