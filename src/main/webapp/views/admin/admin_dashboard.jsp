<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEA POS - Bảng Điều Khiển Quản Lý</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css?v=99">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/responsive/2.5.0/css/responsive.bootstrap5.min.css">
</head>
<body>
<input type="hidden" id="appContextPath" value="${pageContext.request.contextPath}">
<!-- THÔNG BÁO NỔI -->
<div class="toast-container position-fixed top-0 end-0 p-3 mt-2" style="z-index: 1055;" id="js-toast-container"></div>
<div class="wrapper">
    <!-- NHÚNG SIDEBAR TỪ FILE DÙNG CHUNG -->
    <%@ include file="../layout/sidebar.jsp" %>
    <!-- ================= MAIN CONTENT ================= -->
    <div class="main-content">
        <!-- NAVBAR CỦA ADMIN -->
        <header class="top-navbar bg-white shadow-sm px-4 py-3 d-flex justify-content-between align-items-center mb-4">
            <!-- Mobile Header -->
            <div class="d-flex align-items-center d-lg-none">
                <button class="btn btn-light me-3 border-0 shadow-sm" onclick="toggleSidebar()">
                    <i class="bi bi-list fs-5"></i>
                </button>
                <h5 class="mb-0 fw-bold text-dark">TEA POS</h5>
            </div>
            <!-- Desktop Left Side (Tiêu đề) -->
            <div class="d-none d-lg-flex align-items-center">
                <h4 class="mb-0 fw-bold text-dark me-4"><i class="bi bi-grid-1x2-fill text-brand me-2"></i>Bảng Điều Khiển</h4>
            </div>
            <!-- BỘ LỌC NGÀY & NHÂN VIÊN -->
            <form action="${pageContext.request.contextPath}/admin" method="get" class="filter-form d-none d-md-flex align-items-center gap-2 bg-light rounded-pill p-1 border shadow-sm">
                <div class="d-flex align-items-center px-3 border-end">
                    <span class="small text-muted fw-bold me-2">Từ:</span>
                    <input type="date" class="form-control form-control-sm border-0 bg-transparent shadow-none p-0 fw-medium text-dark" name="tuNgay" value="${requestScope.tuNgay}">
                </div>
                <div class="d-flex align-items-center px-3 border-end">
                    <span class="small text-muted fw-bold me-2">Đến:</span>
                    <input type="date" class="form-control form-control-sm border-0 bg-transparent shadow-none p-0 fw-medium text-dark" name="denNgay" value="${requestScope.denNgay}">
                </div>
                <div class="d-flex align-items-center px-2">
                    <i class="bi bi-person-badge text-primary ms-1 me-2"></i>
                    <select class="form-select form-select-sm border-0 bg-transparent shadow-none fw-medium text-dark p-0 pe-4" name="maNV">
                        <option value="">Mọi nhân viên</option>
                        <c:forEach var="nv" items="${requestScope.danhSachNhanVien}">
                            <option value="${nv.maNV}" ${requestScope.selectedNV == nv.maNV ? 'selected' : ''}>${nv.hoTen}</option>
                        </c:forEach>
                    </select>
                </div>
                <button class="btn btn-sm btn-dark rounded-pill px-3 fw-bold" type="submit">Lọc</button>
                <a href="${pageContext.request.contextPath}/admin" class="btn btn-sm btn-light text-danger rounded-pill px-2 me-1" title="Xóa Lọc"><i class="bi bi-arrow-clockwise"></i></a>
            </form>
            <!-- Profile -->
            <div class="dropdown d-flex align-items-center ms-3">
                <button class="btn btn-light rounded-pill px-3 py-2 fw-semibold border-0 shadow-sm d-flex align-items-center" type="button" data-bs-toggle="dropdown">
                    <img src="https://ui-avatars.com/api/?name=${sessionScope.nhanVienDangNhap.hoTen}&background=D97706&color=fff&rounded=true&bold=true" alt="avatar" width="28" height="28" class="me-2 rounded-circle">
                    <span class="d-none d-sm-inline">${sessionScope.nhanVienDangNhap.hoTen}</span>
                    <i class="bi bi-chevron-down ms-2 small text-muted"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 mt-2 rounded-3">
                    <li><h6 class="dropdown-header d-sm-none">${sessionScope.nhanVienDangNhap.hoTen}</h6></li>
                    <li><a class="dropdown-item text-danger fw-semibold py-2" href="${pageContext.request.contextPath}/auth?action=logout"><i class="bi bi-box-arrow-right me-2"></i> Đăng Xuất</a></li>
                </ul>
            </div>
        </header>
        <!-- NỘI DUNG TRANG CHỦ -->
        <div class="container-fluid px-4 mb-5">
            <!-- Mobile Lọc (Chỉ hiện trên mobile) -->
            <div class="d-block d-md-none mb-4">
                <form action="${pageContext.request.contextPath}/admin" method="get" class="bg-white p-3 rounded-4 shadow-sm border">
                    <div class="row g-2">
                        <div class="col-6"><input type="date" class="form-control form-control-sm" name="tuNgay" value="${requestScope.tuNgay}"></div>
                        <div class="col-6"><input type="date" class="form-control form-control-sm" name="denNgay" value="${requestScope.denNgay}"></div>
                        <div class="col-12">
                            <select class="form-select form-select-sm" name="maNV">
                                <option value="">Mọi nhân viên</option>
                                <c:forEach var="nv" items="${requestScope.danhSachNhanVien}">
                                    <option value="${nv.maNV}" ${requestScope.selectedNV == nv.maNV ? 'selected' : ''}>${nv.hoTen}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-12 d-flex gap-2">
                            <button class="btn btn-sm btn-dark flex-grow-1" type="submit">Lọc</button>
                            <a href="${pageContext.request.contextPath}/admin" class="btn btn-sm btn-outline-danger"><i class="bi bi-arrow-clockwise"></i></a>
                        </div>
                    </div>
                </form>
            </div>
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h6 class="text-muted fw-bold mb-0 text-uppercase small" style="letter-spacing: 0.5px;">Chỉ Số Tổng Quan</h6>
                <span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 px-3 py-2 fs-6 rounded-pill shadow-sm">
<i class="bi bi-graph-up-arrow me-1"></i> Tháng này: <fmt:formatNumber value="${not empty requestScope.thongKe.doanhThuThangNay ? requestScope.thongKe.doanhThuThangNay : 0}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
</span>
            </div>
            <!-- THẺ THỐNG KÊ -->
            <div class="row g-4 mb-4">
                <div class="col-md-6 col-xl-3">
                    <div class="card stat-card border-0 shadow-sm h-100" style="border-radius: 16px;">
                        <div class="card-body d-flex justify-content-between align-items-center p-4">
                            <div>
                                <h6 class="text-muted fw-semibold mb-1 small text-uppercase">DOANH THU KỲ LỌC</h6>
                                <h3 class="fw-bold text-dark mb-0">
                                    <fmt:formatNumber value="${requestScope.thongKe.doanhThuHomNay}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </h3>
                            </div>
                            <div class="stat-icon bg-success bg-opacity-10 text-success rounded-circle d-flex align-items-center justify-content-center" style="width: 50px; height: 50px; font-size: 1.5rem;">
                                <i class="bi bi-cash-stack"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="card stat-card border-0 shadow-sm h-100" style="border-radius: 16px;">
                        <div class="card-body d-flex justify-content-between align-items-center p-4">
                            <div>
                                <h6 class="text-muted fw-semibold mb-1 small text-uppercase">ĐƠN HÀNG KỲ LỌC</h6>
                                <h3 class="fw-bold text-dark mb-0">${requestScope.thongKe.donHangMoi} <span class="fs-6 fw-medium text-muted">đơn</span></h3>
                            </div>
                            <div class="stat-icon bg-primary bg-opacity-10 text-primary rounded-circle d-flex align-items-center justify-content-center" style="width: 50px; height: 50px; font-size: 1.5rem;">
                                <i class="bi bi-receipt-cutoff"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="card stat-card border-0 shadow-sm h-100" style="border-radius: 16px;">
                        <div class="card-body d-flex justify-content-between align-items-center p-4">
                            <div>
                                <h6 class="text-muted fw-semibold mb-1 small text-uppercase">MÓN ĐANG BÁN</h6>
                                <h3 class="fw-bold text-dark mb-0">${requestScope.thongKe.tongSanPham} <span class="fs-6 fw-medium text-muted">món</span></h3>
                            </div>
                            <div class="stat-icon bg-warning bg-opacity-10 text-warning rounded-circle d-flex align-items-center justify-content-center" style="width: 50px; height: 50px; font-size: 1.5rem;">
                                <i class="bi bi-cup-hot-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3">
                    <div class="card stat-card border-0 shadow-sm h-100" style="border-radius: 16px;">
                        <div class="card-body d-flex justify-content-between align-items-center p-4">
                            <div>
                                <h6 class="text-muted fw-semibold mb-1 small text-uppercase">TỔNG KHÁCH HÀNG</h6>
                                <h3 class="fw-bold text-dark mb-0">${requestScope.thongKe.tongKhachHang} <span class="fs-6 fw-medium text-muted">người</span></h3>
                            </div>
                            <div class="stat-icon bg-danger bg-opacity-10 text-danger rounded-circle d-flex align-items-center justify-content-center" style="width: 50px; height: 50px; font-size: 1.5rem;">
                                <i class="bi bi-people-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- PHẦN BIỂU ĐỒ VÀ TOP SẢN PHẨM -->
            <div class="row g-4 mb-4">
                <div class="col-lg-8">
                    <div class="card border-0 shadow-sm h-100" style="border-radius: 16px;">
                        <div class="card-header bg-white border-bottom py-3">
                            <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-bar-chart-fill text-primary me-2"></i>Biểu Đồ Doanh Thu</h6>
                        </div>
                        <div class="card-body p-4">
                            <canvas id="revenueChart" style="max-height: 300px; width: 100%;"></canvas>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm h-100" style="border-radius: 16px;">
                        <div class="card-header bg-white border-bottom py-3">
                            <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-fire text-danger me-2"></i>Top 5 Món Bán Chạy</h6>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-borderless table-hover align-middle mb-0">
                                    <tbody>
                                    <c:choose>
                                        <c:when test="${not empty requestScope.topSanPham}">
                                            <c:forEach var="top" items="${requestScope.topSanPham}" varStatus="stt">
                                                <tr class="border-bottom border-light">
                                                    <td class="ps-4" width="15%">
                                                        <c:choose>
                                                            <c:when test="${stt.index == 0}">
                                                                <div class="bg-warning text-white fw-bold rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 36px; height: 36px;"><i class="bi bi-trophy-fill"></i></div>
                                                            </c:when>
                                                            <c:when test="${stt.index == 1}">
                                                                <div class="bg-secondary text-white fw-bold rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 36px; height: 36px;">2</div>
                                                            </c:when>
                                                            <c:when test="${stt.index == 2}">
                                                                <div class="text-white fw-bold rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 36px; height: 36px; background-color: #CD7F32;">3</div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="bg-light text-muted fw-bold rounded-circle d-flex align-items-center justify-content-center" style="width: 36px; height: 36px;">${stt.index + 1}</div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="fw-semibold text-dark">${top.tenSanPham}</td>
                                                    <td class="text-end pe-4">
                                                        <span class="badge bg-primary bg-opacity-10 text-primary px-3 py-2 fw-bold rounded-pill">${top.soLuongBan} ly</span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="3" class="text-center text-muted py-5">
                                                    <i class="bi bi-cup text-light fs-1 d-block mb-2"></i>
                                                    <span class="fw-medium">Chưa có dữ liệu bán hàng.</span>
                                                </td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- BẢNG DANH SÁCH ĐƠN HÀNG -->
            <div class="row g-4">
                <div class="col-12">
                    <div class="card border-0 shadow-sm mb-4" style="border-radius: 16px;">
                        <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center py-3">
                            <h6 class="mb-0 fw-bold text-dark"><i class="bi bi-receipt text-success me-2"></i>Lịch Sử Giao Dịch Đã Lọc</h6>
                            <span class="badge bg-dark text-white rounded-pill px-3 py-2 shadow-sm">${requestScope.totalRecords} đơn hàng</span>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive" style="max-height: 500px; overflow-y: auto;">
                                <table class="table table-hover table-custom mb-0 text-center align-middle dt-responsive nowrap" style="width:100%" id="orderTable">
                                    <thead class="sticky-top bg-light shadow-sm">
                                    <tr>
                                        <th width="10%">Giờ Tạo</th>
                                        <th width="15%" class="text-start">Mã Hóa Đơn</th>
                                        <th width="20%">Thu Ngân</th>
                                        <th width="20%" class="text-end">Tổng Doanh Thu</th>
                                        <th width="15%">Trạng Thái</th>
                                        <th width="10%">Thao Tác</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="dh" items="${requestScope.listDonHang}">
                                        <tr>
                                            <td class="fw-semibold text-muted"><fmt:formatDate value="${dh.thoiGian}" pattern="HH:mm dd/MM"/></td>
                                            <td class="text-start fw-bold text-dark">${dh.maDH}</td>
                                            <td>
                                                <div class="d-flex align-items-center justify-content-center">
                                                    <div class="bg-primary bg-opacity-10 text-primary rounded-circle d-flex align-items-center justify-content-center fw-bold me-2" style="width:28px; height:28px; font-size:0.75rem;">
                                                            ${fn:substring(dh.tenNhanVien, 0, 1)}
                                                    </div>
                                                    <span class="fw-medium text-dark">${dh.tenNhanVien}</span>
                                                </div>
                                            </td>
                                            <td class="text-end fw-bold text-danger fs-6"><fmt:formatNumber value="${dh.tongTien}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${dh.trangThai == 'Hoàn thành'}">
                                                        <span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 px-3 py-2 rounded-pill">Hoàn thành</span>
                                                    </c:when>
                                                    <c:when test="${dh.trangThai == 'Đã hủy'}">
                                                        <span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 px-3 py-2 rounded-pill">Đã hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-warning bg-opacity-10 text-warning border border-warning border-opacity-25 px-3 py-2 rounded-pill">${dh.trangThai}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <button class="btn btn-light btn-sm rounded-circle shadow-sm text-primary border" style="width: 36px; height: 36px;" title="Xem Hóa Đơn" onclick="viewOrderReceipt('${dh.maDH}')">
                                                    <i class="bi bi-receipt"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <c:if test="${totalPages > 1}">
                                <div class="d-flex justify-content-center mt-0 p-3 bg-white border-top border-light">
                                    <nav>
                                        <ul class="pagination pagination-sm shadow-sm mb-0">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link text-brand fw-bold" href="${pageContext.request.contextPath}/admin?tuNgay=${requestScope.tuNgay}&denNgay=${requestScope.denNgay}&maNV=${requestScope.selectedNV}&page=${currentPage - 1}">Trước</a>
                                            </li>
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link ${currentPage == i ? 'bg-brand border-brand text-white' : 'text-dark'}" href="${pageContext.request.contextPath}/admin?tuNgay=${requestScope.tuNgay}&denNgay=${requestScope.denNgay}&maNV=${requestScope.selectedNV}&page=${i}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link text-brand fw-bold" href="${pageContext.request.contextPath}/admin?tuNgay=${requestScope.tuNgay}&denNgay=${requestScope.denNgay}&maNV=${requestScope.selectedNV}&page=${currentPage + 1}">Sau</a>
                                            </li>
                                        </ul>
                                    </nav>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- MODAL XEM CHI TIẾT ĐƠN HÀNG LẤY TỪ AJAX -->
<div class="modal fade" id="orderDetailModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content border-0 bg-transparent shadow-none">
            <div class="modal-body receipt-view" id="receiptContent">
                <div style="text-align:center; margin-bottom:15px;">
                    <h3 style="font-weight:bold; margin:0; letter-spacing: 1px;">TEA POS</h3>
                    <div style="font-size:12px; margin-top: 5px;">Bản Sao Hóa Đơn</div>
                    <hr>
                </div>
                <div style="font-size:12px; margin-bottom:15px; line-height: 1.6;">
                    <div><span style="font-weight:bold;">Mã HD:</span> <span id="r_maDH">Đang tải...</span></div>
                    <div><span style="font-weight:bold;">Ngày:</span> <span id="r_ngay">...</span></div>
                    <div><span style="font-weight:bold;">Thu ngân:</span> <span id="r_nhanVien">...</span></div>
                    <div><span style="font-weight:bold;">Khách:</span> <span id="r_khach">...</span></div>
                    <div><span style="font-weight:bold;">TT:</span> <span id="r_pttt">...</span></div>
                </div>
                <hr>
                <div style="font-size:12px;">
                    <table style="width:100%; border-collapse:collapse;" id="r_items">
                        <tr><td class="text-center py-3"><div class="spinner-border text-brand" role="status"></div></td></tr>
                    </table>
                </div>
                <hr>
                <div style="font-size:12px; line-height: 1.6;">
                    <div style="display:flex; justify-content:space-between;"><span>Tổng cộng:</span><span style="font-weight:bold;" id="r_tongTienHang">0</span></div>
                    <div style="display:flex; justify-content:space-between;"><span>Giảm giá/Điểm:</span><span id="r_giamGia">0</span></div>
                    <hr>
                    <div style="display:flex; justify-content:space-between; font-weight:bold; font-size:16px; margin-top:5px; margin-bottom:5px;"><span>THANH TOÁN:</span><span id="r_tongPhaiTra">0</span></div>
                    <div style="display:flex; justify-content:space-between;"><span>Tiền nhận:</span><span id="r_tienKhachDua">0</span></div>
                    <div style="display:flex; justify-content:space-between;"><span>Tiền thối:</span><span id="r_tienThoi">0</span></div>
                </div>
                <hr>
                <div style="text-align:center; font-size:11px; margin-top:15px; color:#555;">Bản sao được trích xuất từ hệ thống.</div>
            </div>
            <div class="text-center mt-3 pb-4">
                <button type="button" class="btn btn-light fw-bold px-4 rounded-pill shadow-sm" data-bs-dismiss="modal">Đóng Bản Sao</button>
            </div>
        </div>
    </div>
</div>
<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdn.datatables.net/responsive/2.5.0/js/dataTables.responsive.min.js"></script>
<script src="https://cdn.datatables.net/responsive/2.5.0/js/responsive.bootstrap5.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/global.js"></script>
<script>
    // --- HỆ THỐNG TOAST ---
    function showToast(message, type = 'danger') {
        let icon = type === 'danger' ? 'bi-exclamation-triangle-fill' : (type === 'success' ? 'bi-check-circle-fill' : 'bi-info-circle-fill');
        let bgClass = type === 'danger' ? 'alert-danger' : (type === 'success' ? 'alert-success' : 'alert-warning text-dark');
        let toastHTML = `
<div class="toast align-items-center border-0 mb-2 shadow-lg rounded-3 \${bgClass}" role="alert" aria-live="assertive" aria-atomic="true">
<div class="d-flex">
<div class="toast-body fw-bold d-flex align-items-center"><i class="bi \${icon} me-2 fs-5"></i> \${message}</div>
<button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
</div>
</div>`;
        let container = document.getElementById('js-toast-container');
        container.insertAdjacentHTML('beforeend', toastHTML);
        let toastEl = container.lastElementChild;
        let bsToast = new bootstrap.Toast(toastEl, { delay: 4000 });
        bsToast.show();
        toastEl.addEventListener('hidden.bs.toast', () => { toastEl.remove(); });
    }
    // --- XỬ LÝ LẤY CHI TIẾT HÓA ĐƠN QUA AJAX ---
    const orderModal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
    const appBasePath = document.getElementById('appContextPath').value;
    function viewOrderReceipt(maDH) {
        document.getElementById('r_maDH').innerText = "Đang tải...";
        document.getElementById('r_items').innerHTML = '<tr><td class="text-center py-3"><div class="spinner-border text-warning" role="status"></div></td></tr>';
        ['r_nhanVien', 'r_khach', 'r_ngay', 'r_pttt', 'r_tongTienHang', 'r_giamGia', 'r_tongPhaiTra', 'r_tienKhachDua', 'r_tienThoi'].forEach(id => {
            document.getElementById(id).innerText = "...";
        });
        orderModal.show();
        fetch(appBasePath + '/admin?action=get-receipt&maDH=' + maDH)
            .then(res => {
                if(!res.ok) throw new Error("Máy chủ trả về trạng thái lỗi: " + res.status);
                return res.text();
            })
            .then(text => {
                try {
                    let data = JSON.parse(text);
                    if(data.error) {
                        document.getElementById('r_items').innerHTML = `<tr><td class="text-center text-danger py-4 fw-bold"><i class="bi bi-x-circle fs-2 d-block mb-2"></i>\${data.error}</td></tr>`;
                        document.getElementById('r_maDH').innerText = "Lỗi Dữ Liệu";
                        return;
                    }
                    document.getElementById('r_maDH').innerText = data.maDH;
                    document.getElementById('r_nhanVien').innerText = data.nhanVien;
                    document.getElementById('r_khach').innerText = data.khachHang || 'Khách vãng lai';
                    document.getElementById('r_ngay').innerText = data.ngay;
                    document.getElementById('r_pttt').innerText = data.phuongThuc;
                    let tbody = document.getElementById('r_items');
                    tbody.innerHTML = '';
                    if(!data.items || data.items.length === 0) {
                        tbody.innerHTML = '<tr><td class="text-center py-2">Không có chi tiết.</td></tr>';
                    } else {
                        data.items.forEach(item => {
                            let tr = document.createElement('tr');
                            let basePriceFormatted = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.giaChot * item.soLuong);
// Tên món + Giá gốc
                            tr.innerHTML = `<td style="font-weight:bold; padding-top: 8px; vertical-align: top;">\${item.soLuong} x \${item.tenMon}</td>
<td style="text-align:right; padding-top: 8px; vertical-align: top;">\${basePriceFormatted}</td>`;
                            tbody.appendChild(tr);
// Tùy chọn
                            let sizeText = item.size ? item.size : 'M';
                            let trDetails = document.createElement('tr');
                            trDetails.innerHTML = `<td colspan="2" style="padding-left:12px; color:#555; font-size:11px; padding-bottom: 2px;">- Size \${sizeText}, \${item.da} Đá, \${item.duong} Đường</td>`;
                            tbody.appendChild(trDetails);
                            let tongTien1Mon = item.giaChot * item.soLuong;
                            if(item.toppings && item.toppings.length > 0) {
                                item.toppings.forEach(tp => {
                                    let tpTotal = tp.gia * tp.sl;
                                    tongTien1Mon += tpTotal;
                                    let tpPriceFormatted = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(tpTotal);
                                    let trTp = document.createElement('tr');
                                    trTp.innerHTML = `<td style="padding-left:12px; color:#555; font-size:11px;">+ \${tp.sl} x \${tp.ten}</td>
<td style="text-align:right; color:#555; font-size:11px;">\${tpPriceFormatted}</td>`;
                                    tbody.appendChild(trTp);
                                });
                            }
// Tổng món
                            let trPrice = document.createElement('tr');
                            let priceFormatted = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(tongTien1Mon);
                            trPrice.innerHTML = `<td colspan="2" style="text-align:right; font-size: 11px; padding-top: 4px; padding-bottom: 8px; border-bottom: 1px dashed #ccc;">
<span style="color:#555; margin-right: 5px;">Thành tiền:</span>
<span style="font-size: 12px; font-weight: bold;">\${priceFormatted}</span>
</td>`;
                            tbody.appendChild(trPrice);
                        });
                    }
                    const fmtCur = (num) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num);
                    document.getElementById('r_tongTienHang').innerText = fmtCur(data.tongTienHang);
                    document.getElementById('r_giamGia').innerText = "-" + fmtCur(data.tienGiamGia);
                    document.getElementById('r_tongPhaiTra').innerText = fmtCur(data.tongPhaiTra);
                    document.getElementById('r_tienKhachDua').innerText = fmtCur(data.tienKhachDua);
                    document.getElementById('r_tienThoi').innerText = fmtCur(data.tienKhachDua - data.tongPhaiTra);
                } catch(e) {
                    console.error("Lỗi parse JSON:", text);
                    document.getElementById('r_items').innerHTML = '<tr><td class="text-center text-danger py-4 fw-bold">Dữ liệu từ máy chủ bị hỏng!<br><small>Kiểm tra dữ liệu đặc biệt.</small></td></tr>';
                    document.getElementById('r_maDH').innerText = "Lỗi Định Dạng";
                }
            })
            .catch(err => {
                console.error("Lỗi fetch:", err);
                document.getElementById('r_items').innerHTML = `<tr><td class="text-center text-danger py-4 fw-bold">Mất kết nối máy chủ!<br><small>\${err.message}</small></td></tr>`;
            });
    }
    // Biểu đồ
    document.addEventListener("DOMContentLoaded", function() {
        try {
            const labelsString = '${requestScope.chartLabels != null ? requestScope.chartLabels : "[]"}';
            const dataValuesString = '${requestScope.chartValues != null ? requestScope.chartValues : "[]"}';
            let labels = [];
            let dataValues = [];
            if (!labelsString.startsWith("$")) { labels = JSON.parse(labelsString); }
            if (!dataValuesString.startsWith("$")) { dataValues = JSON.parse(dataValuesString); }
            if (labels.length > 0) {
                const ctx = document.getElementById('revenueChart').getContext('2d');
                let gradient = ctx.createLinearGradient(0, 0, 0, 400);
                gradient.addColorStop(0, 'rgba(217, 119, 6, 0.8)');
                gradient.addColorStop(1, 'rgba(217, 119, 6, 0.1)');
                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Doanh thu (VNĐ)',
                            data: dataValues,
                            backgroundColor: gradient,
                            borderColor: '#D97706',
                            borderWidth: 1,
                            borderRadius: 8,
                            barPercentage: 0.5
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                backgroundColor: '#0F172A',
                                titleFont: { family: 'Plus Jakarta Sans', size: 13 },
                                bodyFont: { family: 'Plus Jakarta Sans', size: 14, weight: 'bold' },
                                padding: 12,
                                cornerRadius: 8,
                                callbacks: {
                                    label: function(context) {
                                        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.raw);
                                    }
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                grid: { borderDash: [8], color: '#E2E8F0', drawBorder: false },
                                ticks: {
                                    font: { family: 'Plus Jakarta Sans' },
                                    color: '#64748B',
                                    callback: function(value) {
                                        if(value >= 1000000) return (value / 1000000) + 'M';
                                        if(value >= 1000) return (value / 1000) + 'k';
                                        return value;
                                    }
                                }
                            },
                            x: {
                                grid: { display: false, drawBorder: false },
                                ticks: { font: { family: 'Plus Jakarta Sans' }, color: '#64748B' }
                            }
                        }
                    }
                });
            } else {
                let canvasParent = document.getElementById('revenueChart').parentElement;
                canvasParent.innerHTML = '<div class="text-center text-muted py-5"><i class="bi bi-graph-down text-light fs-1 d-block mb-2"></i><span class="fw-medium">Chưa có đủ dữ liệu giao dịch để vẽ biểu đồ.</span></div>';
            }
        } catch (e) {
            console.error("Lỗi vẽ biểu đồ:", e);
        }
    });
    $(document).ready(function() {
        try {
            if ($('#orderTable').length > 0) {
                $('#orderTable').DataTable({
                    "responsive": true,
                    "paging": false,
                    "searching": false,
                    "info": false,
                    "order": [],
                    "columnDefs": [ { "orderable": false, "targets": [9] } ],
                    "language": {
                        "emptyTable": '<div class="d-flex flex-column align-items-center py-4"><i class="bi bi-inbox fs-1 text-light mb-3"></i><p class="mb-0 fw-medium text-muted">Chưa có giao dịch nào phù hợp với bộ lọc.</p></div>'
                    }
                });
            }
        } catch (e) {
            console.error("Lỗi khởi tạo DataTable:", e);
        }
    });
</script>
</body>
</html>