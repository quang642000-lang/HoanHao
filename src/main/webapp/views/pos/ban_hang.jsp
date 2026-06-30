<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>TEA POS - Hệ Thống Bán Hàng</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pos.css">
</head>
<body>
<input type="hidden" id="appContextPath" value="${pageContext.request.contextPath}">
<%@ include file="../layout/toast.jsp" %>
<nav class="navbar navbar-expand-lg navbar-dark shadow-sm sticky-top">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold fs-4 d-flex align-items-center" href="${pageContext.request.contextPath}/ban-hang" style="color: var(--brand-primary);">
            <i class="bi bi-cup-straw me-2 fs-3"></i> TEA POS
        </a>
        <div class="d-flex align-items-center text-white">
            <span class="me-3 fw-medium d-none d-md-inline"><i class="bi bi-person-circle me-1"></i> ${sessionScope.nhanVienDangNhap.hoTen}</span>
            <button class="btn btn-sm btn-outline-light fw-bold me-3 rounded-pill px-3 shadow-sm" data-bs-toggle="modal" data-bs-target="#profileModal">
                <i class="bi bi-gear-fill"></i> Cài đặt
            </button>
            <c:if test="${sessionScope.nhanVienDangNhap.vaiTro.maVaiTro == 1}">
                <a href="${pageContext.request.contextPath}/admin" class="btn btn-sm btn-light text-dark fw-bold me-2 rounded-pill px-3 shadow-sm"><i class="bi bi-grid-fill"></i> Admin</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/auth?action=logout" class="btn btn-sm btn-danger fw-bold rounded-pill px-3 shadow-sm"><i class="bi bi-power"></i> Thoát</a>
        </div>
    </div>
</nav>
<div class="container-fluid px-3 px-xl-4 mt-3">
    <!-- Đã thêm class pos-main-row để khống chế chiều cao Layout -->
    <div class="row g-4 pos-main-row">
        <!-- ================= BÊN TRÁI: DANH SÁCH MÓN ================= -->
        <!-- Đã thêm class product-area -->
        <div class="col-lg-7 col-xl-8 product-area">
            <!-- Vùng Danh mục: Luôn đứng yên ở trên cùng -->
            <div class="category-scroll-wrapper">
                <div class="d-flex category-scroll gap-2">
                    <a href="${pageContext.request.contextPath}/ban-hang" class="btn-filter text-decoration-none ${empty param.maDanhMuc ? 'active' : ''}">Tất cả</a>
                    <c:forEach var="dm" items="${requestScope.danhSachDanhMuc}">
                        <a href="${pageContext.request.contextPath}/ban-hang?maDanhMuc=${dm.maDanhMuc}" class="btn-filter text-decoration-none ${param.maDanhMuc == dm.maDanhMuc ? 'active' : ''}">${dm.tenDanhMuc}</a>
                    </c:forEach>
                </div>
            </div>
            <!-- Vùng Lưới Sản phẩm: Tự động có thanh cuộn dọc (không làm cuộn trang) -->
            <div class="product-grid-wrapper custom-scrollbar">
                <div class="row row-cols-2 row-cols-md-3 row-cols-xl-4 g-3">
                    <c:choose>
                        <c:when test="${not empty requestScope.danhSachSanPham}">
                            <c:forEach var="sp" items="${requestScope.danhSachSanPham}">
                                <c:if test="${sp.trangThai == 1}">
                                    <div class="col">
                                        <div class="card h-100 shadow-sm product-card" onclick="openOptionsModal('${sp.maSP}', '${fn:escapeXml(sp.tenSanPham)}')">
                                            <div class="product-img-wrapper">
                                                <img src="${pageContext.request.contextPath}/image/${not empty sp.hinhAnh ? sp.hinhAnh : 'default.png'}" class="product-img" onerror="this.src='https://placehold.co/300x200?text=No+Image'" alt="${fn:escapeXml(sp.tenSanPham)}">
                                            </div>
                                            <div class="card-body p-3 d-flex flex-column justify-content-between text-center">
                                                <h6 class="card-title fw-bold mb-2 text-dark" style="font-size: 0.9rem; line-height: 1.3;" title="${fn:escapeXml(sp.tenSanPham)}">${sp.tenSanPham}</h6>
                                                <div class="mt-auto">
                                                    <span class="badge bg-light text-primary border border-primary border-opacity-25 rounded-pill px-3 py-2 w-100 fw-bold"><i class="bi bi-plus-lg"></i> Chọn</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="col-12 text-center py-5">
                                <i class="bi bi-cup-straw text-muted opacity-50" style="font-size: 4rem;"></i>
                                <h5 class="text-muted mt-3 fw-semibold">Không có sản phẩm nào!</h5>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <!-- ================= BÊN PHẢI: GIỎ HÀNG THU NGÂN ================= -->
        <div class="col-lg-5 col-xl-4 offcanvas-lg offcanvas-end shadow cart-area" tabindex="-1" id="mobileCartOffcanvas" aria-labelledby="mobileCartLabel" style="background-color: var(--bg-surface);">
            <div class="offcanvas-header d-lg-none bg-white border-bottom shadow-sm z-3 flex-shrink-0">
                <h5 class="offcanvas-title fw-bold text-brand" id="mobileCartLabel"><i class="bi bi-cart3 me-2"></i>GIỎ HÀNG</h5>
                <button type="button" class="btn-close shadow-none" data-bs-dismiss="offcanvas" aria-label="Close"></button>
            </div>
            <!-- GHI CHÚ: Đã XÓA thẻ style="height: 100%;" tại đây -->
            <div class="offcanvas-body p-0 p-lg-2 overflow-hidden d-flex flex-column">
                <div class="card shadow-lg border-0 cart-wrapper w-100 rounded-0 rounded-lg-4">
                    <div class="card-header bg-white border-bottom py-3 d-flex justify-content-between align-items-center flex-shrink-0">
                        <h5 class="mb-0 fw-bold text-dark d-none d-lg-block"><i class="bi bi-cart3 me-2" style="color: var(--brand-primary);"></i> ĐƠN HÀNG</h5>
                        <h5 class="mb-0 fw-bold text-dark d-lg-none">Số lượng món</h5>
                        <button class="btn btn-sm btn-outline-danger rounded-pill fw-bold" onclick="clearCart()"><i class="bi bi-trash"></i> Xóa Hết</button>
                    </div>
                    <!-- VÙNG NÀY SẼ TỰ ĐỘNG CUỘN KHI NHIỀU MÓN NHỜ CSS MIN-HEIGHT: 0 -->
                    <div class="card-body p-0 cart-items custom-scrollbar" id="cart-items-container">
                        <!-- Render JS Cart Items -->
                    </div>
                    <div class="card-footer bg-white border-top shadow-sm p-3 flex-shrink-0">
                        <form action="${pageContext.request.contextPath}/ban-hang" method="post" id="checkout-form" onsubmit="return validateCheckout(event)">
                            <input type="hidden" name="action" value="checkout">
                            <div id="hidden-cart-inputs"></div>
                            <input type="hidden" name="tongTienHang" id="input_tongTienHang" value="0">
                            <input type="hidden" name="tienGiamGia" id="input_tienGiamGia" value="0">
                            <input type="hidden" name="tongPhaiTra" id="input_tongPhaiTra" value="0">
                            <input type="hidden" name="maKM" id="input_maKM" value="">
                            <!-- TT Khách Hàng -->
                            <div class="row g-2 mb-2">
                                <div class="col-5">
                                    <!-- Sửa lỗi nghiệp vụ 2: Đổi maxlength thành 11 và pattern cho SĐT -->
                                    <input type="tel" class="form-control form-control-sm text-center fw-bold" name="sdtKhachHang" id="sdtKhachHang" placeholder="SĐT Khách" maxlength="11" pattern="\d{10,11}" oninput="checkCustomerPhone()">
                                </div>
                                <div class="col-7">
                                    <input type="text" class="form-control form-control-sm fw-medium" name="tenKhachHang" id="tenKhachHang" placeholder="Tên khách (nếu mới)">
                                </div>
                            </div>
                            <div id="customerInfoPanel" class="bg-light p-2 rounded-3 border mb-2" style="display: none; border-color: #E2E8F0 !important;">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="fw-bold text-dark small"><i class="bi bi-person-check-fill text-success"></i> <span id="lblTenKH"></span></span>
                                    <span class="badge bg-warning text-dark px-2 rounded-pill"><i class="bi bi-coin"></i> <span id="lblDiem">0</span></span>
                                </div>
                                <div class="form-check form-switch small mb-1">
                                    <input class="form-check-input" type="checkbox" id="toggleDiem" onchange="applyPoints()">
                                    <label class="form-check-label text-danger fw-bold" for="toggleDiem">Dùng điểm (1đ = 1.000đ)</label>
                                </div>
                                <div id="nhapDiemContainer" class="input-group input-group-sm mt-1" style="display: none;">
                                    <span class="input-group-text bg-white fw-medium">Dùng:</span>
                                    <input type="number" class="form-control text-end text-danger fw-bold" id="input_nhapDiemTay" value="0" min="0" oninput="calculateCustomPoints()">
                                    <button class="btn btn-outline-danger fw-bold" type="button" onclick="useMaxPoints()">Max</button>
                                </div>
                                <input type="hidden" name="diemSuDung" id="input_diemSuDung" value="0">
                            </div>
                            <div id="newCustomerPanel" class="mb-2 p-2 bg-white rounded-3 border border-primary border-opacity-50 shadow-sm" style="display: none;">
                                <div class="text-primary small fw-bold mb-1"><i class="bi bi-stars"></i> Mở thẻ hội viên mới:</div>
                                <input type="email" class="form-control form-control-sm fw-medium border-primary" name="emailKhachHang" id="emailKhachHang" placeholder="Email (Để nhận mã xác thực Web O2O)">
                            </div>
                            <!-- Khuyến Mãi -->
                            <div class="mb-3 bg-light p-3 rounded-3 border" style="border-color: #E2E8F0 !important;">
                                <label class="fw-bold small text-muted mb-2 d-block"><i class="bi bi-ticket-perforated"></i> Mã Giảm Giá</label>
                                <div class="input-group">
                                    <input type="text" class="form-control text-uppercase fw-bold border-dark" id="inputVoucherCode" placeholder="Nhập mã KM...">
                                    <button class="btn btn-dark fw-bold px-3" type="button" onclick="checkAndApplyVoucher()">Áp Dụng</button>
                                </div>
                                <div id="activeVoucherInfo" class="mt-2 pt-2 border-top" style="display: none;">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="text-success fw-bold"><i class="bi bi-check-circle-fill me-1"></i> Đã áp mã: <span id="voucherLabel" class="text-decoration-underline"></span></span>
                                        <button type="button" class="btn btn-sm btn-outline-danger py-0 px-2" onclick="removeVoucher()"><i class="bi bi-x"></i> Gỡ</button>
                                    </div>
                                </div>
                            </div>
                            <!-- Tổng kết -->
                            <div class="border rounded-3 p-3 mb-3 bg-light" style="border-color: #E2E8F0 !important;">
                                <div class="d-flex justify-content-between mb-1 small">
                                    <span class="text-muted fw-semibold">Tổng tiền hàng:</span>
                                    <span class="fw-bold text-dark" id="display_tongTienHang">0 ₫</span>
                                </div>
                                <div class="d-flex justify-content-between mb-1 small text-success">
                                    <span class="fw-semibold">Giảm Voucher:</span>
                                    <span class="fw-bold" id="display_tienGiamGia">- 0 ₫</span>
                                </div>
                                <div class="d-flex justify-content-between mb-2 small text-danger" id="row_giamDiem" style="display: none !important;">
                                    <span class="fw-semibold">Trừ điểm tích lũy:</span>
                                    <span class="fw-bold" id="display_giamDiem">- 0 ₫</span>
                                </div>
                                <hr class="my-2 border-secondary opacity-25">
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="fw-bold text-dark">TỔNG THANH TOÁN:</span>
                                    <span class="fw-bold text-danger" style="font-size: 1.4rem;" id="display_tongPhaiTra">0 ₫</span>
                                </div>
                            </div>
                            <!-- Thanh toán -->
                            <div class="row g-2 align-items-center mb-3">
                                <div class="col-12 col-sm-5">
                                    <select class="form-select fw-bold bg-light" name="maPTTT" id="select_pttt" required onchange="handlePaymentMethodChange()">
                                        <c:forEach var="pt" items="${requestScope.danhSachPTTT}">
                                            <option value="${pt.maPTTT}">${pt.tenPhuongThuc}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-12 col-sm-7">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white fw-bold border-end-0"><i class="bi bi-cash"></i></span>
                                        <!-- Sửa lỗi UX 3: Thêm onfocus để cuộn giao diện lên tránh bị bàn phím ảo che -->
                                        <input type="number" class="form-control text-end fw-bold text-primary border-start-0 ps-0" name="tienKhachDua" id="tienKhachDua" placeholder="Khách đưa" required oninput="calculateChange()" onfocus="this.scrollIntoView({behavior: 'smooth', block: 'center'});">
                                    </div>
                                </div>
                                <div class="col-12 text-end small mt-1" id="tienThuaContainer" style="display: none;">
                                    <span class="fw-semibold text-muted">Tiền thối lại:</span>
                                    <span class="fw-bold text-success fs-6 ms-2" id="tienThuaLabel">0 ₫</span>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-brand w-100 py-3 fs-5 shadow-sm" id="btn-checkout" disabled>
                                <i class="bi bi-check-circle me-2"></i> THANH TOÁN ĐƠN
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<button class="btn btn-brand floating-cart-btn d-lg-none" type="button" data-bs-toggle="offcanvas" data-bs-target="#mobileCartOffcanvas">
    <i class="bi bi-cart3 fs-3"></i>
    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger border border-light" id="mobileCartBadge" style="font-size: 0.8rem;">0</span>
</button>
<%@ include file="../layout/confirm_modal.jsp" %>
<div class="modal fade" id="profileModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0">
            <div class="modal-header text-white py-3 border-0" style="background-color: var(--brand-dark);">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-lines-fill text-brand me-2"></i>Thiết Lập Tài Khoản</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/ban-hang" method="post" id="form-profile" onsubmit="validateProfileAndConfirm(event, this)">
                <div class="modal-body p-4 bg-light">
                    <input type="hidden" name="action" value="update-profile">
                    <div class="alert alert-info small shadow-sm border-0 mb-4">
                        <i class="bi bi-shield-lock-fill me-1"></i> Để bảo mật, mọi thay đổi đều yêu cầu <strong>Mật khẩu hiện tại</strong>.
                    </div>
                    <h6 class="fw-bold text-dark mb-3 text-uppercase small" style="letter-spacing: 1px;">Thông tin cơ bản</h6>
                    <div class="row g-3 mb-4">
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-muted">Họ và Tên</label>
                            <input type="text" class="form-control fw-medium" name="hoTen" value="${sessionScope.nhanVienDangNhap.hoTen}" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-muted">Số Điện Thoại</label>
                            <input type="tel" class="form-control fw-medium" name="sdt" value="${sessionScope.nhanVienDangNhap.SDT}" pattern="\d{10,11}" required>
                        </div>
                        <div class="col-md-12 mt-3">
                            <label class="form-label fw-bold small text-muted">Địa Chỉ Email</label>
                            <input type="email" class="form-control fw-medium" name="email" value="${sessionScope.nhanVienDangNhap.email}" required>
                        </div>
                    </div>
                    <h6 class="fw-bold text-dark mb-3 text-uppercase small" style="letter-spacing: 1px;">Bảo mật & Đổi mật khẩu</h6>
                    <div class="mb-3">
                        <label class="form-label fw-bold small text-danger">Mật Khẩu Hiện Tại (Bắt buộc) *</label>
                        <input type="password" class="form-control border-danger" name="oldPass" required placeholder="Nhập mật khẩu hiện tại...">
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-muted">Mật Khẩu Mới</label>
                            <input type="password" class="form-control" name="newPass" id="newPass" minlength="6" placeholder="Bỏ trống nếu không đổi">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-muted">Xác Nhận Mật Khẩu</label>
                            <input type="password" class="form-control" id="confirmPass" placeholder="Nhập lại mật khẩu mới">
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0 p-3 bg-white d-flex justify-content-between">
                    <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4"><i class="bi bi-floppy-fill me-1"></i> Lưu Thiết Lập</button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="qrModal" tabindex="-1" data-bs-backdrop="static" data-bs-keyboard="false">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content shadow-lg border-0">
            <div class="modal-header text-white border-0 py-3" style="background-color: var(--brand-primary);">
                <h6 class="modal-title fw-bold m-0"><i class="bi bi-qr-code-scan me-2"></i> Quét Mã Thanh Toán</h6>
            </div>
            <div class="modal-body text-center bg-white p-4">
                <h3 class="text-danger fw-bold mb-1" id="qrAmount">0 ₫</h3>
                <p class="text-muted small fw-medium mb-3">Mã GD: <span class="fw-bold text-dark" id="qrCodeDisplay"></span></p>
                <div class="bg-light p-3 rounded-4 d-inline-block mb-3 position-relative border">
                    <img id="qrImage" src="" alt="QR" style="width: 200px; height: 200px; object-fit: contain;">
                    <div id="qrSuccessOverlay" class="position-absolute top-0 start-0 w-100 h-100 bg-white bg-opacity-75 d-flex flex-column justify-content-center align-items-center rounded-4" style="display: none !important; z-index: 10; backdrop-filter: blur(2px);">
                        <i class='bi bi-check-circle-fill text-success' style='font-size: 4rem;'></i>
                        <h5 class='text-success mt-2 fw-bold'>Đã Nhận Tiền!</h5>
                    </div>
                    <!-- BỔ SUNG OVERLAY HẾT HẠN CHE MÃ QR KHI VỀ 0 -->
                    <div id="qrExpiredOverlay" class="position-absolute top-0 start-0 w-100 h-100 bg-white bg-opacity-85 d-flex flex-column justify-content-center align-items-center rounded-4" style="display: none !important; z-index: 10; backdrop-filter: blur(4px);">
                        <i class='bi bi-x-circle-fill text-danger' style='font-size: 4rem;'></i>
                        <h5 class='text-danger mt-2 fw-bold'>Mã Đã Hết Hạn</h5>
                    </div>
                    <!-- ----------------------------------------- -->
                </div>
                <!-- BỔ SUNG DÒNG CẢNH BÁO ĐẾM NGƯỢC THỜI GIAN -->
                <div class="text-danger fw-bold small mb-3 text-uppercase animate__animated animate__pulse animate__infinite">
                    <i class="bi bi-clock-history"></i> Mã QR chỉ có hiệu lực thanh toán trong <span id="qrCountdownText" class="fs-5">60</span> giây
                </div>
                <div id="qrLoadingStatus" class="text-primary fw-bold small mb-2 d-flex align-items-center justify-content-center">
                    <div class="spinner-border spinner-border-sm me-2" role="status"></div>
                    <span>Hệ thống đang chờ tiền vào...</span>
                </div>
            </div>
            <div class="modal-footer border-0 p-3 bg-light d-flex justify-content-between rounded-bottom-4">
                <button type="button" class="btn btn-outline-danger fw-bold rounded-pill px-3" onclick="cancelQRPayment()">Hủy</button>
                <button type="button" class="btn btn-success fw-bold rounded-pill px-3" onclick="forceSubmitCheckout()">
                    Bỏ Qua <i class="bi bi-arrow-right"></i>
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="optionModal" tabindex="-1">
    <!-- Sửa lỗi UX 2: Thêm modal-dialog-scrollable để tránh bị tràn và che nút trên mobile -->
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content shadow-lg">
            <div class="modal-header border-0 py-3 bg-light">
                <h5 class="modal-title fw-bold text-dark" id="modalProductName">Tên Sản Phẩm</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div class="mb-4">
                    <label class="fw-bold text-muted small mb-2 text-uppercase" style="letter-spacing: 0.5px;">1. Chọn Kích Cỡ (Size)</label>
                    <div id="sizeContainer" class="d-flex flex-wrap gap-2"></div>
                </div>
                <div class="row mb-4">
                    <div class="col-6">
                        <label class="fw-bold text-muted small mb-2 text-uppercase" style="letter-spacing: 0.5px;">2. Mức Đá</label>
                        <select class="form-select bg-light fw-medium" id="modalDa">
                            <option value="100%">100% Đá (Bình thường)</option>
                            <option value="50%">50% Đá (Ít đá)</option>
                            <option value="0%">0% Đá (Không đá)</option>
                        </select>
                    </div>
                    <div class="col-6">
                        <label class="fw-bold text-muted small mb-2 text-uppercase" style="letter-spacing: 0.5px;">3. Mức Đường</label>
                        <select class="form-select bg-light fw-medium" id="modalDuong">
                            <option value="100%">100% Đường (Bình thường)</option>
                            <option value="50%">50% Đường (Ít ngọt)</option>
                            <option value="0%">0% Đường (Không đường)</option>
                        </select>
                    </div>
                </div>
                <label class="fw-bold text-muted small mb-2 text-uppercase" style="letter-spacing: 0.5px;">4. Chọn Thêm Topping</label>
                <div class="topping-list custom-scrollbar bg-light rounded-3 p-2 border">
                    <c:forEach var="tp" items="${requestScope.danhSachTopping}">
                        <c:if test="${tp.trangThai == 1}">
                            <div class="d-flex justify-content-between align-items-center p-2 mb-1 bg-white rounded shadow-sm">
                                <div class="d-flex align-items-center">
                                    <img src="${pageContext.request.contextPath}/image/${not empty tp.hinhAnh ? tp.hinhAnh : 'default.png'}"
                                         class="rounded me-3 border" style="width: 44px; height: 44px; object-fit: cover;"
                                         onerror="this.src='https://placehold.co/100x100?text=No+Image'">
                                    <div>
                                        <div class="fw-bold text-dark" style="font-size: 0.95rem;">${tp.tenTopping}</div>
                                        <div class="text-danger fw-semibold small">+<fmt:formatNumber value="${tp.giaBan}" type="number"/>đ</div>
                                    </div>
                                </div>
                                <div class="input-group input-group-sm" style="width: 100px;">
                                    <button class="btn btn-outline-secondary fw-bold" type="button" onclick="changeModalTpQty('${tp.maTopping}', -1)">-</button>
                                    <input type="text" class="form-control text-center fw-bold bg-white"
                                           id="tp_qty_${tp.maTopping}" value="0" readonly
                                           data-id="${tp.maTopping}" data-name="${fn:escapeXml(tp.tenTopping)}" data-price="${tp.giaBan}">
                                    <button class="btn btn-outline-secondary fw-bold text-dark" type="button" onclick="changeModalTpQty('${tp.maTopping}', 1)">+</button>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
            <div class="modal-footer border-0 p-3 bg-light">
                <button type="button" id="btn-confirm-modal" class="btn btn-brand w-100 py-3 fs-6 rounded-pill" onclick="confirmAddToCart()">
                    <i class="bi bi-cart-plus me-2"></i> XÁC NHẬN THÊM MÓN
                </button>
            </div>
        </div>
    </div>
</div>
<c:if test="${not empty sessionScope.recentOrder}">
    <div class="modal fade" id="receiptModal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
        <div class="modal-dialog modal-dialog-centered modal-sm">
            <div class="modal-content border-0 bg-transparent shadow-none">
                <div class="modal-body thermal-receipt" id="printable-receipt-content">
                    <div style="text-align:center; margin-bottom:15px;">
                        <h3 style="font-weight:bold; margin:0; letter-spacing: 1px;">TEA POS</h3>
                        <div style="font-size:12px; margin-top: 5px;">Hệ Thống Bán Hàng Premium</div>
                        <div style="font-size:12px;">Hotline: 0988.888.888</div>
                        <hr>
                        <h5 style="font-weight:bold; margin-top:10px;">HÓA ĐƠN THANH TOÁN</h5>
                    </div>
                    <div style="font-size:12px; margin-bottom:15px; line-height: 1.6;">
                        <div><span style="font-weight:bold;">Mã HD:</span> ${sessionScope.recentOrder.maDH}</div>
                        <div><span style="font-weight:bold;">Ngày:</span> <fmt:formatDate value="${sessionScope.recentOrder.thoiGianTao}" pattern="dd/MM/yyyy HH:mm"/></div>
                        <div><span style="font-weight:bold;">Thu ngân:</span> ${sessionScope.nhanVienDangNhap.hoTen}</div>
                        <c:choose>
                            <c:when test="${not empty sessionScope.recentOrder.khachHang}">
                                <div><span style="font-weight:bold;">Khách:</span> ${sessionScope.recentOrder.khachHang.tenKH}</div>
                            </c:when>
                            <c:otherwise>
                                <div><span style="font-weight:bold;">Khách:</span> Khách vãng lai</div>
                            </c:otherwise>
                        </c:choose>
                        <div><span style="font-weight:bold;">TT:</span> ${sessionScope.recentOrder.phuongThucThanhToan.tenPhuongThuc}</div>
                    </div>
                    <hr>
                    <div style="font-size:12px;">
                        <table style="width:100%; border-collapse:collapse;">
                            <c:forEach var="ct" items="${sessionScope.recentOrder.danhSachChiTiet}">
                                <!-- 1. Tên món và Giá gốc -->
                                <tr>
                                    <td style="font-weight:bold; padding-top: 8px; vertical-align: top;">${ct.soLuong} x ${ct.bienThe.sanPham.tenSanPham}</td>
                                    <td style="text-align:right; padding-top: 8px; vertical-align: top;">
                                        <fmt:formatNumber value="${ct.giaChot * ct.soLuong}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                    </td>
                                </tr>
                                <!-- 2. Tùy chọn (Format lại gạch đầu dòng rõ ràng, bỏ dấu ngoặc, check ẩn Size rỗng) -->
                                <tr>
                                    <td colspan="2" style="padding-left:12px; color:#555; font-size:11px; padding-bottom: 2px;">
                                        - <c:if test="${not empty fn:trim(ct.bienThe.kichCo)}">Size ${ct.bienThe.kichCo}, </c:if>${ct.mucDa} Đá, ${ct.mucDuong} Đường
                                    </td>
                                </tr>
                                <c:set var="tongTien1Mon" value="${ct.giaChot * ct.soLuong}" />
                                <!-- 3. Topping -->
                                <c:forEach var="tpItem" items="${ct.danhSachTopping}">
                                    <tr>
                                        <td style="padding-left:12px; color:#555; font-size:11px;">+ ${tpItem.soLuongTopping} x ${tpItem.topping.tenTopping}</td>
                                        <td style="text-align:right; color:#555; font-size:11px;">
                                            <fmt:formatNumber value="${tpItem.giaChot * tpItem.soLuongTopping}" type="currency" currencySymbol="đ" maxFractionDigits="0"/>
                                        </td>
                                    </tr>
                                    <c:set var="tongTien1Mon" value="${tongTien1Mon + (tpItem.giaChot * tpItem.soLuongTopping)}" />
                                </c:forEach>
                                <!-- 4. Tổng thành tiền của nhóm món này -->
                                <tr>
                                    <td colspan="2" style="text-align:right; font-size: 11px; padding-top: 4px; padding-bottom: 8px; border-bottom: 1px dashed #ccc;">
                                        <span style="color:#555; margin-right: 5px;">Thành tiền:</span>
                                        <span style="font-size: 12px; font-weight: bold;"><fmt:formatNumber value="${tongTien1Mon}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                    <hr>
                    <div style="font-size:12px; line-height: 1.6;">
                        <div style="display:flex; justify-content:space-between;">
                            <span>Tổng cộng:</span>
                            <span style="font-weight:bold;"><fmt:formatNumber value="${sessionScope.recentOrder.tongTienHang}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                        </div>
                        <c:if test="${sessionScope.recentOrder.tienGiamGia > 0}">
                            <div style="display:flex; justify-content:space-between;">
                                <span>Giảm Voucher:</span>
                                <c:set var="giamVoucherBill" value="${sessionScope.recentOrder.tienGiamGia - (not empty sessionScope.diemSuDungBill ? sessionScope.diemSuDungBill * 1000 : 0)}"/>
                                <span>-<fmt:formatNumber value="${giamVoucherBill > 0 ? giamVoucherBill : 0}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                            </div>
                        </c:if>
                        <c:if test="${not empty sessionScope.diemSuDungBill && sessionScope.diemSuDungBill > 0}">
                            <div style="display:flex; justify-content:space-between;">
                                <span>Trừ điểm (${sessionScope.diemSuDungBill}):</span>
                                <span>-<fmt:formatNumber value="${sessionScope.diemSuDungBill * 1000}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                            </div>
                        </c:if>
                        <hr>
                        <div style="display:flex; justify-content:space-between; font-weight:bold; font-size:16px; margin-top:5px; margin-bottom:5px;">
                            <span>THANH TOÁN:</span>
                            <span><fmt:formatNumber value="${sessionScope.recentOrder.tongTienTra}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                        </div>
                        <div style="display:flex; justify-content:space-between;">
                            <span>Tiền nhận:</span>
                            <span><fmt:formatNumber value="${sessionScope.recentOrder.soTienKhachDua}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                        </div>
                        <div style="display:flex; justify-content:space-between;">
                            <span>Tiền thối:</span>
                            <span><fmt:formatNumber value="${sessionScope.recentOrder.soTienKhachDua - sessionScope.recentOrder.tongTienTra}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></span>
                        </div>
                    </div>
                    <hr>
                    <div style="text-align:center; font-size:12px; margin-top:15px;">
                        <p style="font-weight:bold; margin:0;">CẢM ƠN QUÝ KHÁCH!</p>
                        <p style="margin:5px 0 0 0; font-size: 10px;">Powered by TEA POS</p>
                    </div>
                </div>
                <div class="mt-4 text-center pb-3">
                    <button type="button" class="btn btn-light fw-bold px-4 rounded-pill me-2 shadow-sm" data-bs-dismiss="modal">Đóng</button>
                    <button type="button" class="btn btn-primary fw-bold px-4 rounded-pill shadow-sm" onclick="printReceipt()"><i class="bi bi-printer me-2"></i> In Lại</button>
                </div>
            </div>
        </div>
    </div>
</c:if>
<div id="hidden-variants-data" style="display: none;">
    <c:forEach var="bt" items="${requestScope.danhSachBienThe}">
        <div class="variant-item-data"
             data-mabt="${bt.maBienThe}"
             data-masp="${bt.sanPham.maSP}"
             data-size="${fn:escapeXml(bt.kichCo)}"
             data-price="${bt.giaBan}"></div>
    </c:forEach>
</div>
<div id="hidden-vouchers-data" style="display: none;">
    <c:forEach var="km" items="${requestScope.danhSachKhuyenMai}">
        <c:if test="${km.trangThai == 1 && (km.soLuong - km.soLuongDaDung > 0)}">
            <div class="voucher-item-data"
                 data-id="${km.maKM}"
                 data-code="${km.maCode}"
                 data-loai="${km.loaiGiamGia}"
                 data-giatri="${km.giaTriGiam}"
                 data-min="${km.dieuKienToiThieu}"
                 data-start="${km.ngayBatDau.time}"
                 data-end="${km.ngayKetThuc.time}"></div>
        </c:if>
    </c:forEach>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/global.js"></script>
<script>
    const appBasePath = document.getElementById('appContextPath').value;
    window.allVariants = [];
    document.querySelectorAll('.variant-item-data').forEach(function(item) {
        window.allVariants.push({
            maBT: item.getAttribute('data-mabt'),
            maSP: item.getAttribute('data-masp'),
            size: item.getAttribute('data-size'),
            price: parseInt(item.getAttribute('data-price'))
        });
    });
    window.availableVouchers = [];
    document.querySelectorAll('.voucher-item-data').forEach(function(item) {
        window.availableVouchers.push({
            id: item.getAttribute('data-id'),
            code: item.getAttribute('data-code').toUpperCase(),
            loai: item.getAttribute('data-loai'),
            giaTri: parseFloat(item.getAttribute('data-giatri')),
            min: parseFloat(item.getAttribute('data-min')),
            start: parseInt(item.getAttribute('data-start')),
            end: parseInt(item.getAttribute('data-end'))
        });
    });
    function validateProfileAndConfirm(event, formElement) {
        event.preventDefault();
        let newP = document.getElementById('newPass').value;
        let confP = document.getElementById('confirmPass').value;
        if(newP !== '' && newP !== confP) {
            showToast("Cảnh báo: Mật khẩu mới và Xác nhận không khớp nhau!", "danger");
            return false;
        }
        showConfirmAction('Lưu Thiết Lập', 'Xác nhận thay đổi thông tin cá nhân và bảo mật?', function() {
            formElement.submit();
        });
    }
</script>
<script src="${pageContext.request.contextPath}/assets/js/pos.js"></script>
</body>
</html>