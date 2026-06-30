<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Danh Mục - TEA POS</title>
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
                <h4 class="text-dark fw-bold m-0"><i class="bi bi-folder-fill text-brand me-2"></i>Quản Lý Danh Mục</h4>
            </div>
            <div class="d-flex align-items-center">
                <span class="fw-bold text-dark d-none d-md-block me-3">${sessionScope.nhanVienDangNhap.hoTen}</span>
            </div>
        </header>
        <div class="container-fluid px-4 mb-5">
            <div class="col-12 mb-4">
                <!-- FORM TÌM KIẾM CHUẨN ĐỒNG BỘ -->
                <div class="card mb-3 border-0 shadow-sm" style="border-radius: 16px;">
                    <div class="card-body p-3">
                        <form action="${pageContext.request.contextPath}/danh-muc" method="get">
                            <input type="hidden" name="action" value="search">
                            <div class="row g-3 align-items-center">
                                <div class="col-md-8">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                                        <input type="text" class="form-control border-start-0 ps-0" name="keyword" placeholder="Tìm tên danh mục..." value="${param.keyword}">
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <button type="submit" class="btn btn-dark w-100 fw-bold rounded-pill">Tìm Kiếm</button>
                                </div>
                                <div class="col-md-2 text-end">
                                    <c:if test="${not empty param.keyword}">
                                        <a href="${pageContext.request.contextPath}/danh-muc?action=list" class="btn btn-outline-danger fw-bold w-100 rounded-pill"><i class="bi bi-x-circle"></i> Xóa Lọc</a>
                                    </c:if>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <!-- BẢNG DỮ LIỆU CHUẨN ĐỒNG BỘ -->
                <div class="card shadow-sm border-0" style="border-radius: 16px;">
                    <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center py-3">
                        <h5 class="mb-0 fw-bold text-dark"><i class="bi bi-list-ul text-brand me-2"></i>Danh Sách Phân Loại</h5>
                        <div>
                            <button type="button" class="btn btn-brand fw-bold shadow-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#addModal">
                                <i class="bi bi-plus-circle me-1"></i> Thêm Mới
                            </button>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover table-custom mb-0 text-center align-middle dt-responsive nowrap" style="width:100%" id="danhMucTable">
                                <thead>
                                <tr>
                                    <th width="10%">STT</th>
                                    <th width="20%">Mã DM</th>
                                    <th class="text-start">Tên Danh Mục</th>
                                    <th width="20%">Thao Tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="dm" items="${requestScope.danhSach}" varStatus="status">
                                    <tr>
                                        <td class="fw-semibold text-muted">
                                            <c:choose>
                                                <c:when test="${not empty currentPage}">${(currentPage - 1) * 5 + status.index + 1}</c:when>
                                                <c:otherwise>${status.index + 1}</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fw-semibold text-muted">${dm.maDanhMuc}</td>
                                        <td class="text-start fw-bold text-dark fs-6">${dm.tenDanhMuc}</td>
                                        <td>
                                            <div class="btn-group btn-group-sm shadow-sm">
                                                <button class="btn btn-light text-primary border" data-bs-toggle="modal" data-bs-target="#editModal" data-id="${dm.maDanhMuc}" data-name="${fn:escapeXml(dm.tenDanhMuc)}" onclick="fillEditModal(this.getAttribute('data-id'), this.getAttribute('data-name'))">
                                                    <i class="bi bi-pencil-square"></i>
                                                </button>
                                                <a href="${pageContext.request.contextPath}/danh-muc?action=delete&id=${dm.maDanhMuc}" class="btn btn-light text-danger border" data-name="${fn:escapeXml(dm.tenDanhMuc)}" onclick="event.preventDefault(); showConfirmLink('Xóa Danh Mục', 'Bạn có chắc chắn muốn xóa danh mục [' + this.getAttribute('data-name') + ']?', this.href);">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                            <c:if test="${totalPages > 1}">
                                <jsp:include page="../layout/pagination.jsp"><jsp:param name="baseUrl" value="/danh-muc?action=list" /></jsp:include>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="../layout/confirm_modal.jsp" %>
<!-- MODAL THÊM MỚI -->
<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header border-0 py-3 bg-light">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-plus-circle-fill text-brand me-2"></i>Thêm Danh Mục Mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/danh-muc" method="post" onsubmit="showConfirmForm(event, this, 'Tạo Danh Mục', 'Xác nhận tạo danh mục mới này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-3">
                        <label class="form-label fw-semibold text-muted small text-uppercase">Tên Danh Mục</label>
                        <input type="text" class="form-control fw-bold" name="tenDanhMuc" placeholder="VD: Trà Sữa, Cà Phê..." required>
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 p-3">
                    <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border me-2" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4 shadow-sm"><i class="bi bi-check2 me-1"></i> Khởi Tạo</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- MODAL SỬA -->
<div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header bg-light border-0 py-3">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-pencil-square text-brand me-2"></i>Cập Nhật Danh Mục</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/danh-muc" method="post" onsubmit="showConfirmForm(event, this, 'Lưu Thay Đổi', 'Bạn chắc chắn muốn lưu thông tin này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="maDanhMuc" id="edit_maDanhMuc">
                    <div class="mb-3">
                        <label class="form-label text-muted fw-bold small text-uppercase">Mã Danh Mục</label>
                        <input type="text" class="form-control bg-light fw-bold text-muted border-0" id="display_maDanhMuc" disabled>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold text-dark small text-uppercase">Tên Danh Mục</label>
                        <input type="text" class="form-control fw-bold" name="tenDanhMuc" id="edit_tenDanhMuc" required>
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
    function fillEditModal(maDM, tenDM) {
        document.getElementById("edit_maDanhMuc").value = maDM;
        document.getElementById("display_maDanhMuc").value = maDM;
        document.getElementById("edit_tenDanhMuc").value = tenDM;
    }
    $(document).ready(function() {
        if ($('#danhMucTable tbody td').length > 1) {
            $('#danhMucTable').DataTable({
                "responsive": true, "paging": false, "searching": false, "info": false, "order": [],
                "columnDefs": [{ "orderable": false, "targets": [2] }],
                "language": { "emptyTable": "<div class='text-muted py-5'><i class='bi bi-inbox fs-1 d-block mb-3 opacity-50'></i>Chưa có danh mục nào.</div>" }
            });
        }
    });
</script>
</body>
</html>