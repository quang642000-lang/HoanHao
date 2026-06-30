package controller.admin;

import model.Topping;
import service.IToppingService;
import service.impl.ToppingServiceImpl;
import util.ConfigUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
@WebServlet(name = "ToppingController", value = "/topping")
public class ToppingController extends HttpServlet {
    private IToppingService toppingService = new ToppingServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "delete":
                request.getSession().setAttribute("message", toppingService.delete(request.getParameter("id")));
                response.sendRedirect(request.getContextPath() + "/topping?action=list");
                break;
            case "toggle-status":
                request.getSession().setAttribute("message", toppingService.updateTrangThai(request.getParameter("id"), Integer.parseInt(request.getParameter("status"))));
                response.sendRedirect(request.getContextPath() + "/topping?action=list");
                break;
            case "search":
                request.setAttribute("danhSach", toppingService.search(request.getParameter("keyword")));
                request.setAttribute("selectedKeyword", request.getParameter("keyword"));
                request.getRequestDispatcher("/views/admin/topping.jsp").forward(request, response);
                break;
            case "list":
            default:
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
                }
                request.setAttribute("danhSach", toppingService.getAllByPage(page));
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", toppingService.getTotalPages());
                request.getRequestDispatcher("/views/admin/topping.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String uploadPath = ConfigUtil.getUploadDir();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        Topping tp = new Topping();
        tp.setTenTopping(request.getParameter("tenTopping"));
        try { tp.setGiaBan(Integer.parseInt(request.getParameter("giaBan"))); } catch (Exception e) { tp.setGiaBan(0); }

        Part filePart = request.getPart("hinhAnhFile");
        String originalName = filePart.getSubmittedFileName();

        if (originalName != null && !originalName.isEmpty()) {
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String safeFileName = UUID.randomUUID().toString() + extension;
            filePart.write(uploadPath + File.separator + safeFileName);
            tp.setHinhAnh(safeFileName);

            String oldHinhAnh = request.getParameter("oldHinhAnh");
            if (oldHinhAnh != null && !oldHinhAnh.isEmpty() && !oldHinhAnh.equals("default.png")) {
                new File(uploadPath + File.separator + oldHinhAnh).delete();
            }
        } else {
            String oldHinhAnh = request.getParameter("oldHinhAnh");
            tp.setHinhAnh(oldHinhAnh != null && !oldHinhAnh.isEmpty() ? oldHinhAnh : "default.png");
        }

        if ("add".equals(action)) {
            request.getSession().setAttribute("message", toppingService.add(tp));
        } else if ("update".equals(action)) {
            tp.setMaTopping(request.getParameter("maTopping"));
            request.getSession().setAttribute("message", toppingService.update(tp));
        }
        response.sendRedirect(request.getContextPath() + "/topping?action=list");
    }
}