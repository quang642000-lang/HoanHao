<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng Nhập Hệ Thống</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body class="bg-light d-flex align-items-center vh-100">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-sm border-0 rounded-3">
                <div class="card-header bg-primary text-white text-center py-3">
                    <h4 class="mb-0 fw-bold"><i class="bi bi-person-circle"></i> ĐĂNG NHẬP</h4>
                </div>
                <div class="card-body p-4">
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger shadow-sm"><i class="bi bi-exclamation-triangle-fill"></i> ${requestScope.error}</div>
                    </c:if>
                    <c:if test="${not empty requestScope.message}">
                        <div class="alert alert-success shadow-sm"><i class="bi bi-check-circle-fill"></i> ${requestScope.message}</div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/auth" method="post">
                        <input type="hidden" name="action" value="login">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Tên Đăng Nhập</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-person"></i></span>
                                <input type="text" class="form-control" name="username" placeholder="Nhập tài khoản..." required>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Mật Khẩu</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-lock"></i></span>
                                <input type="password" class="form-control" name="password" placeholder="Nhập mật khẩu..." required>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 fw-bold mb-3 py-2">
                            <i class="bi bi-box-arrow-in-right"></i> Đăng Nhập
                        </button>
                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/auth?action=forgot" class="text-decoration-none text-primary fw-medium">Quên Mật Khẩu?</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>