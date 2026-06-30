<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Khôi Phục Mật Khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body class="bg-light d-flex align-items-center vh-100">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-sm border-0 rounded-3">
                <div class="card-header bg-warning text-dark text-center py-3">
                    <h4 class="mb-0 fw-bold"><i class="bi bi-envelope-at-fill"></i> KHÔI PHỤC MẬT KHẨU</h4>
                </div>
                <div class="card-body p-4">
                    <p class="text-muted text-center small mb-4">Vui lòng nhập địa chỉ Email đã liên kết với tài khoản của bạn để nhận mã xác thực OTP.</p>
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger shadow-sm"><i class="bi bi-exclamation-triangle-fill"></i> ${requestScope.error}</div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/auth" method="post">
                        <input type="hidden" name="action" value="send-otp">
                        <div class="mb-4">
                            <label class="form-label fw-bold">Địa Chỉ Email Tài Khoản</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                                <input type="email" class="form-control" name="email" placeholder="Nhập địa chỉ email đăng ký..." required>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-warning w-100 fw-bold mb-3 py-2 text-dark">
                            Xác Nhận & Gửi OTP <i class="bi bi-send"></i>
                        </button>
                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/auth?action=login" class="text-decoration-none text-secondary fw-medium">
                                <i class="bi bi-arrow-left"></i> Quay lại Đăng nhập
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>