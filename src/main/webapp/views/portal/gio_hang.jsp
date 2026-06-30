<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Giỏ Hàng O2O - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-light bg-warning shadow-sm sticky-top py-3">
    <div class="container d-flex justify-content-between align-items-center">
        <a href="${pageContext.request.contextPath}/portal/trang-chu" class="text-decoration-none fw-bold text-dark"><i class="bi bi-arrow-left"></i> Về Menu</a>
        <h5 class="mb-0 fw-bold text-dark">Giỏ Hàng Click & Collect</h5>
    </div>
</nav>
<div class="container mt-4" style="max-width: 600px;">
    <c:choose>
        <c:when test="${empty requestScope.myCart or empty requestScope.myCart.danhSachChiTiet}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm border mt-5">
                <i class="bi bi-cart-x text-muted opacity-50" style="font-size: 5rem;"></i>
                <h4 class="fw-bold mt-3">Giỏ hàng đang trống</h4>
                <a href="${pageContext.request.contextPath}/portal/trang-chu" class="btn btn-warning fw-bold mt-3 rounded-pill px-4">Mua Ngay</a>
            </div>
        </c:when>
        <c:otherwise>
            <form action="${pageContext.request.contextPath}/portal/trang-chu" method="post">
                <input type="hidden" name="action" value="checkout">
                <div class="card border-0 shadow-sm rounded-4 mb-3">
                    <div class="card-body p-4">
                        <c:set var="tongTien" value="0"/>
                        <c:forEach var="item" items="${requestScope.myCart.danhSachChiTiet}">
                            <div class="d-flex justify-content-between mb-3 border-bottom pb-2">
                                <div>
                                    <h6 class="fw-bold mb-1">${item.soLuong}x ${item.bienThe.sanPham.tenSanPham} (Size ${item.bienThe.kichCo})</h6>
                                    <small class="text-muted">${item.mucDaDuong}</small>
                                </div>
                                <div class="fw-bold text-danger">${item.bienThe.giaBan * item.soLuong}đ</div>
                            </div>
                            <c:set var="tongTien" value="${tongTien + (item.bienThe.giaBan * item.soLuong)}"/>
                        </c:forEach>
                        <h4 class="fw-bold text-end mt-3">Tổng: <span class="text-danger">${tongTien}đ</span></h4>
                    </div>
                </div>

                <div class="card border-0 shadow-sm rounded-4 mb-4">
                    <div class="card-body p-4 bg-light rounded-4">
                        <label class="fw-bold mb-2"><i class="bi bi-clock"></i> Chọn giờ hẹn lấy nước:</label>
                        <input type="time" name="gioHenLay" class="form-control form-control-lg fw-bold text-primary mb-3" required>

                        <label class="fw-bold mb-2"><i class="bi bi-wallet2"></i> Thanh toán:</label>
                        <select class="form-select form-select-lg fw-bold mb-3"><option>Thanh toán tại quầy</option></select>
                    </div>
                </div>

                <button type="submit" class="btn btn-success w-100 py-3 fs-5 fw-bold rounded-pill shadow-lg"><i class="bi bi-check-circle"></i> ĐẶT HÀNG TRƯỚC</button>
            </form>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>