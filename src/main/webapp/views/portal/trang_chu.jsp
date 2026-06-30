<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thực Đơn Mua Sắm - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root { --brand-primary: #D97706; }
        body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F8FAFC; padding-top: 70px; }
        .navbar-o2o { background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); }
        .hero-banner {
            background: linear-gradient(135deg, #F59E0B, #B45309);
            color: white; padding: 3rem 1.5rem; border-radius: 20px;
            margin-bottom: 2rem; box-shadow: 0 10px 30px rgba(217, 119, 6, 0.2);
        }
        .category-scroll::-webkit-scrollbar { display: none; }
        .btn-cat { border-radius: 99px; padding: 0.5rem 1.5rem; font-weight: 600; color: #475569; border: 1px solid #E2E8F0; background: white; white-space: nowrap; transition: 0.2s; text-decoration: none;}
        .btn-cat.active { background: var(--brand-primary); color: white; border-color: var(--brand-primary); box-shadow: 0 4px 10px rgba(217, 119, 6, 0.3); }
        .product-card { border: 1px solid #E2E8F0; border-radius: 16px; overflow: hidden; transition: 0.3s; background: white; cursor: pointer; }
        .product-card:hover { transform: translateY(-5px); box-shadow: 0 15px 25px rgba(0,0,0,0.05); border-color: var(--brand-primary); }
        .product-img { width: 100%; height: 180px; object-fit: cover; transition: 0.3s; }
        .product-card:hover .product-img { transform: scale(1.05); }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light shadow-sm fixed-top navbar-o2o border-bottom">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/portal/trang-chu" style="color: var(--brand-primary);">
            <i class="bi bi-cup-straw fs-4 me-1"></i> TEA POS
        </a>
        <div class="d-flex align-items-center gap-2">
            <c:choose>
                <c:when test="${not empty sessionScope.khachHangDangNhap}">
                    <a href="${pageContext.request.contextPath}/portal/ho-so" class="btn btn-light fw-bold rounded-pill border shadow-sm text-dark px-3">
                        <i class="bi bi-person-circle text-primary"></i> ${sessionScope.khachHangDangNhap.tenKH}
                    </a>
                </c:when>
                <c:otherwise>
                    <button class="btn btn-outline-dark fw-bold rounded-pill px-3 shadow-sm" data-bs-toggle="modal" data-bs-target="#loginModal">
                        <i class="bi bi-box-arrow-in-right"></i> Hội Viên
                    </button>
                </c:otherwise>
            </c:choose>
            <a href="${pageContext.request.contextPath}/portal/trang-chu?action=cart" class="btn fw-bold rounded-pill shadow-sm px-3 text-white" style="background-color: var(--brand-primary);">
                <i class="bi bi-bag-check-fill"></i>
            </a>
        </div>
    </div>
</nav>

<div class="container mb-5">
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success shadow-sm rounded-4 fw-bold mt-3 border-0 bg-success text-white"><i class="bi bi-check-circle-fill me-2"></i> ${sessionScope.message}</div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <div class="hero-banner mt-3 text-center">
        <h1 class="fw-bold display-6 mb-3">Thưởng Thức Trà Sữa Tuyệt Hảo</h1>
        <p class="fs-5 mb-0 fw-medium opacity-75">Click & Collect - Đặt hàng trực tuyến, không chờ đợi!</p>
    </div>

    <!-- Thanh Cuộn Danh Mục -->
    <div class="d-flex category-scroll gap-2 overflow-auto mb-4 pb-2">
        <a href="${pageContext.request.contextPath}/portal/trang-chu" class="btn-cat ${requestScope.selectedDanhMuc == 'all' ? 'active' : ''}">Tất Cả Món</a>
        <c:forEach var="dm" items="${requestScope.danhSachDanhMuc}">
            <a href="${pageContext.request.contextPath}/portal/trang-chu?danhMuc=${dm.maDanhMuc}" class="btn-cat ${requestScope.selectedDanhMuc == dm.maDanhMuc ? 'active' : ''}">${dm.tenDanhMuc}</a>
        </c:forEach>
    </div>

    <!-- Lưới Sản Phẩm -->
    <div class="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3 g-md-4">
        <c:forEach var="sp" items="${requestScope.danhSachSanPham}">
            <c:if test="${sp.trangThai == 1}">
                <div class="col">
                    <div class="product-card h-100 d-flex flex-column" onclick="alert('Module Chọn Topping O2O sẽ được phát triển tiếp ở chặng sau!')">
                        <div class="overflow-hidden">
                            <img src="${pageContext.request.contextPath}/image/${not empty sp.hinhAnh ? sp.hinhAnh : 'default.png'}" class="product-img" onerror="this.src='https://placehold.co/300x300?text=No+Image'">
                        </div>
                        <div class="card-body text-center p-3 d-flex flex-column justify-content-between flex-grow-1">
                            <h6 class="fw-bold text-dark mb-3">${sp.tenSanPham}</h6>
                            <button class="btn btn-outline-danger fw-bold rounded-pill w-100 mt-auto"><i class="bi bi-plus-lg"></i> Chọn Món</button>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
        <c:if test="${empty requestScope.danhSachSanPham}">
            <div class="col-12 text-center py-5">
                <i class="bi bi-inbox fs-1 text-muted opacity-50"></i>
                <p class="mt-2 text-muted fw-bold">Chưa có món ăn nào trong danh mục này.</p>
            </div>
        </c:if>
    </div>
</div>

<!-- Modal Đăng Nhập Mini O2O -->
<div class="modal fade" id="loginModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content shadow-lg border-0 rounded-4">
            <div class="modal-header bg-light border-0">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-person-bounding-box text-primary me-2"></i>Đăng Nhập Nhận Điểm</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/portal/ho-so" method="post">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="login">
                    <label class="form-label fw-bold small text-muted text-uppercase">Số Điện Thoại Đăng Ký</label>
                    <input type="tel" class="form-control form-control-lg fw-bold text-primary" name="sdt" placeholder="Nhập SĐT..." pattern="\d{10,11}" required>
                </div>
                <div class="modal-footer border-0 bg-light p-3">
                    <button type="submit" class="btn btn-primary w-100 fw-bold rounded-pill py-2 shadow-sm">Xác Nhận & Đăng Nhập</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>