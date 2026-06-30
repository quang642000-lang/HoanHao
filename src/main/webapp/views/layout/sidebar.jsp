<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<div class="sidebar-overlay" id="sidebarOverlay" onclick="toggleSidebar()"></div>
<nav class="sidebar" id="sidebar">
    <div class="sidebar-header d-flex align-items-center justify-content-center">
        <a href="${pageContext.request.contextPath}/admin" class="text-decoration-none">
            <h4 class="mb-0 fw-bold d-flex align-items-center" style="color: var(--brand-primary); letter-spacing: 1px;">
                <i class="bi bi-cup-straw fs-3 me-2"></i> TEA POS
            </h4>
        </a>
    </div>

    <div class="sidebar-menu" id="main-sidebar-menu">
        <div class="text-uppercase text-white-50 fw-bold small px-3 mb-2 mt-2" style="font-size: 0.7rem; letter-spacing: 1px;">Tổng Quan</div>
        <a href="${pageContext.request.contextPath}/admin" class="menu-item" data-path="/admin"><i class="bi bi-grid-1x2-fill"></i> <span>Bảng Điều Khiển</span></a>
        <!-- BỔ SUNG KHO LƯU TRỮ HÓA ĐƠN -->
        <a href="${pageContext.request.contextPath}/quan-ly-don-hang" class="menu-item" data-path="/quan-ly-don-hang"><i class="bi bi-receipt-cutoff"></i> <span>Kho Lưu Trữ Hóa Đơn</span></a>
        <!-- CẬP NHẬT ĐƯỜNG DẪN MÁY POS -->
        <a href="${pageContext.request.contextPath}/nhan-don" class="menu-item"><i class="bi bi-bell"></i> <span>Nhận Đơn (O2O)</span></a>
        <a href="${pageContext.request.contextPath}/pos" class="menu-item text-warning" target="_blank"><i class="bi bi-display"></i> <span>Mở Máy POS</span></a>
        <div class="text-uppercase text-white-50 fw-bold small px-3 mb-2 mt-4" style="font-size: 0.7rem; letter-spacing: 1px;">Thực Đơn & Món</div>
        <a href="${pageContext.request.contextPath}/danh-muc" class="menu-item" data-path="/danh-muc"><i class="bi bi-collection-fill"></i> <span>Quản Lý Danh Mục</span></a>
        <a href="${pageContext.request.contextPath}/san-pham" class="menu-item" data-path="/san-pham"><i class="bi bi-cup-hot-fill"></i> <span>Quản Lý Sản Phẩm</span></a>
        <a href="${pageContext.request.contextPath}/bien-the" class="menu-item" data-path="/bien-the"><i class="bi bi-tags-fill"></i> <span>Biến Thể & Kích Cỡ</span></a>
        <a href="${pageContext.request.contextPath}/topping" class="menu-item" data-path="/topping"><i class="bi bi-plus-square-fill"></i> <span>Quản Lý Topping</span></a>

        <div class="text-uppercase text-white-50 fw-bold small px-3 mb-2 mt-4" style="font-size: 0.7rem; letter-spacing: 1px;">Marketing & CRM</div>
        <a href="${pageContext.request.contextPath}/khach-hang" class="menu-item" data-path="/khach-hang"><i class="bi bi-person-vcard-fill"></i> <span>Thẻ Khách Hàng</span></a>
        <a href="${pageContext.request.contextPath}/khuyen-mai" class="menu-item" data-path="/khuyen-mai"><i class="bi bi-ticket-perforated-fill"></i> <span>Mã Khuyến Mãi</span></a>
        <a href="${pageContext.request.contextPath}/phuong-thuc" class="menu-item" data-path="/phuong-thuc"><i class="bi bi-wallet-fill"></i> <span>Cổng Thanh Toán</span></a>

        <div class="text-uppercase text-white-50 fw-bold small px-3 mb-2 mt-4" style="font-size: 0.7rem; letter-spacing: 1px;">Hệ Thống</div>
        <a href="${pageContext.request.contextPath}/nhan-vien" class="menu-item" data-path="/nhan-vien"><i class="bi bi-shield-lock-fill"></i> <span>Tài Khoản Nhân Viên</span></a>
        <!-- BỔ SUNG PHÂN QUYỀN VÀ NHẬT KÝ KIỂM TOÁN -->
        <a href="${pageContext.request.contextPath}/vai-tro" class="menu-item" data-path="/vai-tro"><i class="bi bi-person-badge"></i> <span>Phân Quyền Vai Trò</span></a>
        <a href="${pageContext.request.contextPath}/nhat-ky" class="menu-item" data-path="/nhat-ky"><i class="bi bi-clock-history"></i> <span>Nhật Ký (Audit Trail)</span></a>
    </div>
</nav>