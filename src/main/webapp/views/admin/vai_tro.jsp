<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Vai Trò - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css">
</head>
<body>
<div class="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
    <div class="main-content">
        <header class="top-navbar bg-white shadow-sm px-4 py-3 d-flex justify-content-between align-items-center mb-4">
            <h4 class="text-dark fw-bold m-0"><i class="bi bi-shield-check text-brand me-2"></i>Quản Lý Quyền Hạn (RBAC)</h4>
        </header>

        <div class="container-fluid px-4 mb-5">
            <div class="alert alert-primary bg-primary bg-opacity-10 text-primary border-primary border-opacity-25 shadow-sm rounded-4 mb-4 fw-bold">
                <i class="bi bi-info-circle-fill me-2"></i> Vai trò được thiết lập cố định từ Hệ thống để đảm bảo bảo mật. Mọi thay đổi quyền hạn nâng cao vui lòng liên hệ Bộ phận Kỹ Thuật IT.
            </div>

            <div class="row">
                <c:forEach var="vt" items="${requestScope.danhSachVaiTro}">
                    <div class="col-md-6 mb-3">
                        <div class="card shadow-sm border-0 rounded-4 h-100">
                            <div class="card-body p-4 d-flex align-items-center">
                                <div class="rounded-circle d-flex justify-content-center align-items-center text-white me-4 shadow-sm" style="width: 60px; height: 60px; background: ${vt.maVaiTro == 1 ? 'linear-gradient(135deg, #DC2626, #991B1B)' : 'linear-gradient(135deg, #2563EB, #1D4ED8)'};">
                                    <i class="bi ${vt.maVaiTro == 1 ? 'bi-shield-lock-fill' : 'bi-person-badge-fill'} fs-3"></i>
                                </div>
                                <div>
                                    <h5 class="fw-bold text-dark mb-1">${vt.tenVaiTro} <span class="badge bg-light text-muted border ms-2 rounded-pill">Mã quyền: ${vt.maVaiTro}</span></h5>
                                    <p class="text-muted small mb-0 fw-medium">${vt.moTa}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/global.js"></script>
</body>
</html>