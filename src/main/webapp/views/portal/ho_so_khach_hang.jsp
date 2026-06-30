<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ Sơ Của Tôi - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F8FAFC; }
        .profile-card { border-radius: 20px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.05); overflow: hidden; }
        .profile-header { background: linear-gradient(135deg, #2563EB, #1D4ED8); padding: 3rem 1.5rem; text-align: center; color: white; position: relative; }
        .avatar-circle { width: 100px; height: 100px; background: white; color: #2563EB; font-size: 3rem; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; box-shadow: 0 10px 20px rgba(0,0,0,0.2); margin-bottom: 1rem; border: 4px solid rgba(255,255,255,0.2); background-clip: padding-box; }
    </style>
</head>
<body>
<nav class="navbar navbar-light bg-white shadow-sm sticky-top border-bottom py-3">
    <div class="container">
        <a href="${pageContext.request.contextPath}/portal/trang-chu" class="text-decoration-none fw-bold text-dark fs-5">
            <i class="bi bi-house-door-fill text-primary"></i> Về Cửa Hàng
        </a>
        <form action="${pageContext.request.contextPath}/portal/ho-so" method="post" class="m-0">
            <input type="hidden" name="action" value="logout">
            <button type="submit" class="btn btn-light text-danger border fw-bold rounded-pill shadow-sm px-4">
                Thoát Khỏi Máy <i class="bi bi-box-arrow-right ms-1"></i>
            </button>
        </form>
    </div>
</nav>

<div class="container mt-4 mb-5" style="max-width: 600px;">
    <div class="card profile-card bg-white">
        <div class="profile-header">
            <div class="avatar-circle">
                <i class="bi bi-person-fill"></i>
            </div>
            <h3 class="fw-bold mb-1">${sessionScope.khachHangDangNhap.tenKH}</h3>
            <p class="mb-0 fw-medium opacity-75 fs-5"><i class="bi bi-telephone-fill"></i> ${sessionScope.khachHangDangNhap.SDT}</p>
        </div>

        <div class="card-body p-4 p-md-5">
            <div class="row text-center mb-4 g-3">
                <div class="col-6">
                    <div class="bg-light p-3 rounded-4 border h-100">
                        <h6 class="text-muted fw-bold text-uppercase small mb-2" style="letter-spacing: 1px;">Điểm Tích Lũy</h6>
                        <h2 class="fw-bold text-warning mb-0"><i class="bi bi-stars"></i> ${sessionScope.khachHangDangNhap.diemTichLuy}</h2>
                        <small class="text-muted fw-medium mt-1 d-block">1đ = 1.000 VNĐ</small>
                    </div>
                </div>
                <div class="col-6">
                    <div class="bg-light p-3 rounded-4 border h-100">
                        <h6 class="text-muted fw-bold text-uppercase small mb-2" style="letter-spacing: 1px;">Hạng Thẻ Nhận Diện</h6>
                        <h2 class="fw-bold text-primary mb-0">${sessionScope.khachHangDangNhap.hangThanhVien}</h2>
                    </div>
                </div>
            </div>

            <hr class="border-secondary opacity-10 my-4">

            <h5 class="fw-bold text-dark mb-3"><i class="bi bi-clock-history text-primary me-2"></i>Giao Dịch Gần Đây Nhất</h5>
            <div class="text-center py-5 bg-light rounded-4 border border-dashed border-2">
                <i class="bi bi-receipt text-muted opacity-25" style="font-size: 3.5rem;"></i>
                <p class="text-muted fw-bold mt-3 mb-0">Tính năng tra cứu lịch sử mua hàng O2O <br> đang được hoàn thiện!</p>
            </div>
        </div>
    </div>
</div>
</body>
</html>