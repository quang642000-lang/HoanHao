<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Nhân Viên - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css?v=5">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/responsive/2.5.0/css/responsive.bootstrap5.min.css">
</head>
<body>
<%@ include file="../layout/sidebar.jsp" %>
<div class="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
    <div class="main-content">
        <!-- HEADER CHUẨN ĐỒNG BỘ -->
        <header class="top-navbar bg-white shadow-sm px-4 py-3 d-flex justify-content-between align-items-center mb-4">
            <div class="d-flex align-items-center">
                <button class="btn btn-light me-3 border-0 shadow-sm d-lg-none" onclick="toggleSidebar()"><i class="bi bi-list fs-5"></i></button>
                <h4 class="text-dark fw-bold m-0"><i class="bi bi-person-badge-fill text-brand me-2"></i>Quản Lý Nhân Viên</h4>
            </div>
            <div class="d-flex align-items-center"><span class="fw-bold text-dark d-none d-md-block me-3">${sessionScope.nhanVienDangNhap.hoTen}</span></div>
        </header>

        <div class="container-fluid px-4 mb-5">
            <div class="col-12 mb-4">
                <!-- FORM TÌM KIẾM CHUẨN ĐỒNG BỘ -->
                <div class="card mb-3 border-0 shadow-sm" style="border-radius: 16px;">
                    <div class="card-body p-3">
                        <form action="${pageContext.request.contextPath}/nhan-vien" method="get">
                            <input type="hidden" name="action" value="search">
                            <div class="row g-3 align-items-center">
                                <div class="col-md-8">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                                        <input type="text" class="form-control border-start-0 ps-0" name="keyword" placeholder="Tìm theo tên, SĐT hoặc email..." value="${param.keyword}">
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <button type="submit" class="btn btn-dark w-100 fw-bold rounded-pill">Tìm Kiếm</button>
                                </div>
                                <div class="col-md-2 text-end">
                                    <c:if test="${not empty param.keyword}">
                                        <a href="${pageContext.request.contextPath}/nhan-vien?action=list" class="btn btn-outline-danger fw-bold w-100 rounded-pill"><i class="bi bi-x-circle"></i> Xóa Lọc</a>
                                    </c:if>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- BẢNG DỮ LIỆU CHUẨN ĐỒNG BỘ -->
                <div class="card shadow-sm border-0" style="border-radius: 16px;">
                    <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center py-3">
                        <h5 class="mb-0 fw-bold text-dark"><i class="bi bi-person-lines-fill text-brand me-2"></i>Danh Sách Nhân Sự</h5>
                        <div>
                            <button type="button" class="btn btn-brand fw-bold shadow-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#addModal">
                                <i class="bi bi-plus-circle me-1"></i> Cấp Tài Khoản
                            </button>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover table-custom mb-0 text-center align-middle dt-responsive nowrap" style="width:100%" id="nhanVienTable">
                                <thead>
                                <tr>
                                    <th class="text-start ps-4">Họ Tên & Liên Hệ</th>
                                    <th>Tài Khoản</th>
                                    <th>Vai Trò</th>
                                    <th>Trạng Thái</th>
                                    <th>Thao Tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${not empty requestScope.danhSach}">
                                        <c:forEach var="nv" items="${requestScope.danhSach}">
                                            <tr>
                                                <td class="text-start ps-4">
                                                    <div class="d-flex align-items-center">
                                                        <div class="me-3 rounded-circle d-flex justify-content-center align-items-center text-white fw-bold shadow-sm" style="width: 42px; height: 42px; background: ${nv.vaiTro.maVaiTro == 1 ? '#DC2626' : '#2563EB'};">
                                                                ${fn:substring(nv.hoTen, 0, 1)}
                                                        </div>
                                                        <div>
                                                            <div class="fw-bold text-dark fs-6">${nv.hoTen} <span class="badge bg-light text-muted border fw-normal ms-1 rounded-pill">${nv.maNV}</span></div>
                                                            <div class="small text-muted mt-1"><i class="bi bi-telephone"></i> ${nv.SDT} &nbsp;&bull;&nbsp; <i class="bi bi-envelope"></i> ${nv.email}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td class="fw-bold text-secondary">${nv.tenDangNhap}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${nv.vaiTro.maVaiTro == 1}">
                                                            <span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 rounded-pill px-3 py-2"><i class="bi bi-shield-star-fill"></i> ${nv.vaiTro.tenVaiTro}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25 rounded-pill px-3 py-2"><i class="bi bi-person-badge"></i> ${nv.vaiTro.tenVaiTro}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${nv.trangThai == 1}"><span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 rounded-pill px-3 py-2">Hoạt Động</span></c:when>
                                                        <c:otherwise><span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25 rounded-pill px-3 py-2">Bị Khóa</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="btn-group btn-group-sm shadow-sm">
                                                        <c:choose>
                                                            <c:when test="${nv.trangThai == 1}">
                                                                <a href="${pageContext.request.contextPath}/nhan-vien?action=toggle-status&id=${nv.maNV}&status=0" class="btn btn-light text-warning border" title="Khóa" onclick="event.preventDefault(); showConfirmLink('Khóa Tài Khoản', 'Bạn có chắc chắn muốn KHÓA nhân viên này?', this.href);"><i class="bi bi-pause-circle"></i></a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${pageContext.request.contextPath}/nhan-vien?action=toggle-status&id=${nv.maNV}&status=1" class="btn btn-light text-success border" title="Mở khóa" onclick="event.preventDefault(); showConfirmLink('Mở Khóa', 'Bạn có muốn mở lại quyền truy cập cho nhân viên này?', this.href);"><i class="bi bi-play-circle"></i></a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <a href="${pageContext.request.contextPath}/nhan-vien?action=delete&id=${nv.maNV}" class="btn btn-light text-danger border" title="Xóa" onclick="event.preventDefault(); showConfirmLink('Xóa Nhân Viên', 'Bạn có chắc muốn xóa tài khoản [${fn:escapeXml(nv.hoTen)}]? Thao tác này không thể hoàn tác.', this.href);">
                                                            <i class="bi bi-trash"></i>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="5" class="text-center text-muted py-5"><i class="bi bi-inbox fs-1 d-block mb-3 opacity-50"></i>Chưa có Nhân Viên nào.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                                </tbody>
                            </table>
                            <c:if test="${totalPages > 1}">
                                <jsp:include page="../layout/pagination.jsp"><jsp:param name="baseUrl" value="/nhan-vien?action=list" /></jsp:include>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../layout/confirm_modal.jsp" %>

<!-- MODAL THÊM MỚI NHÂN VIÊN CHUẨN ĐỒNG BỘ -->
<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header border-0 py-3 bg-light">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-person-plus-fill text-brand me-2"></i>Cấp Tài Khoản Mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/nhan-vien" method="post" onsubmit="showConfirmForm(event, this, 'Xác Nhận Tạo', 'Xác nhận tạo tài khoản mới cho nhân viên này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="add">
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Họ và Tên</label>
                            <input type="text" class="form-control fw-bold" name="hoTen" placeholder="VD: Nguyễn Văn A" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Số Điện Thoại</label>
                            <input type="tel" class="form-control fw-bold" name="SDT" placeholder="Gồm 10 số..." pattern="\d{10,11}" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small text-muted text-uppercase">Địa Chỉ Email</label>
                        <input type="email" class="form-control fw-bold" name="email" placeholder="VD: mail@domain.com" required>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Tên Đăng Nhập</label>
                            <input type="text" class="form-control fw-bold text-primary" name="tenDangNhap" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Mật Khẩu</label>
                            <input type="password" class="form-control border-danger" name="matKhau" minlength="6" required>
                        </div>
                    </div>
                    <div class="mb-2">
                        <label class="form-label fw-semibold small text-muted text-uppercase">Quyền Hạn</label>
                        <select class="form-select fw-bold bg-light" name="maVaiTro" required>
                            <option value="1">Admin (Quản trị viên)</option>
                            <option value="2" selected>Nhân Viên (Bán Hàng)</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 p-3 d-flex justify-content-end">
                    <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border me-2" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4 shadow-sm"><i class="bi bi-person-check-fill me-1"></i> Khởi Tạo</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/global.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdn.datatables.net/responsive/2.5.0/js/dataTables.responsive.min.js"></script>
<script src="https://cdn.datatables.net/responsive/2.5.0/js/responsive.bootstrap5.min.js"></script>
<script>
    $(document).ready(function() {
        if ($('#nhanVienTable tbody td').length > 1) {
            $('#nhanVienTable').DataTable({
                "responsive": true, "paging": false, "searching": false, "info": false, "order": [],
                "columnDefs": [{ "orderable": false, "targets": [12] }],
                "language": { "emptyTable": "Chưa có Nhân Viên nào." }
            });
        }
    });
</script>
</body>
</html>