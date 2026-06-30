<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ Hàng O2O - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F8FAFC; }
        .card-custom { border-radius: 20px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.05); }
    </style>
</head>
<body>
<nav class="navbar navbar-light bg-white shadow-sm sticky-top border-bottom py-3">
    <div class="container d-flex justify-content-between align-items-center">
        <a href="${pageContext.request.contextPath}/portal/trang-chu" class="text-decoration-none fw-bold text-dark fs-5">
            <i class="bi bi-arrow-left"></i> Quay lại
        </a>
        <h5 class="mb-0 fw-bold text-dark d-none d-sm-block">Giỏ Hàng Của Bạn</h5>
        <div style="width: 100px;"></div>
    </div>
</nav>

<div class="container mt-5 mb-5" style="max-width: 700px;">
    <div class="card card-custom p-5 text-center bg-white">
        <div class="mb-4">
            <div class="d-inline-flex align-items-center justify-content-center rounded-circle bg-light" style="width: 120px; height: 120px;">
                <i class="bi bi-bag-x text-muted opacity-50" style="font-size: 4rem;"></i>
            </div>
        </div>
        <h3 class="fw-bold text-dark mb-3">Giỏ hàng đang trống!</h3>
        <p class="text-muted fw-medium mb-4 fs-5">Bạn chưa chọn món đồ uống nào vào giỏ hàng. Hãy khám phá các món ngon của TEA POS nhé!</p>
        <a href="${pageContext.request.contextPath}/portal/trang-chu" class="btn fw-bold rounded-pill shadow-sm px-5 py-3 fs-5 text-white" style="background-color: #D97706;">
            <i class="bi bi-cup-hot-fill me-2"></i> Trở Lại Thực Đơn
        </a>
    </div>
</div>
</body>
</html>