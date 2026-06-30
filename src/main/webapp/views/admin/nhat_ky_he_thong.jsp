<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Nhật Ký Hệ Thống - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css">
</head>
<body>
<div class="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
    <div class="main-content">
        <header class="top-navbar bg-white shadow-sm px-4 py-3 d-flex justify-content-between align-items-center mb-4">
            <h4 class="text-dark fw-bold m-0"><i class="bi bi-clock-history text-brand me-2"></i>Kiểm Toán Hệ Thống (Audit Trail)</h4>
        </header>

        <div class="container-fluid px-4 mb-5">
            <div class="alert alert-dark bg-dark text-white border-0 shadow-sm rounded-4 mb-4">
                <i class="bi bi-shield-lock-fill text-warning me-2 fs-5"></i>
                <strong>Hộp Đen Dữ Liệu:</strong> Mọi thao tác Thêm/Sửa/Xóa quan trọng đều được lưu lại dưới dạng chuỗi JSON nguyên thủy.
            </div>

            <div class="card shadow-sm border-0" style="border-radius: 16px;">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-custom mb-0 text-center align-middle">
                            <thead class="bg-light">
                            <tr>
                                <th width="15%">Thời Gian</th>
                                <th width="15%">Nhân Viên</th>
                                <th width="15%">Hành Động</th>
                                <th width="15%">Bảng Dữ Liệu</th>
                                <th class="text-start">Chi Tiết Thay Đổi (JSON)</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="log" items="${requestScope.danhSachLog}">
                                <tr>
                                    <td class="fw-bold text-muted"><fmt:formatDate value="${log.thoiGian}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                    <td class="fw-bold text-primary">${log.nhanVien.hoTen}<br><small class="text-muted">${log.nhanVien.maNV}</small></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${log.hanhDong == 'INSERT'}"><span class="badge bg-success bg-opacity-10 text-success px-3 py-2 rounded-pill">THÊM MỚI</span></c:when>
                                            <c:when test="${log.hanhDong == 'UPDATE'}"><span class="badge bg-warning bg-opacity-10 text-warning px-3 py-2 rounded-pill">CẬP NHẬT</span></c:when>
                                            <c:when test="${log.hanhDong == 'DELETE'}"><span class="badge bg-danger bg-opacity-10 text-danger px-3 py-2 rounded-pill">XÓA BỎ</span></c:when>
                                            <c:otherwise><span class="badge bg-secondary text-white">${log.hanhDong}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="fw-bold text-dark">${log.tableTacDong}<br><small class="text-muted">ID: ${log.recordTacDong}</small></td>
                                    <td class="text-start">
                                        <button class="btn btn-sm btn-outline-dark fw-bold rounded-pill" onclick="viewJson(`${fn:escapeXml(log.dataCu)}`, `${fn:escapeXml(log.dataMoi)}`)">
                                            <i class="bi bi-braces"></i> Xem Data
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty requestScope.danhSachLog}">
                                <tr><td colspan="5" class="text-muted py-5"><i class="bi bi-inbox fs-1 d-block mb-3 opacity-50"></i>Hệ thống chưa ghi nhận log nào.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <jsp:include page="../layout/pagination.jsp"><jsp:param name="baseUrl" value="/nhat-ky?action=list" /></jsp:include>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal Xem JSON -->
<div class="modal fade" id="jsonModal" tabindex="-1">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content shadow-lg border-0">
            <div class="modal-header bg-dark text-white border-0 py-3">
                <h5 class="modal-title fw-bold"><i class="bi bi-filetype-json text-warning me-2"></i>So Sánh Dữ Liệu</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-0">
                <div class="row g-0">
                    <div class="col-6 border-end p-3 bg-light">
                        <h6 class="fw-bold text-danger mb-2">Dữ Liệu Cũ:</h6>
                        <pre id="oldDataView" class="mb-0 bg-white p-2 rounded border" style="max-height: 400px; overflow-y: auto; font-size: 0.85rem;"></pre>
                    </div>
                    <div class="col-6 p-3 bg-light">
                        <h6 class="fw-bold text-success mb-2">Dữ Liệu Mới:</h6>
                        <pre id="newDataView" class="mb-0 bg-white p-2 rounded border" style="max-height: 400px; overflow-y: auto; font-size: 0.85rem;"></pre>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const jsonModal = new bootstrap.Modal(document.getElementById('jsonModal'));
    function viewJson(oldData, newData) {
        try {
            document.getElementById('oldDataView').innerText = oldData ? JSON.stringify(JSON.parse(oldData), null, 4) : "NULL";
            document.getElementById('newDataView').innerText = newData ? JSON.stringify(JSON.parse(newData), null, 4) : "NULL";
        } catch (e) {
            document.getElementById('oldDataView').innerText = oldData;
            document.getElementById('newDataView').innerText = newData;
        }
        jsonModal.show();
    }
</script>
</body>
</html>