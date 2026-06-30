package controller.pos;

import util.ConfigUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ImageServlet", value = "/image/*")
public class ImageServlet extends HttpServlet {
    private static final String BASE_PATH = ConfigUtil.getUploadDir();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = URLDecoder.decode(pathInfo.substring(1), StandardCharsets.UTF_8.name());
        try {
            File baseDir = new File(BASE_PATH);
            File file = new File(baseDir, fileName);

            // BẢO MẬT: Ngăn chặn Hacker dùng ../../ để đọc file hệ thống
            if (!file.getCanonicalPath().startsWith(baseDir.getCanonicalPath())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            if (!file.exists() || file.isDirectory()) {
                String defaultPath = getServletContext().getRealPath("") + File.separator + "assets" + File.separator + "img" + File.separator + "default.png";
                file = new File(defaultPath);
                if (!file.exists()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }

            String contentType = getServletContext().getMimeType(file.getName());
            if (contentType == null) contentType = "application/octet-stream";

            response.setContentType(contentType);
            response.setContentLength((int) file.length());

            try (FileInputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}