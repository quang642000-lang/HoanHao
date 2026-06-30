<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Nhận Đơn O2O - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css">
</head>
<body>
<div class="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
    <div class="main-content">
        <header class="top-navbar bg-white shadow-sm px-4 py-3 mb-4">
            <h4 class="text-dark fw-bold m-0"><i class="bi bi-bell-fill text-danger me-2 animate__animated animate__swing animate__infinite"></i>Tiếp Nhận Đơn Hàng O2O</h4>
        </header>
        <div class="container-fluid px-4 mb-5">
            <div class="alert alert-info fw-bold shadow-sm border-0"><i class="bi bi-info-circle-fill me-2"></i> Màn hình tự động cập nhật đơn mới mỗi 5 giây. Không cần tải lại trang.</div>
            <div class="row g-3" id="orderList">
                <div class="col-12 text-center py-5" id="emptyState"><div class="spinner-border text-primary" role="status"></div><p class="mt-2 text-muted fw-bold">Đang quét đơn hàng mới...</p></div>
            </div>
        </div>
    </div>
</div>

<script>
    const appBasePath = '${pageContext.request.contextPath}';
    function formatMoney(n) { return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n); }

    function fetchOrders() {
        fetch(appBasePath + '/api/nhan-don/poll')
            .then(res => res.json())
            .then(data => {
                const container = document.getElementById('orderList');
                if(data.length === 0) {
                    container.innerHTML = `<div class="col-12 text-center py-5 text-muted"><i class="bi bi-cup-hot fs-1 opacity-50 mb-3 d-block"></i><h5 class="fw-bold">Hiện chưa có đơn đặt trước nào!</h5></div>`;
                    return;
                }
                let html = '';
                data.forEach(order => {
                    html += `
                    <div class="col-md-4 col-xl-3">
                        <div class="card shadow-lg border-0 rounded-4 border-top border-warning border-5">
                            <div class="card-body p-4">
                                <span class="badge bg-warning text-dark mb-2">Đơn Đặt Trước (Click & Collect)</span>
                                <h5 class="fw-bold text-dark mb-1">Khách: \${order.tenKH}</h5>
                                <p class="text-muted small fw-medium mb-3"><i class="bi bi-telephone"></i> \${order.sdt}</p>
                                <div class="bg-light rounded p-2 mb-3 text-center border">
                                    <span class="d-block small text-muted">Giờ hẹn lấy:</span>
                                    <span class="fw-bold text-danger fs-6">\${order.henLay}</span>
                                </div>
                                <h4 class="text-end fw-bold text-primary mb-3">\${formatMoney(order.tongTien)}</h4>
                                <div class="d-flex gap-2">
                                    <button class="btn btn-outline-danger flex-grow-1 fw-bold rounded-pill" onclick="updateOrderStatus('\${order.maDH}', 'Đã hủy')">Hủy Đơn</button>
                                    <button class="btn btn-success flex-grow-1 fw-bold rounded-pill shadow-sm" onclick="updateOrderStatus('\${order.maDH}', 'Hoàn thành')">Trả Nước</button>
                                </div>
                            </div>
                        </div>
                    </div>`;
                });
                container.innerHTML = html;
            });
    }

    function updateOrderStatus(maDH, status) {
        if(confirm('Chuyển trạng thái đơn thành: ' + status + '?')) {
            fetch(appBasePath + '/api/nhan-don/poll', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `maDH=\${maDH}&status=\${status}`
            }).then(() => fetchOrders());
        }
    }

    // Polling 5s/lần
    setInterval(fetchOrders, 5000);
    fetchOrders();
</script>
</body>
</html>