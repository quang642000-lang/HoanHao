<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Khách Hàng CRM - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css?v=5">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/responsive/2.5.0/css/responsive.bootstrap5.min.css">
</head>
<body>
<div class="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
    <div class="main-content">
        <!-- HEADER CHUẨN ĐỒNG BỘ -->
        <header class="top-navbar bg-white shadow-sm px-4 py-3 d-flex justify-content-between align-items-center mb-4">
            <div class="d-flex align-items-center">
                <button class="btn btn-light me-3 border-0 shadow-sm d-lg-none" onclick="toggleSidebar()"><i class="bi bi-list fs-5"></i></button>
                <h4 class="text-dark fw-bold m-0"><i class="bi bi-people-fill text-brand me-2"></i>Quản Lý Khách Hàng</h4>
            </div>
            <div class="d-flex align-items-center"><span class="fw-bold text-dark d-none d-md-block me-3">${sessionScope.nhanVienDangNhap.hoTen}</span></div>
        </header>
        <div class="container-fluid px-4 mb-5">
            <div class="col-12 mb-4">
                <!-- FORM TÌM KIẾM CHUẨN ĐỒNG BỘ -->
                <div class="card mb-3 border-0 shadow-sm" style="border-radius: 16px;">
                    <div class="card-body p-3">
                        <form action="${pageContext.request.contextPath}/khach-hang" method="get">
                            <input type="hidden" name="action" value="search">
                            <div class="row g-3 align-items-center">
                                <div class="col-md-8">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                                        <input type="text" class="form-control border-start-0 ps-0" name="sdt" placeholder="Tìm số điện thoại khách hàng..." value="${param.sdt}" required pattern="\d{10,11}">
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <button type="submit" class="btn btn-dark w-100 fw-bold rounded-pill">Tìm Kiếm</button>
                                </div>
                                <div class="col-md-2 text-end">
                                    <c:if test="${not empty param.sdt}">
                                        <a href="${pageContext.request.contextPath}/khach-hang?action=list" class="btn btn-outline-danger fw-bold w-100 rounded-pill"><i class="bi bi-x-circle"></i> Xóa Lọc</a>
                                    </c:if>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <!-- BẢNG DỮ LIỆU CHUẨN ĐỒNG BỘ -->
                <div class="card shadow-sm border-0" style="border-radius: 16px;">
                    <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center py-3">
                        <h5 class="mb-0 fw-bold text-dark"><i class="bi bi-person-lines-fill text-brand me-2"></i>Danh Sách Thành Viên</h5>
                        <div>
                            <button type="button" class="btn btn-brand fw-bold shadow-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#addModal">
                                <i class="bi bi-plus-circle me-1"></i> Mở Thẻ Mới
                            </button>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover table-custom mb-0 text-center align-middle dt-responsive nowrap" style="width:100%" id="khachHangTable">
                                <thead>
                                <tr>
                                    <th width="5%">STT</th>
                                    <th width="15%">Mã KH</th>
                                    <th class="text-start">Họ Tên</th>
                                    <th width="20%">Liên Hệ</th>
                                    <th width="15%">Điểm Tích Lũy</th>
                                    <th width="15%">Thao Tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${not empty requestScope.danhSach}">
                                        <c:forEach var="kh" items="${requestScope.danhSach}" varStatus="status">
                                            <tr>
                                                <td class="fw-semibold text-muted">${(empty currentPage ? 0 : currentPage - 1) * 5 + status.index + 1}</td>
                                                <td class="fw-semibold text-muted">${kh.maKH}</td>
                                                <td class="text-start fw-bold text-dark">${kh.tenKH}</td>
                                                <td class="fw-medium">${kh.SDT}</td>
                                                <td><span class="badge points-badge fs-6 px-3 rounded-pill bg-warning text-dark shadow-sm border border-warning"><i class="bi bi-stars"></i> ${kh.diemTichLuy}</span></td>
                                                <td>
                                                    <div class="btn-group btn-group-sm shadow-sm">
                                                        <button class="btn btn-light text-primary border" data-bs-toggle="modal" data-bs-target="#editModal" onclick="fillEditModal('${kh.maKH}', '${fn:escapeXml(kh.tenKH)}', '${kh.SDT}')">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/khach-hang?action=delete&maKH=${kh.maKH}" class="btn btn-light text-danger border" onclick="event.preventDefault(); showConfirmLink('Xóa Khách Hàng', 'Bạn có chắc chắn muốn xóa thành viên [${fn:escapeXml(kh.tenKH)}]? Thao tác không thể hoàn tác.', this.href);">
                                                            <i class="bi bi-trash"></i>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="6" class="text-center text-muted py-5"><i class="bi bi-inbox fs-1 d-block mb-3 opacity-50"></i>Chưa có thành viên nào.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                                </tbody>
                            </table>
                            <c:if test="${totalPages > 1}">
                                <jsp:include page="../layout/pagination.jsp"><jsp:param name="baseUrl" value="/khach-hang?action=list" /></jsp:include>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="../layout/confirm_modal.jsp" %>
<!-- MODAL THÊM KHÁCH HÀNG CHUẨN ĐỒNG BỘ -->
<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header border-0 py-3 bg-light">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-person-plus-fill text-brand me-2"></i>Mở Thẻ Thành Viên</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/khach-hang" method="post" onsubmit="showConfirmForm(event, this, 'Mở Thẻ Mới', 'Xác nhận tạo thẻ thành viên?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-3">
                        <label class="form-label fw-semibold small text-muted text-uppercase">Họ và Tên</label>
                        <input type="text" class="form-control fw-bold" name="tenKhachHang" placeholder="Nhập tên khách..." required>
                    </div>
                    <div class="mb-2">
                        <label class="form-label fw-semibold small text-muted text-uppercase">Số Điện Thoại</label>
                        <input type="tel" class="form-control fw-bold text-primary" name="soDienThoai" placeholder="Gồm 10 số..." required pattern="\d{10,11}">
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 p-3">
                    <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border me-2" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4 shadow-sm"><i class="bi bi-check2 me-1"></i> Đăng Ký Thẻ</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- MODAL CẬP NHẬT CHUẨN ĐỒNG BỘ -->
<div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header bg-light border-0 py-3">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-pencil-square text-brand me-2"></i>Cập Nhật Thành Viên</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/khach-hang" method="post" onsubmit="showConfirmForm(event, this, 'Lưu Thay Đổi', 'Xác nhận cập nhật thông tin khách hàng này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="maKH" id="edit_maKH">
                    <div class="mb-3">
                        <label class="form-label text-muted fw-bold small text-uppercase">Mã Khách Hàng</label>
                        <input type="text" class="form-control bg-light fw-bold text-muted border-0" id="display_maKH" disabled>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold text-dark small text-uppercase">Họ và Tên</label>
                        <input type="text" class="form-control fw-bold" name="tenKhachHang" id="edit_tenKH" required>
                    </div>
                    <div class="mb-2">
                        <label class="form-label fw-bold text-dark small text-uppercase">Số Điện Thoại</label>
                        <input type="text" class="form-control fw-bold text-primary" name="soDienThoai" id="edit_sdt" required pattern="\d{10,11}">
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 p-3">
                    <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4">Lưu Thay Đổi</button>
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
    function fillEditModal(maKH, tenKH, sdt) {
        document.getElementById("edit_maKH").value = maKH;
        document.getElementById("display_maKH").value = maKH;
        document.getElementById("edit_tenKH").value = tenKH;
        document.getElementById("edit_sdt").value = sdt;
    }
    $(document).ready(function() {
        if ($('#khachHangTable tbody td').length > 1) {
            $('#khachHangTable').DataTable({
                "responsive": true, "paging": false, "searching": false, "info": false, "order": [],
                "columnDefs": [{ "orderable": false, "targets": [3] }],
                "language": { "emptyTable": "Chưa có dữ liệu." }
            });
        }
    });
</script>
</body>
</html>