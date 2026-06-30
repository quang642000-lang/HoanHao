<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Sản Phẩm - TEA POS</title>
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
                <h4 class="text-dark fw-bold m-0"><i class="bi bi-cup-hot-fill text-brand me-2"></i>Quản Lý Sản Phẩm</h4>
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
                        <form action="${pageContext.request.contextPath}/san-pham" method="get">
                            <input type="hidden" name="action" value="search">
                            <div class="row g-3 align-items-center">
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                                        <input type="text" class="form-control border-start-0 ps-0" name="keyword" placeholder="Tên hoặc mã SP..." value="${requestScope.selectedKeyword}">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-funnel"></i></span>
                                        <select class="form-select border-start-0 ps-0" name="filterDanhMuc">
                                            <option value="all">Tất cả danh mục</option>
                                            <c:forEach var="dm" items="${requestScope.danhSachDm}">
                                                <option value="${dm.maDanhMuc}" ${requestScope.selectedDanhMuc == dm.maDanhMuc ? 'selected' : ''}>${dm.tenDanhMuc}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <button type="submit" class="btn btn-dark w-100 fw-bold rounded-pill">Tìm Kiếm</button>
                                </div>
                                <div class="col-md-2 text-end">
                                    <c:if test="${not empty requestScope.selectedKeyword or (not empty requestScope.selectedDanhMuc and requestScope.selectedDanhMuc != 'all')}">
                                        <a href="${pageContext.request.contextPath}/san-pham?action=list" class="btn btn-outline-danger fw-bold w-100 rounded-pill"><i class="bi bi-x-circle"></i> Xóa Lọc</a>
                                    </c:if>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <!-- BẢNG DỮ LIỆU CHUẨN ĐỒNG BỘ -->
                <div class="card shadow-sm border-0" style="border-radius: 16px;">
                    <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center py-3">
                        <h5 class="mb-0 fw-bold text-dark"><i class="bi bi-list-task text-brand me-2"></i>Danh Sách Thực Đơn</h5>
                        <div>
                            <button type="button" class="btn btn-brand fw-bold shadow-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#addModal">
                                <i class="bi bi-plus-circle me-1"></i> Thêm Món Mới
                            </button>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover table-custom mb-0 text-center align-middle dt-responsive nowrap" style="width:100%" id="sanPhamTable">
                                <thead>
                                <tr>
                                    <th width="5%">STT</th>
                                    <th width="10%">Ảnh</th>
                                    <th width="10%">Mã SP</th>
                                    <th class="text-start">Tên Sản Phẩm</th>
                                    <th>Danh Mục</th>
                                    <th>Trạng Thái</th>
                                    <th>Thao Tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${not empty requestScope.danhSachSp}">
                                        <c:forEach var="sp" items="${requestScope.danhSachSp}" varStatus="status">
                                            <tr>
                                                <td class="fw-semibold text-muted">${(empty currentPage ? 0 : currentPage - 1) * 5 + status.index + 1}</td>
                                                <td>
                                                    <img src="${pageContext.request.contextPath}/image/${not empty sp.hinhAnh ? sp.hinhAnh : 'default.png'}" class="product-img shadow-sm rounded" style="width: 45px; height: 45px; object-fit: cover;" onerror="this.src='https://placehold.co/100x100?text=No+Image'" alt="${fn:escapeXml(sp.tenSanPham)}">
                                                </td>
                                                <td class="fw-semibold text-muted">${sp.maSP}</td>
                                                <td class="text-start fw-bold text-dark fs-6">${sp.tenSanPham}</td>
                                                <td><span class="badge bg-light text-dark border px-3 rounded-pill">${sp.danhMuc.tenDanhMuc}</span></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${sp.trangThai == 1}"><span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 rounded-pill px-3 py-2">Đang Bán</span></c:when>
                                                        <c:otherwise><span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25 rounded-pill px-3 py-2">Ngừng Bán</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="btn-group btn-group-sm shadow-sm">
                                                        <c:choose>
                                                            <c:when test="${sp.trangThai == 1}">
                                                                <a href="${pageContext.request.contextPath}/san-pham?action=toggle-status&id=${sp.maSP}&status=0" class="btn btn-light text-warning border" title="Ngừng bán" onclick="event.preventDefault(); showConfirmLink('Tạm Ngừng', 'Tạm ngừng bán sản phẩm này?', this.href);"><i class="bi bi-pause-circle"></i></a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${pageContext.request.contextPath}/san-pham?action=toggle-status&id=${sp.maSP}&status=1" class="btn btn-light text-success border" title="Mở bán" onclick="event.preventDefault(); showConfirmLink('Mở Bán Lại', 'Tiếp tục bán sản phẩm này?', this.href);"><i class="bi bi-play-circle"></i></a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <button class="btn btn-light text-primary border" data-bs-toggle="modal" data-bs-target="#editSpModal" onclick="fillEditModal('${sp.maSP}', '${fn:escapeXml(sp.tenSanPham)}', '${sp.hinhAnh}', '${sp.danhMuc.maDanhMuc}')">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/san-pham?action=delete&id=${sp.maSP}" class="btn btn-light text-danger border" onclick="event.preventDefault(); showConfirmLink('Xóa Sản Phẩm', 'Bạn có chắc chắn muốn xóa sản phẩm này không?', this.href);">
                                                            <i class="bi bi-trash"></i>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="7" class="text-center text-muted py-5"><i class="bi bi-inbox fs-1 d-block mb-3 opacity-50"></i>Chưa có sản phẩm nào.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                                </tbody>
                            </table>
                            <c:if test="${totalPages > 1}">
                                <jsp:include page="../layout/pagination.jsp"><jsp:param name="baseUrl" value="/san-pham?action=list" /></jsp:include>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="../layout/confirm_modal.jsp" %>
<!-- MODAL THÊM MỚI CHUẨN ĐỒNG BỘ -->
<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header border-0 py-3 bg-light">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-plus-circle-fill text-brand me-2"></i>Thêm Món Mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/san-pham" method="post" enctype="multipart/form-data" onsubmit="showConfirmForm(event, this, 'Thêm Món', 'Bạn có chắc chắn muốn thêm món này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-3">
                        <label class="form-label fw-semibold text-muted small text-uppercase">Danh Mục</label>
                        <select class="form-select bg-light fw-bold" name="maDanhMuc" required>
                            <option value="" disabled selected>-- Chọn danh mục --</option>
                            <c:forEach var="dm" items="${requestScope.danhSachDm}">
                                <option value="${dm.maDanhMuc}">${dm.tenDanhMuc}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold text-muted small text-uppercase">Tên Sản Phẩm</label>
                        <input type="text" class="form-control fw-bold" name="tenSanPham" placeholder="VD: Trà sữa Oolong..." required>
                    </div>
                    <div class="mb-2">
                        <label class="form-label fw-semibold text-muted small text-uppercase">Tải Hình Ảnh Lên</label>
                        <input type="file" class="form-control" name="hinhAnhFile" accept="image/*">
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 p-3">
                    <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border me-2" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4 shadow-sm"><i class="bi bi-check2 me-1"></i> Lưu Sản Phẩm</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- MODAL CẬP NHẬT CHUẨN ĐỒNG BỘ -->
<div class="modal fade" id="editSpModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header bg-light border-0 py-3">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-pencil-square text-brand me-2"></i>Cập Nhật Món</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/san-pham" method="post" enctype="multipart/form-data" onsubmit="showConfirmForm(event, this, 'Lưu Cập Nhật', 'Bạn chắc chắn muốn lưu thông tin này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="maSP" id="edit_maSP">
                    <input type="hidden" name="oldHinhAnh" id="edit_oldHinhAnh">
                    <div class="mb-3">
                        <label class="form-label text-muted fw-bold small text-uppercase">Mã Sản Phẩm</label>
                        <input type="text" class="form-control bg-light fw-bold text-muted border-0" id="display_maSP" disabled>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold text-dark small text-uppercase">Danh Mục</label>
                        <select class="form-select fw-bold" name="maDanhMuc" id="edit_maDanhMuc" required>
                            <c:forEach var="dm" items="${requestScope.danhSachDm}">
                                <option value="${dm.maDanhMuc}">${dm.tenDanhMuc}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold text-dark small text-uppercase">Tên Sản Phẩm</label>
                        <input type="text" class="form-control fw-bold" name="tenSanPham" id="edit_tenSanPham" required>
                    </div>
                    <div class="mb-2">
                        <label class="form-label fw-bold text-dark small text-uppercase">Thay Ảnh (Trống để giữ nguyên)</label>
                        <input type="file" class="form-control" name="hinhAnhFile" accept="image/*">
                        <div class="mt-3 text-center p-2 bg-light rounded-4 border border-light">
                            <img id="preview_hinhAnh" src="" alt="Ảnh hiện tại" style="width: 80px; height: 80px; object-fit: cover; border-radius: 12px; border: 1px solid #E2E8F0;">
                        </div>
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 d-flex justify-content-between p-3">
                    <a href="#" id="btn-delete-modal" class="btn btn-outline-danger fw-bold rounded-pill px-4" onclick="event.preventDefault(); showConfirmLink('Xóa Sản Phẩm', 'Bạn muốn xóa vĩnh viễn sản phẩm này?', this.href);">Xóa Bỏ</a>
                    <div>
                        <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border me-2" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4">Lưu Lại</button>
                    </div>
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
    function fillEditModal(maSP, tenSanPham, hinhAnh, maDanhMuc) {
        document.getElementById('edit_maSP').value = maSP;
        document.getElementById('display_maSP').value = maSP;
        document.getElementById('edit_tenSanPham').value = tenSanPham;
        document.getElementById('edit_oldHinhAnh').value = hinhAnh;
        let imgUrl = hinhAnh ? ('${pageContext.request.contextPath}/image/' + hinhAnh) : 'https://placehold.co/100x100?text=No+Image';
        document.getElementById('preview_hinhAnh').src = imgUrl;
        document.getElementById('edit_maDanhMuc').value = maDanhMuc;
        document.getElementById('btn-delete-modal').href = '${pageContext.request.contextPath}/san-pham?action=delete&id=' + maSP;
    }
    $(document).ready(function() {
        if ($('#sanPhamTable tbody td').length > 1) {
            $('#sanPhamTable').DataTable({
                "responsive": true, "paging": false, "searching": false, "info": false, "order": [],
                "columnDefs": [{ "orderable": false, "targets": [1] }],
                "language": { "emptyTable": "Chưa có dữ liệu." }
            });
        }
    });
</script>
</body>
</html>