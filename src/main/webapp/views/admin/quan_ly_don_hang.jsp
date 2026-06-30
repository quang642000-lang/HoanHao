<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Đơn Hàng - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/global.css">
</head>
<body>
<input type="hidden" id="appContextPath" value="${pageContext.request.contextPath}">
<div class="wrapper">
    <%@ include file="../layout/sidebar.jsp" %>
    <div class="main-content">
        <header class="top-navbar bg-white shadow-sm px-4 py-3 d-flex justify-content-between align-items-center mb-4">
            <h4 class="text-dark fw-bold m-0"><i class="bi bi-receipt-cutoff text-brand me-2"></i>Kho Lưu Trữ Hóa Đơn</h4>
            <div class="d-flex align-items-center"><span class="fw-bold text-dark d-none d-md-block me-3">${sessionScope.nhanVienDangNhap.hoTen}</span></div>
        </header>

        <div class="container-fluid px-4 mb-5">
            <div class="card shadow-sm border-0" style="border-radius: 16px;">
                <div class="card-header bg-white border-bottom py-3">
                    <form action="${pageContext.request.contextPath}/quan-ly-don-hang" method="get" class="row g-3 align-items-center">
                        <div class="col-md-4">
                            <div class="input-group">
                                <span class="input-group-text bg-light fw-bold text-muted border-end-0">Từ</span>
                                <input type="date" class="form-control fw-bold border-start-0 ps-0" name="tuNgay" value="${requestScope.tuNgay}">
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="input-group">
                                <span class="input-group-text bg-light fw-bold text-muted border-end-0">Đến</span>
                                <input type="date" class="form-control fw-bold border-start-0 ps-0" name="denNgay" value="${requestScope.denNgay}">
                            </div>
                        </div>
                        <div class="col-md-4 d-flex gap-2">
                            <button type="submit" class="btn btn-dark w-100 fw-bold rounded-pill">Tra Cứu Lịch Sử</button>
                        </div>
                    </form>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-custom mb-0 text-center align-middle">
                            <thead class="bg-light">
                            <tr>
                                <th width="15%">Ngày Tạo</th>
                                <th class="text-start">Mã Hóa Đơn</th>
                                <th>Thu Ngân</th>
                                <th class="text-end">Thanh Toán</th>
                                <th>Trạng Thái</th>
                                <th>Bản Sao</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="dh" items="${requestScope.listDonHang}">
                                <tr>
                                    <td class="fw-bold text-muted"><fmt:formatDate value="${dh.thoiGian}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td class="text-start fw-bold text-dark">${dh.maDH}</td>
                                    <td class="fw-bold text-primary">${dh.tenNhanVien}</td>
                                    <td class="text-end fw-bold text-danger fs-6"><fmt:formatNumber value="${dh.tongTien}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                    <td><span class="badge bg-success bg-opacity-10 text-success px-3 py-2 rounded-pill">${dh.trangThai}</span></td>
                                    <td>
                                        <button class="btn btn-light btn-sm text-primary border rounded-pill fw-bold" onclick="viewOrderReceipt('${dh.maDH}')">
                                            <i class="bi bi-printer"></i> In Lại
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <jsp:include page="../layout/pagination.jsp"><jsp:param name="baseUrl" value="/quan-ly-don-hang?tuNgay=${requestScope.tuNgay}&denNgay=${requestScope.denNgay}" /></jsp:include>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Tái sử dụng chính Modal Xem Bill Ảo của trang Dashboard -->
<div class="modal fade" id="orderDetailModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content border-0 bg-transparent shadow-none">
            <div class="modal-body receipt-view" id="receiptContent">
                <div class="text-center mb-3"><h3 class="fw-bold m-0 tracking-wider">TEA POS</h3><small>Bản Sao Kế Toán</small><hr></div>
                <div style="font-size:12px; line-height: 1.6;">
                    <div><b>Mã HD:</b> <span id="r_maDH">Đang tải...</span></div>
                    <div><b>Ngày:</b> <span id="r_ngay"></span></div>
                    <div><b>Thu ngân:</b> <span id="r_nhanVien"></span></div>
                    <div><b>Khách:</b> <span id="r_khach"></span></div>
                </div><hr>
                <table style="width:100%; border-collapse:collapse; font-size:12px;" id="r_items"></table><hr>
                <div style="font-size:12px; line-height: 1.6;">
                    <div class="d-flex justify-content-between"><span>Tổng cộng:</span><b id="r_tongTienHang">0</b></div>
                    <div class="d-flex justify-content-between"><span>Giảm giá/Điểm:</span><span id="r_giamGia">0</span></div><hr>
                    <div class="d-flex justify-content-between fw-bold fs-6 my-2"><span>THANH TOÁN:</span><span id="r_tongPhaiTra">0</span></div>
                </div>
            </div>
            <div class="text-center mt-3 pb-4">
                <button type="button" class="btn btn-light fw-bold px-4 rounded-pill shadow-sm" data-bs-dismiss="modal">Đóng Bản Sao</button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/global.js"></script>
<script>
    // Xử lý AJAX gọi Bill ảo (Tái sử dụng logic từ Dashboard)
    const orderModal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
    const appBasePath = document.getElementById('appContextPath').value;
    const fmtCur = (num) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num);

    function viewOrderReceipt(maDH) {
        orderModal.show();
        document.getElementById('r_maDH').innerText = "Đang tải...";
        fetch(appBasePath + '/quan-ly-don-hang?action=get-receipt&maDH=' + maDH)
            .then(res => res.json())
            .then(data => {
                document.getElementById('r_maDH').innerText = data.maDH;
                document.getElementById('r_nhanVien').innerText = data.nhanVien;
                document.getElementById('r_khach').innerText = data.khachHang || 'Khách vãng lai';
                document.getElementById('r_ngay').innerText = data.ngay;
                let tbody = document.getElementById('r_items'); tbody.innerHTML = '';
                data.items.forEach(item => {
                    tbody.innerHTML += `<tr><td class="fw-bold py-1">\${item.soLuong}x \${item.tenMon}</td><td class="text-end py-1">\${fmtCur(item.giaChot * item.soLuong)}</td></tr>`;
                    if(item.toppings.length > 0) {
                        item.toppings.forEach(tp => {
                            tbody.innerHTML += `<tr><td class="ps-3 text-muted" style="font-size:11px;">+\${tp.sl}x \${tp.ten}</td><td class="text-end text-muted" style="font-size:11px;">\${fmtCur(tp.gia * tp.sl)}</td></tr>`;
                        });
                    }
                });
                document.getElementById('r_tongTienHang').innerText = fmtCur(data.tongTienHang);
                document.getElementById('r_giamGia').innerText = "-" + fmtCur(data.tienGiamGia);
                document.getElementById('r_tongPhaiTra').innerText = fmtCur(data.tongPhaiTra);
            });
    }
</script>
</body>
</html>