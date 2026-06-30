package controller.admin;

import model.DanhMuc;
import model.SanPham;
import service.ISanPhamService;
import service.IDanhMucService;
import service.impl.SanPhamServiceImpl;
import service.impl.DanhMucServiceImpl;
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

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
@WebServlet(name = "SanPhamController", value = "/san-pham")
public class SanPhamController extends HttpServlet {
    private ISanPhamService sanPhamService = new SanPhamServiceImpl();
    private IDanhMucService danhMucService = new DanhMucServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "toggle-status":
                String maSpToggle = request.getParameter("id");
                int trangThaiMoi = Integer.parseInt(request.getParameter("status"));
                request.getSession().setAttribute("message", sanPhamService.updateTrangThai(maSpToggle, trangThaiMoi));
                response.sendRedirect(request.getContextPath() + "/san-pham?action=list");
                break;
            case "delete":
                String maSpDel = request.getParameter("id");
                request.getSession().setAttribute("message", sanPhamService.delete(maSpDel));
                response.sendRedirect(request.getContextPath() + "/san-pham?action=list");
                break;
            case "search":
                String keyword = request.getParameter("keyword");
                String filterDanhMuc = request.getParameter("filterDanhMuc");
                request.setAttribute("danhSachSp", sanPhamService.search(keyword, filterDanhMuc));
                request.setAttribute("danhSachDm", danhMucService.getAll());
                request.setAttribute("selectedKeyword", keyword);
                request.setAttribute("selectedDanhMuc", filterDanhMuc);
                request.getRequestDispatcher("/views/admin/san_pham.jsp").forward(request, response);
                break;
            case "list":
            default:
                int page = 1;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try { page = Integer.parseInt(pageParam); } catch (Exception e) {}
                }
                request.setAttribute("danhSachSp", sanPhamService.getAllByPage(page));
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", sanPhamService.getTotalPages());
                request.setAttribute("danhSachDm", danhMucService.getAll());
                request.getRequestDispatcher("/views/admin/san_pham.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String uploadPath = ConfigUtil.getUploadDir();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        if ("add".equals(action)) {
            SanPham sp = new SanPham();
            sp.setTenSanPham(request.getParameter("tenSanPham"));

            Part filePart = request.getPart("hinhAnhFile");
            String originalName = filePart.getSubmittedFileName();
            if (originalName != null && !originalName.isEmpty()) {
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String safeFileName = UUID.randomUUID().toString() + extension;
                filePart.write(uploadPath + File.separator + safeFileName);
                sp.setHinhAnh(safeFileName);
            } else {
                sp.setHinhAnh("default.png");
            }

            DanhMuc dm = new DanhMuc();
            dm.setMaDanhMuc(request.getParameter("maDanhMuc"));
            sp.setDanhMuc(dm);

            request.getSession().setAttribute("message", sanPhamService.add(sp));
        } else if ("update".equals(action)) {
            SanPham sp = new SanPham();
            sp.setMaSP(request.getParameter("maSP"));
            sp.setTenSanPham(request.getParameter("tenSanPham"));

            Part filePart = request.getPart("hinhAnhFile");
            String originalName = filePart.getSubmittedFileName();
            if (originalName != null && !originalName.isEmpty()) {
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String safeFileName = UUID.randomUUID().toString() + extension;
                filePart.write(uploadPath + File.separator + safeFileName);
                sp.setHinhAnh(safeFileName);

                String oldHinhAnh = request.getParameter("oldHinhAnh");
                if (oldHinhAnh != null && !oldHinhAnh.isEmpty() && !oldHinhAnh.equals("default.png")) {
                    new File(uploadPath + File.separator + oldHinhAnh).delete();
                }
            } else {
                String oldHinhAnh = request.getParameter("oldHinhAnh");
                sp.setHinhAnh(oldHinhAnh != null && !oldHinhAnh.isEmpty() ? oldHinhAnh : "default.png");
            }

            DanhMuc dm = new DanhMuc();
            dm.setMaDanhMuc(request.getParameter("maDanhMuc"));
            sp.setDanhMuc(dm);

            request.getSession().setAttribute("message", sanPhamService.update(sp));
        }
        response.sendRedirect(request.getContextPath() + "/san-pham?action=list");
    }
}
