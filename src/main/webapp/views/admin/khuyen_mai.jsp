<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Khuyến Mãi - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
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
                <h4 class="text-dark fw-bold m-0"><i class="bi bi-ticket-perforated-fill text-brand me-2"></i>Quản Lý Khuyến Mãi</h4>
            </div>
            <div class="d-flex align-items-center"><span class="fw-bold text-dark d-none d-md-block me-3">${sessionScope.nhanVienDangNhap.hoTen}</span></div>
        </header>
        <div class="container-fluid px-4 mb-5">
            <div class="col-12 mb-4">
                <!-- FORM TÌM KIẾM CHUẨN ĐỒNG BỘ -->
                <div class="card mb-3 border-0 shadow-sm" style="border-radius: 16px;">
                    <div class="card-body p-3">
                        <form action="${pageContext.request.contextPath}/khuyen-mai" method="get">
                            <input type="hidden" name="action" value="search">
                            <div class="row g-3 align-items-center">
                                <div class="col-md-8">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                                        <input type="text" class="form-control border-start-0 ps-0" name="keyword" placeholder="Tìm tên hoặc mã code khuyến mãi..." value="${param.keyword}">
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <button type="submit" class="btn btn-dark w-100 fw-bold rounded-pill">Tìm Kiếm</button>
                                </div>
                                <div class="col-md-2 text-end">
                                    <c:if test="${not empty param.keyword}">
                                        <a href="${pageContext.request.contextPath}/khuyen-mai?action=list" class="btn btn-outline-danger fw-bold w-100 rounded-pill"><i class="bi bi-x-circle"></i> Xóa Lọc</a>
                                    </c:if>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <!-- BẢNG DỮ LIỆU CHUẨN ĐỒNG BỘ -->
                <div class="card shadow-sm border-0" style="border-radius: 16px;">
                    <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center py-3">
                        <h5 class="mb-0 fw-bold text-dark"><i class="bi bi-ticket-detailed text-brand me-2"></i>Danh Sách Mã Giảm Giá</h5>
                        <div>
                            <button type="button" class="btn btn-brand fw-bold shadow-sm rounded-pill px-3" data-bs-toggle="modal" data-bs-target="#addModal">
                                <i class="bi bi-plus-circle me-1"></i> Thêm Khuyến Mãi
                            </button>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover table-custom mb-0 text-center align-middle dt-responsive nowrap" style="width:100%" id="khuyenMaiTable">
                                <thead>
                                <tr>
                                    <th width="5%">STT</th>
                                    <th class="text-start">Tên Chương Trình</th>
                                    <th>Mã Code</th>
                                    <th>Mức Giảm</th>
                                    <th>Đã Dùng</th>
                                    <th>Hạn Dùng</th>
                                    <th>Trạng Thái</th>
                                    <th>Thao Tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${not empty requestScope.danhSach}">
                                        <c:forEach var="km" items="${requestScope.danhSach}" varStatus="status">
                                            <tr>
                                                <td class="fw-semibold text-muted">${(currentPage != null ? (currentPage - 1) * 5 : 0) + status.index + 1}</td>
                                                <td class="text-start fw-bold text-dark" style="max-width: 200px; white-space: normal;">${km.tenKM}</td>
                                                <td><span class="badge border border-danger text-danger bg-danger bg-opacity-10 py-2 px-3 fw-bold fs-6 rounded-pill">${km.maCode}</span></td>
                                                <td class="fw-bold text-danger">
                                                    <c:choose>
                                                        <c:when test="${km.loaiGiamGia == 'Phần Trăm'}">${km.giaTriGiam}%</c:when>
                                                        <c:otherwise><fmt:formatNumber value="${km.giaTriGiam}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></c:otherwise>
                                                    </c:choose>
                                                    <div class="small fw-medium text-muted mt-1">(Đơn tối thiểu: <fmt:formatNumber value="${km.dieuKienToiThieu}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>)</div>
                                                </td>
                                                <td class="fw-bold">
                                                    <c:choose>
                                                        <c:when test="${km.soLuong - km.soLuongDaDung <= 0}"><span class="text-danger">Hết (${km.soLuongDaDung}/${km.soLuong})</span></c:when>
                                                        <c:otherwise><span class="text-primary">${km.soLuongDaDung} / ${km.soLuong}</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="small fw-medium text-muted">
                                                    <fmt:formatDate value="${km.ngayBatDau}" pattern="dd/MM/yyyy"/><br><i class="bi bi-three-dots-vertical"></i><br><fmt:formatDate value="${km.ngayKetThuc}" pattern="dd/MM/yyyy"/>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${km.soLuong - km.soLuongDaDung <= 0}"><span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 rounded-pill px-3 py-2">Hết Mã</span></c:when>
                                                        <c:when test="${km.trangThai == 1}"><span class="badge bg-success bg-opacity-10 text-success border border-success border-opacity-25 rounded-pill px-3 py-2">Đang Chạy</span></c:when>
                                                        <c:otherwise><span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25 rounded-pill px-3 py-2">Đã Dừng</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="btn-group btn-group-sm shadow-sm">
                                                        <c:choose>
                                                            <c:when test="${km.trangThai == 1}">
                                                                <a href="${pageContext.request.contextPath}/khuyen-mai?action=toggle-status&id=${km.maKM}&status=0" class="btn btn-light text-warning border" title="Dừng" onclick="event.preventDefault(); showConfirmLink('Dừng Khuyến Mãi', 'Tạm ngưng chương trình khuyến mãi này?', this.href);"><i class="bi bi-pause-circle"></i></a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${pageContext.request.contextPath}/khuyen-mai?action=toggle-status&id=${km.maKM}&status=1" class="btn btn-light text-success border" title="Bật" onclick="event.preventDefault(); showConfirmLink('Bật Lại', 'Mở lại chương trình khuyến mãi này?', this.href);"><i class="bi bi-play-circle"></i></a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <button class="btn btn-light text-primary border" data-bs-toggle="modal" data-bs-target="#editModal" onclick="fillEditModal('${km.maKM}', '${fn:escapeXml(km.tenKM)}', '${km.maCode}', '${km.loaiGiamGia}', '${km.giaTriGiam}', '${km.dieuKienToiThieu}', '${km.soLuong}', '${km.soLuongDaDung}', '${km.ngayBatDau}', '${km.ngayKetThuc}')">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/khuyen-mai?action=delete&id=${km.maKM}" class="btn btn-light text-danger border" onclick="event.preventDefault(); showConfirmLink('Xóa Khuyến Mãi', 'Bạn có chắc muốn xóa vĩnh viễn chương trình khuyến mãi này?', this.href);"><i class="bi bi-trash"></i></a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="8" class="text-center text-muted py-5"><i class="bi bi-inbox fs-1 d-block mb-3 opacity-50"></i>Không có chương trình khuyến mãi nào.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                                </tbody>
                            </table>
                            <c:if test="${totalPages > 1}">
                                <jsp:include page="../layout/pagination.jsp"><jsp:param name="baseUrl" value="/khuyen-mai?action=list" /></jsp:include>
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
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header border-0 py-3 bg-light">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-gift-fill text-brand me-2"></i>Phát Hành Mã Giảm Giá Mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/khuyen-mai" method="post" onsubmit="showConfirmForm(event, this, 'Phát Hành Mã', 'Xác nhận tạo mới chương trình khuyến mãi này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-3">
                        <label class="form-label fw-semibold small text-muted text-uppercase">Tên Chương Trình</label>
                        <input type="text" name="tenKm" class="form-control fw-bold" required placeholder="VD: Khuyến mãi Tết">
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-brand text-uppercase">Mã Code Nhập</label>
                            <input type="text" name="maCode" class="form-control text-uppercase fw-bold text-danger" style="border-color: var(--brand-primary);" required placeholder="VD: TET2026">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Loại Giảm</label>
                            <select name="loaiGiamGia" class="form-select fw-bold">
                                <option value="Trực Tiếp">Giảm Trực Tiếp (VNĐ)</option>
                                <option value="Phần Trăm">Giảm Phần Trăm (%)</option>
                            </select>
                        </div>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-4">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Mức Giảm</label>
                            <input type="number" name="giaTriGiam" class="form-control fw-bold text-dark" min="1" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Đơn Tối Thiểu</label>
                            <input type="number" name="dieuKienToiThieu" class="form-control fw-bold" value="0" min="0" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold small text-primary text-uppercase">Số Lượng Mã</label>
                            <input type="number" name="soLuong" class="form-control fw-bold text-primary" value="100" min="1" required>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Từ Ngày</label>
                            <input type="date" name="ngayBatDau" class="form-control" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Đến Ngày</label>
                            <input type="date" name="ngayKetThuc" class="form-control" required>
                        </div>
                    </div>
                </div>
                <div class="modal-footer bg-light border-0 p-3">
                    <button type="button" class="btn btn-light fw-bold rounded-pill px-4 border me-2" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-brand fw-bold rounded-pill px-4 shadow-sm"><i class="bi bi-check2-circle me-1"></i> Phát Hành</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- MODAL SỬA CHUẨN ĐỒNG BỘ -->
<div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content shadow-lg border-0" style="border-radius: 16px;">
            <div class="modal-header bg-light border-0 py-3">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-pencil-square text-brand me-2"></i>Cập Nhật Khuyến Mãi</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/khuyen-mai" method="post" onsubmit="showConfirmForm(event, this, 'Lưu Thay Đổi', 'Bạn chắc chắn muốn lưu thông tin này?');">
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="maKm" id="edit_maKM">
                    <div id="lock_warning" class="alert alert-warning py-2 px-3 small fw-medium mb-4 rounded-3 border-0 bg-opacity-50" style="display:none; color: #B45309;">
                        <i class="bi bi-shield-lock-fill"></i> Mã đã có người dùng. Hệ thống khóa sửa đổi Loại Giảm, Mức Giảm và Đơn Tối Thiểu.
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small text-muted text-uppercase">Tên chương trình</label>
                        <input type="text" class="form-control fw-bold" name="tenKm" id="edit_tenKM" required>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Mã Code</label>
                            <input type="text" class="form-control text-uppercase fw-bold text-danger" name="maCode" id="edit_maCode" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Loại Giảm</label>
                            <select class="form-select fw-bold" name="loaiGiamGia" id="edit_loaiGiamGia" required>
                                <option value="Trực Tiếp">Giảm Trực Tiếp (VNĐ)</option>
                                <option value="Phần Trăm">Giảm Phần Trăm (%)</option>
                            </select>
                        </div>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-4">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Mức Giảm</label>
                            <input type="number" class="form-control fw-bold" name="giaTriGiam" id="edit_giaTriGiam" min="1" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Đơn Min</label>
                            <input type="number" class="form-control fw-bold" name="dieuKienToiThieu" id="edit_dieuKienToiThieu" min="0" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold small text-primary text-uppercase">Tổng Phát <span id="display_daDung" class="text-danger fw-normal ms-1 small"></span></label>
                            <input type="number" class="form-control fw-bold text-primary" name="soLuong" id="edit_soLuong" required>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Ngày Bắt Đầu</label>
                            <input type="date" class="form-control" name="ngayBatDau" id="edit_ngayBatDau" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-muted text-uppercase">Ngày Kết Thúc</label>
                            <input type="date" class="form-control" name="ngayKetThuc" id="edit_ngayKetThuc" required>
                        </div>
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
    function fillEditModal(maKM, tenKM, maCode, loaiGiamGia, giaTri, toiThieu, soLuong, daDung, start, end) {
        document.getElementById('edit_maKM').value = maKM;
        document.getElementById('edit_tenKM').value = tenKM;
        document.getElementById('edit_maCode').value = maCode;
        let selectLoaiGiam = document.getElementById('edit_loaiGiamGia');
        let inputGiaTri = document.getElementById('edit_giaTriGiam');
        let inputToiThieu = document.getElementById('edit_dieuKienToiThieu');
        let lockWarning = document.getElementById('lock_warning');
        selectLoaiGiam.value = loaiGiamGia;
        inputGiaTri.value = giaTri;
        inputToiThieu.value = toiThieu;
        if (daDung > 0) {
            lockWarning.style.display = 'block';
            inputGiaTri.readOnly = true; inputToiThieu.readOnly = true;
            inputGiaTri.classList.add('bg-light'); inputToiThieu.classList.add('bg-light');
            selectLoaiGiam.style.pointerEvents = 'none'; selectLoaiGiam.classList.add('bg-light');
        } else {
            lockWarning.style.display = 'none';
            inputGiaTri.readOnly = false; inputToiThieu.readOnly = false;
            inputGiaTri.classList.remove('bg-light'); inputToiThieu.classList.remove('bg-light');
            selectLoaiGiam.style.pointerEvents = 'auto'; selectLoaiGiam.classList.remove('bg-light');
        }
        let inputSoLuong = document.getElementById('edit_soLuong');
        inputSoLuong.value = soLuong;
        inputSoLuong.min = daDung;
        document.getElementById('display_daDung').innerText = "(" + daDung + " đã dùng)";
        document.getElementById('edit_ngayBatDau').value = start;
        document.getElementById('edit_ngayKetThuc').value = end;
    }
    $(document).ready(function() {
        if ($('#khuyenMaiTable tbody td').length > 1) {
            $('#khuyenMaiTable').DataTable({
                "responsive": true, "paging": false, "searching": false, "info": false, "order": [],
                "columnDefs": [{ "orderable": false, "targets": [4] }],
                "language": { "emptyTable": "Không có chương trình khuyến mãi nào." }
            });
        }
    });
</script>
</body>
</html>