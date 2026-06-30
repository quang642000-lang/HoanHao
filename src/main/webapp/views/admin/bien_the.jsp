<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Biến Thể & Kích Cỡ - TEA POS</title>
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
                <h4 class="text-dark fw-bold m-0"><i class="bi bi-aspect-ratio text-brand me-2"></i>Quản Lý Size</h4>
            </div>
            <div class="d-flex align-items-center"><span class="fw-bold text-dark d-none d-md-block me-3">${sessionScope.nhanVienDangNhap.hoTen}</span></div>
        </header>
        <div class="container-fluid px-4 mb-5">
            <div class="col-12 mb-4">
                <!-- FORM TÌM KIẾM CHUẨN ĐỒNG BỘ -->
                <div class="card mb-3 border-0 shadow-sm" style="border-radius: 16px;">
                    <div class="card-body p-3">
                        <form action="${pageContext.request.contextPath}/bien-the" method="get">
                            <input type="hidden" name="action" value="search">
                            <div class="row g-3 align-items-center">
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                                        <input type="text" class="form-control border-start-0 ps-0" name="keyword" placeholder="Tìm theo tên món, mã, size..." value="${requestScope.selectedKeyword}">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-funnel"></i></span>
                                        <select class="form-select border-start-0 ps-0" name="filterSanPham">
                                            <option value="all">Tất cả sản phẩm</option>
                                            <c:forEach var="sp" items="${requestScope.danhSachSP}">
                                                <option value="${sp.maSP}" ${requestScope.selectedSanPham == sp.maSP ? 'selected' : ''}>${sp.tenSanPham}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <button type="submit" class="btn btn-dark w-100 fw-bold rounded-pill">Tìm Kiếm</button>
                                </div>
                                <div class="col-md-2 text-end">
                                    <c:if test="${not empty requestScope.selectedKeyword or (not empty requestScope.selectedSanPham and requestScope.selectedSanPham != 'all')}">
                                        <a href="${pageContext.request.contextPath}/bien-the?action=list" class="btn btn-outline-danger fw-bold w-100 rounded-pill"><i class="bi bi-x-circle"></i> Xóa Lọc</a>
                                    </c:if>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <!-- BẢNG DỮ LIỆU CHUẨN ĐỒNG BỘ -->
                <div class="card shadow-sm border-0" style="border-radius: 16px;">
                    <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center py-3">
                        <h5 class="mb-0 fw-bold text-dark"><i class="bi bi-list-columns-reverse text-brand me-2"></i>Danh Sách Giá Theo Size</h5>
                        <div>
                            <button type="button" class="btn btn-brand fw-bold shadow-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#addModal">
                                <i class="bi bi-plus-circle me-1"></i> Thêm Kích Cỡ
                            </button>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover table-custom mb-0 text-center align-middle dt-responsive nowrap" style="width:100%" id="bienTheTable">
                                <thead>
                                <tr>
                                    <th width="5%">STT</th>
                                    <th>Mã BT</th>
                                    <th class="text-start">Sản Phẩm Gốc</th>
                                    <th>Size</th>
                                    <th class="text-end">Giá Bán</th>
                                    <th>Trạng Thái</th>
                                    <th>Thao Tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${not empty requestScope.danhSach}">
                                        <c:forEach var="bt" items="${requestScope.danhSach}" varStatus="status">
                                            <tr>
                                                <td class="fw-semibold text-muted">${(empty currentPage ? 0 : currentPage - 1) * 5 + status.index + 1}</td>
                                                <td class="fw-semibold text-muted">${bt.maBienThe}</td>
                                                <td class="text-start fw-bold text-dark">${bt.sanPham.tenSanPham}</td>
                                                <td><span class="badge bg-light text-dark border px-3 rounded-pill">${bt.kichCo}</span></td>
                                                <td class="text-end fw-bold text-danger fs-6"><fmt:formatNumber value="${bt.giaBan}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${bt.trangThai == 1}"><span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 rounded-pill px-3 py-2">Hoạt Động</span></c:when>
                                                        <c:otherwise><span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25 rounded-pill px-3 py-2">Tạm Ngừng</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="btn-group btn-group-sm shadow-sm">
                                                        <c:choose>
                                                            <c:when test="${bt.trangThai == 1}">
                                                                <a href="${pageContext.request.contextPath}/bien-the?action=toggle-status&id=${bt.maBienThe}&status=0" class="btn btn-light text-warning border" title="Tạm ngừng" onclick="event.preventDefault(); showConfirmLink('Tạm Ngừng', 'Tạm ngừng kinh doanh biến thể này?', this.href);"><i class="bi bi-pause-circle"></i></a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${pageContext.request.contextPath}/bien-the?action=toggle-status&id=${bt.maBienThe}&status=1" class="btn btn-light text-success border" title="Mở lại" onclick="event.preventDefault(); showConfirmLink('Mở Lại', 'Mở bán lại biến thể này?', this.href);"><i class="bi bi-play-circle"></i></a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <button class="btn btn-light text-primary border" data-bs-toggle="modal" data-bs-target="#editModal" onclick="fillEditModal('${bt.maBienThe}', '${bt.sanPham.maSP}', '${fn:escapeXml(bt.kichCo)}', '${bt.giaBan}')">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/bien-the?action=delete&id=${bt.maBienThe}" class="btn btn-light text-danger border" onclick="event.preventDefault(); showConfirmLink('Xóa Biến Thể', 'Bạn có chắc chắn muốn xóa vĩnh viễn biến thể này?', this.href);"><i class="bi bi-trash"></i></a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="7" class="text-center text-muted py-5"><i class="bi bi-inbox fs-1 d-block mb-3 opacity-50"></i>Chưa có dữ liệu biến thể.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                                </tbody>
                            </table>
                            <c:if test="${totalPages > 1}">
                                <jsp:include page="layout/pagination.jsp"><jsp:param name="baseUrl" value="/bien-the?action=list" /></jsp:include>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="layout/confirm_modal.jsp" %>
<!-- MODAL THÊM MỚI CHUẨN ĐỒNG BỘ -->
<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header border-0 py-3 bg-light">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-plus-circle-fill text-brand me-2"></i>Thêm Kích Cỡ Mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/bien-the" method="post" onsubmit="showConfirmForm(event, this, 'Tạo Kích Cỡ Mới', 'Xác nhận tạo kích cỡ mới cho sản phẩm này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-3">
                        <label class="form-label fw-semibold text-muted small text-uppercase">Sản Phẩm Gốc</label>
                        <select class="form-select bg-light fw-bold" name="maSP" required>
                            <option value="" disabled selected>-- Chọn món --</option>
                            <c:forEach var="sp" items="${requestScope.danhSachSP}">
                                <option value="${sp.maSP}">${sp.tenSanPham}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold text-muted small text-uppercase">Kích Cỡ (Size)</label>
                        <input type="text" class="form-control fw-bold" name="kichCo" placeholder="VD: M, L, XL..." required>
                    </div>
                    <div class="mb-2">
                        <label class="form-label fw-semibold text-muted small text-uppercase">Giá Bán (VNĐ)</label>
                        <input type="number" class="form-control text-danger fw-bold fs-5" name="giaBan" min="0" placeholder="0" required>
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 p-3">
                    <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border me-2" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4 shadow-sm"><i class="bi bi-check2 me-1"></i> Lưu Dữ Liệu</button>
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
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-pencil-square text-brand me-2"></i>Cập Nhật Kích Cỡ</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/bien-the" method="post" onsubmit="showConfirmForm(event, this, 'Lưu Thay Đổi', 'Bạn chắc chắn muốn lưu thông này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="maBienThe" id="edit_maBienThe">
                    <div class="mb-3">
                        <label class="form-label text-muted fw-bold small text-uppercase">Mã Biến Thể</label>
                        <input type="text" class="form-control bg-light fw-bold text-muted border-0" id="display_maBienThe" disabled>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold text-dark small text-uppercase">Sản Phẩm</label>
                        <select class="form-select fw-bold" name="maSP" id="edit_maSP" required>
                            <c:forEach var="sp" items="${requestScope.danhSachSP}">
                                <option value="${sp.maSP}">${sp.tenSanPham}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold text-dark small text-uppercase">Kích Cỡ (Size)</label>
                        <input type="text" class="form-control fw-bold" name="kichCo" id="edit_kichCo" required>
                    </div>
                    <div class="mb-2">
                        <label class="form-label fw-bold text-dark small text-uppercase">Giá Bán (VNĐ)</label>
                        <input type="number" class="form-control text-danger fw-bold fs-5" name="giaBan" id="edit_giaBan" min="0" required>
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 d-flex justify-content-between p-3">
                    <a href="#" id="btn-delete-modal" class="btn btn-outline-danger fw-bold rounded-pill px-4" onclick="event.preventDefault(); showConfirmLink('Xóa Biến Thể', 'Bạn có chắc chắn muốn xóa vĩnh viễn?', this.href);">Xóa</a>
                    <div>
                        <button type="button" class="btn btn-light fw-bold rounded-pill px-4 me-2 border" data-bs-dismiss="modal">Hủy</button>
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
    function fillEditModal(maBT, maSP, kichCo, giaBan) {
        document.getElementById('edit_maBienThe').value = maBT;
        document.getElementById('display_maBienThe').value = maBT;
        document.getElementById('edit_maSP').value = maSP;
        document.getElementById('edit_kichCo').value = kichCo;
        document.getElementById('edit_giaBan').value = giaBan;
        document.getElementById('btn-delete-modal').href = '${pageContext.request.contextPath}/bien-the?action=delete&id=' + maBT;
    }
    $(document).ready(function() {
        if ($('#bienTheTable tbody td').length > 1) {
            $('#bienTheTable').DataTable({
                "responsive": true, "paging": false, "searching": false, "info": false, "order": [],
                "columnDefs": [{ "orderable": false, "targets": [15] }],
                "language": { "emptyTable": "Chưa có dữ liệu." }
            });
        }
    });
</script>
</body>
</html>