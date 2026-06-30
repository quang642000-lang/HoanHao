<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt Lại Mật Khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body class="bg-light d-flex align-items-center vh-100">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-sm border-0 rounded-3">
                <div class="card-header bg-success text-white text-center py-3">
                    <h4 class="mb-0 fw-bold"><i class="bi bi-key"></i> TẠO MẬT KHẨU MỚI</h4>
                </div>
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-check-circle text-success" style="font-size: 3rem;"></i>
                        <p class="text-muted mt-2">Xác minh Email thành công! Vui lòng nhập mật khẩu mới.</p>
                    </div>
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger shadow-sm"><i class="bi bi-exclamation-triangle-fill"></i> ${requestScope.error}</div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/auth" method="post">
                        <input type="hidden" name="action" value="reset">
                        <div class="mb-4">
                            <label class="form-label fw-bold">Mật Khẩu Mới</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                                <input type="password" class="form-control" name="newPassword" placeholder="Nhập mật khẩu mới an toàn..." required minlength="6">
                            </div>
                        </div>
                        <button type="submit" class="btn btn-success w-100 fw-bold py-2">
                            Lưu Thay Đổi <i class="bi bi-floppy"></i>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>