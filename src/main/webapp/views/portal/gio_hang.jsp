<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Giỏ Hàng Của Bạn - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F1F5F9; padding-bottom: 90px;}
        .cart-card { border-radius: 16px; border: none; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); background: white; transition: 0.2s;}
        .cart-card:hover { border-color: #D97706; box-shadow: 0 10px 15px rgba(217,119,6,0.1); }
        .product-img { width: 80px; height: 80px; object-fit: cover; border-radius: 12px; border: 1px solid #E2E8F0; }
        .form-check-input { width: 1.5em; height: 1.5em; cursor: pointer; }
        .form-check-input:checked { background-color: #D97706; border-color: #D97706; }
        .sticky-bottom-bar {
            position: fixed; bottom: 0; left: 0; width: 100%;
            background: white; border-top: 1px solid #E2E8F0;
            padding: 15px 0; z-index: 1000;
            box-shadow: 0 -4px 20px rgba(0,0,0,0.08);
        }
    </style>
</head>
<body>
<input type="hidden" id="appContextPath" value="${pageContext.request.contextPath}">

<nav class="navbar navbar-light bg-white shadow-sm sticky-top py-3">
    <div class="container d-flex justify-content-between align-items-center">
        <a href="${pageContext.request.contextPath}/portal/trang-chu" class="text-decoration-none fw-bold text-muted"><i class="bi bi-arrow-left"></i> Tiếp tục chọn món</a>
        <h5 class="mb-0 fw-bold text-dark">Giỏ Hàng</h5>
    </div>
</nav>

<div class="container mt-4">
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-danger shadow-sm rounded-4 fw-bold mb-4 border-0">
            <i class="bi bi-exclamation-triangle-fill me-2"></i> ${sessionScope.message}
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${empty requestScope.myCart or empty requestScope.myCart.danhSachChiTiet}">
            <div class="text-center py-5 mt-5 bg-white rounded-4 shadow-sm border">
                <i class="bi bi-cart-x text-muted opacity-50" style="font-size: 6rem;"></i>
                <h4 class="fw-bold mt-3 text-dark">Giỏ hàng đang trống</h4>
                <a href="${pageContext.request.contextPath}/portal/trang-chu" class="btn btn-warning fw-bold mt-3 rounded-pill px-5 shadow-sm text-dark fs-5">MUA NƯỚC NGAY</a>
            </div>
        </c:when>
        <c:otherwise>
            <div id="step-cart">
                <div class="d-flex justify-content-between align-items-center mb-3 px-2">
                    <h6 class="fw-bold text-dark mb-0">Các món đã thêm</h6>
                </div>

                <div class="row">
                    <div class="col-lg-8 mx-auto">
                        <c:forEach var="item" items="${requestScope.myCart.danhSachChiTiet}">
                            <div class="cart-card p-3 mb-3 d-flex align-items-start gap-3">
                                <div class="d-flex align-items-center h-100 mt-4">
                                    <input class="form-check-input item-checkbox" type="checkbox" data-id="${item.maCTGH}" onchange="toggleItemCheck('${item.maCTGH}', this.checked)" ${item.chonMua ? 'checked' : ''}>
                                </div>

                                <img src="${pageContext.request.contextPath}/image/${not empty item.bienThe.sanPham.hinhAnh ? item.bienThe.sanPham.hinhAnh : 'default.png'}" class="product-img" onerror="this.src='https://placehold.co/100x100?text=No+Image'">

                                <div class="flex-grow-1">
                                    <div class="d-flex justify-content-between">
                                        <h6 class="fw-bold text-dark mb-1" style="font-size: 1.1rem;">${item.bienThe.sanPham.tenSanPham}</h6>
                                        <button class="btn btn-sm btn-link text-danger p-0 shadow-none" onclick="updateCartAction('${item.maCTGH}', 'delete')"><i class="bi bi-trash fs-5"></i></button>
                                    </div>

                                    <div class="small text-muted fw-medium mb-1">
                                        Size ${item.bienThe.kichCo} &bull; ${item.mucDaDuong}
                                        <a href="javascript:void(0)" class="text-primary ms-2 fw-bold text-decoration-none" onclick="openEditModal('${item.maCTGH}', '${item.bienThe.sanPham.maSP}', '${fn:escapeXml(item.bienThe.sanPham.tenSanPham)}', '${item.bienThe.maBienThe}', '${item.mucDaDuong}', '${fn:escapeXml(item.toppingsJson)}')"><i class="bi bi-pencil-square"></i> Đổi</a>
                                    </div>

                                    <div id="tp_render_${item.maCTGH}" class="small text-primary fw-semibold mb-2"></div>

                                    <script>
                                        document.addEventListener("DOMContentLoaded", function() {
                                            let tpStr = `${fn:escapeXml(item.toppingsJson)}`.replace(/&quot;/g, '"');
                                            let basePrice = ${item.bienThe.giaBan};
                                            let totalTp = 0;
                                            if(tpStr && tpStr !== '[]') {
                                                try {
                                                    let tpList = JSON.parse(tpStr);
                                                    let html = '';
                                                    tpList.forEach(t => {
                                                        html += `<div class="text-muted">+ \${t.qty}x \${t.name} (\${new Intl.NumberFormat('vi-VN').format(t.price)}đ)</div>`;
                                                        totalTp += (t.price * t.qty);
                                                    });
                                                    document.getElementById('tp_render_${item.maCTGH}').innerHTML = html;
                                                } catch(e){}
                                            }
                                            document.getElementById('tp_render_${item.maCTGH}').setAttribute('data-unit-price', basePrice + totalTp);
                                        });
                                    </script>

                                    <div class="d-flex justify-content-between align-items-center mt-2">
                                        <h6 class="fw-bold text-danger mb-0" id="price_display_${item.maCTGH}">Đang tính...</h6>
                                        <div class="input-group input-group-sm w-auto shadow-sm rounded-3">
                                            <button class="btn btn-light border fw-bold px-3" onclick="updateCartAction('${item.maCTGH}', 'minus')">-</button>
                                            <input type="text" class="form-control text-center fw-bold bg-white item-qty" data-id="${item.maCTGH}" style="max-width: 45px;" value="${item.soLuong}" readonly>
                                            <button class="btn btn-light border fw-bold px-3 text-warning" onclick="updateCartAction('${item.maCTGH}', 'plus')">+</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="sticky-bottom-bar">
                    <div class="container d-flex justify-content-between align-items-center">
                        <div class="d-flex align-items-center ps-2">
                            <h6 class="mb-0 fw-medium text-muted me-3 d-none d-sm-block">Tổng tiền tạm tính:</h6>
                            <h4 class="mb-0 fw-bold text-danger" id="bottomTotalAmount">0đ</h4>
                        </div>
                        <button class="btn btn-warning fw-bold px-4 px-md-5 py-2 rounded-pill fs-5 shadow-sm" onclick="goToCheckoutStep()">Mua Hàng</button>
                    </div>
                </div>
            </div>

            <!-- BƯỚC 2: XÁC NHẬN THANH TOÁN (TÓM TẮT ĐƠN HÀNG) -->
            <div id="step-checkout" style="display: none; padding-bottom: 50px;">
                <form action="${pageContext.request.contextPath}/portal/trang-chu" method="post" id="checkoutO2OForm">
                    <input type="hidden" name="action" value="checkout">
                    <!-- MÃ KHUYẾN MÃI ẨN -->
                    <input type="hidden" name="maKM" id="input_maKM">

                    <div class="row justify-content-center">
                        <div class="col-lg-6">
                            <div class="d-flex align-items-center mb-3">
                                <button type="button" class="btn btn-light rounded-circle shadow-sm border me-3" onclick="backToCart()"><i class="bi bi-arrow-left"></i></button>
                                <h5 class="fw-bold mb-0">Xác Nhận Đơn Hàng</h5>
                            </div>

                            <div class="cart-card p-4 mb-4 border border-warning shadow-sm">
                                <h6 class="fw-bold text-dark border-bottom pb-2 mb-3"><i class="bi bi-receipt text-primary me-2"></i>Chi Tiết Thanh Toán</h6>

                                <div class="d-flex justify-content-between mb-2 small">
                                    <span class="text-muted fw-semibold">Tổng tiền món đã chọn:</span>
                                    <span class="fw-bold text-dark" id="summary_tongTienHang">0đ</span>
                                </div>

                                <!-- KHU VỰC NHẬP MÃ GIẢM GIÁ (SHOPEE STYLE) -->
                                <div class="bg-light p-3 rounded-3 mt-3 mb-3 border">
                                    <label class="fw-bold small text-muted mb-2 d-block"><i class="bi bi-ticket-perforated"></i> Thêm Mã Giảm Giá</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control text-uppercase fw-bold border-dark" id="inputVoucherCode" placeholder="Nhập mã KM...">
                                        <button class="btn btn-dark fw-bold px-3" type="button" onclick="applyVoucher()">Áp Dụng</button>
                                    </div>
                                    <div id="activeVoucherInfo" class="mt-2 pt-2 border-top text-success small fw-bold" style="display: none;">
                                        <i class="bi bi-check-circle-fill"></i> Đã áp mã: <span id="voucherLabel"></span>
                                        <a href="javascript:void(0)" class="text-danger ms-2 text-decoration-none" onclick="removeVoucher()"><i class="bi bi-x"></i> Gỡ Bỏ</a>
                                    </div>
                                </div>

                                <div class="d-flex justify-content-between mb-2 small text-success">
                                    <span class="fw-semibold">Giảm giá:</span>
                                    <span class="fw-bold" id="summary_tienGiamGia">- 0đ</span>
                                </div>
                                <hr class="border-secondary opacity-25">
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="fw-bold text-dark fs-6">TỔNG CẦN THANH TOÁN:</span>
                                    <span class="fw-bold text-danger fs-3" id="summary_tongPhaiTra">0đ</span>
                                </div>
                            </div>

                            <div class="cart-card p-4 mb-4 shadow-sm">
                                <h6 class="fw-bold text-dark mb-3"><i class="bi bi-clock-history text-warning me-2"></i>Tùy Chọn Lấy Nước</h6>
                                <div class="alert alert-info py-2 px-3 small fw-medium border-0 mb-3"><i class="bi bi-info-circle-fill"></i> Quán cần ít nhất 15 phút để chuẩn bị.</div>
                                <input type="time" name="gioHenLay" id="gioHenLay" class="form-control form-control-lg fw-bold text-primary bg-light" required>
                            </div>

                            <button type="button" class="btn btn-success w-100 py-3 fs-5 fw-bold rounded-pill shadow-lg mt-2" onclick="showQRModal()"><i class="bi bi-qr-code-scan me-2"></i> TẠO MÃ QR THANH TOÁN</button>
                        </div>
                    </div>
                </form>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- MODAL CHỈNH SỬA MÓN -->
<div class="modal fade" id="o2oEditModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <div class="modal-header bg-warning py-3">
                <h5 class="modal-title fw-bold text-dark" id="editProductName">Sửa Món</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <input type="hidden" id="editCartId">
                <div id="editSizeContainer" class="d-flex flex-wrap gap-2 mb-4"></div>
                <div class="row mb-3">
                    <div class="col-6">
                        <label class="fw-bold small text-muted">Đá</label>
                        <select class="form-select fw-bold" id="editDa">
                            <option value="100%">100% Đá</option><option value="50%">50% Đá</option><option value="0%">0% Đá</option>
                        </select>
                    </div>
                    <div class="col-6">
                        <label class="fw-bold small text-muted">Đường</label>
                        <select class="form-select fw-bold" id="editDuong">
                            <option value="100%">100% Đường</option><option value="50%">50% Đường</option><option value="0%">0% Đường</option>
                        </select>
                    </div>
                </div>
                <label class="fw-bold small text-muted mb-2 mt-2">Topping Thêm</label>
                <div class="bg-light rounded-3 p-2 border" style="max-height: 200px; overflow-y: auto;">
                    <c:forEach var="tp" items="${requestScope.danhSachTopping}">
                        <c:if test="${tp.trangThai == 1}">
                            <div class="d-flex justify-content-between align-items-center p-2 mb-1 bg-white rounded shadow-sm border">
                                <div class="fw-bold text-dark small">${tp.tenTopping} <br><span class="text-danger">+${tp.giaBan}đ</span></div>
                                <div class="input-group input-group-sm" style="width: 90px;">
                                    <button class="btn btn-outline-secondary" type="button" onclick="changeEditTpQty('${tp.maTopping}', -1)">-</button>
                                    <input type="text" class="form-control text-center fw-bold bg-white"
                                           id="edit_tp_qty_${tp.maTopping}" value="0" readonly
                                           data-id="${tp.maTopping}" data-name="${fn:escapeXml(tp.tenTopping)}" data-price="${tp.giaBan}">
                                    <button class="btn btn-outline-secondary" type="button" onclick="changeEditTpQty('${tp.maTopping}', 1)">+</button>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
            <div class="modal-footer bg-light p-3">
                <button type="button" class="btn btn-warning w-100 fw-bold rounded-pill py-3 shadow-sm text-dark fs-5" onclick="saveEditedItem()">Cập Nhật Thay Đổi</button>
            </div>
        </div>
    </div>
</div>

<!-- MODAL QUÉT QR SEPAY -->
<div class="modal fade" id="qrO2OModal" tabindex="-1" data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content shadow-lg border-0 rounded-4">
            <div class="modal-header text-white border-0 py-3 rounded-top-4" style="background-color: #D97706;">
                <h6 class="modal-title fw-bold m-0"><i class="bi bi-qr-code-scan me-2"></i> Thanh Toán O2O</h6>
            </div>
            <div class="modal-body text-center bg-white p-4">
                <h3 class="text-danger fw-bold mb-1" id="qrAmount">Tổng Tiền...</h3>
                <p class="text-muted small fw-medium mb-3">Mã GD: <span class="fw-bold text-dark" id="qrCodeDisplay"></span></p>
                <div class="bg-light p-3 rounded-4 d-inline-block mb-3 border">
                    <img id="qrImage" src="" alt="QR" style="width: 200px; height: 200px; object-fit: contain;">
                </div>
                <div class="text-primary fw-bold small mb-2 d-flex align-items-center justify-content-center">
                    <div class="spinner-border spinner-border-sm me-2" role="status"></div>
                    <span>Hệ thống đang chờ tiền vào...</span>
                </div>
            </div>
            <div class="modal-footer border-0 p-3 bg-light rounded-bottom-4">
                <button type="button" class="btn btn-outline-danger w-100 fw-bold rounded-pill" onclick="cancelPayment()">Hủy Giao Dịch</button>
                <button type="button" class="btn btn-link text-muted small w-100 mt-2" onclick="forceSubmit()">[Test] Bỏ qua & Hoàn tất</button>
            </div>
        </div>
    </div>
</div>

<!-- DATA ẨN CHO TẦNG JS -->
<div id="hidden-data" style="display:none;">
    <c:forEach var="bt" items="${requestScope.danhSachBienThe}">
        <div class="variant-data" data-masp="${bt.sanPham.maSP}" data-mabt="${bt.maBienThe}" data-size="${fn:escapeXml(bt.kichCo)}" data-price="${bt.giaBan}"></div>
    </c:forEach>
</div>

<!-- DATA VOUCHER LẤY TỪ CONTROLLER -->
<div id="hidden-vouchers-data" style="display: none;">
    <c:forEach var="km" items="${requestScope.danhSachKhuyenMai}">
        <c:if test="${km.trangThai == 1 && (km.soLuong - km.soLuongDaDung > 0)}">
            <div class="voucher-item-data" data-id="${km.maKM}" data-code="${km.maCode}" data-loai="${km.loaiGiamGia}" data-giatri="${km.giaTriGiam}" data-min="${km.dieuKienToiThieu}" data-start="${km.ngayBatDau.time}" data-end="${km.ngayKetThuc.time}"></div>
        </c:if>
    </c:forEach>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const appContext = document.getElementById('appContextPath').value;
    const apiCartUrl = appContext + '/api/portal/cart';

    let allVariants = [];
    document.querySelectorAll('.variant-data').forEach(item => {
        allVariants.push({ maSP: item.dataset.masp, maBT: item.dataset.mabt, size: item.dataset.size, price: parseInt(item.dataset.price) });
    });

    // Tải danh sách Voucher vào JS
    let availableVouchers = [];
    document.querySelectorAll('.voucher-item-data').forEach(function(item) {
        availableVouchers.push({
            id: item.getAttribute('data-id'), code: item.getAttribute('data-code').toUpperCase(),
            loai: item.getAttribute('data-loai'), giaTri: parseFloat(item.getAttribute('data-giatri')),
            min: parseFloat(item.getAttribute('data-min')), start: parseInt(item.getAttribute('data-start')), end: parseInt(item.getAttribute('data-end'))
        });
    });

    // CÁC BIẾN QUẢN LÝ TIỀN BẠC (SHOPEE LOGIC)
    let checkoutTotal = 0; // Giá trị hàng đã Checkbox
    let currentVoucher = null;
    let finalPayAmount = 0; // Giá trị cần chuyển khoản sau cùng

    // 1. TÍNH TOÁN GIÁ TIỀN & SHOPEE CHECKBOX LOGIC
    function calculateTotal() {
        let total = 0;
        let checkedCount = 0;

        document.querySelectorAll('.item-checkbox').forEach(chk => {
            let id = chk.getAttribute('data-id');
            let tpRenderDiv = document.getElementById('tp_render_' + id);
            if(tpRenderDiv) {
                let unitPrice = parseInt(tpRenderDiv.getAttribute('data-unit-price') || 0);
                let qty = parseInt(document.querySelector(`.item-qty[data-id="\${id}"]`).value || 1);

                document.getElementById('price_display_' + id).innerText = new Intl.NumberFormat('vi-VN').format(unitPrice * qty) + 'đ';

                if (chk.checked) {
                    total += (unitPrice * qty);
                    checkedCount++;
                }
            }
        });
        document.getElementById('bottomTotalAmount').innerText = new Intl.NumberFormat('vi-VN').format(total) + 'đ';
        checkoutTotal = total;
        return { total, checkedCount };
    }

    setTimeout(calculateTotal, 300);

    function toggleItemCheck(maCTGH, isChecked) {
        fetch(apiCartUrl, {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: "toggle_check", maCTGH: maCTGH, isChecked: isChecked })
        }).then(() => calculateTotal());
    }

    function updateCartAction(maCTGH, type) {
        let payload = null;
        if (type === 'delete') {
            if(!confirm('Xóa món này khỏi giỏ?')) return;
            payload = { action: "delete", maCTGH: maCTGH };
        } else {
            let change = type === 'plus' ? 1 : -1;
            payload = { action: "update_qty", maCTGH: maCTGH, change: change };
        }

        fetch(apiCartUrl, {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        }).then(() => window.location.reload());
    }

    // 2. CHUYỂN BƯỚC CHECKOUT VÀ HIỂN THỊ TÓM TẮT
    function goToCheckoutStep() {
        let status = calculateTotal();
        if(status.checkedCount === 0) {
            alert('Vui lòng chọn ít nhất 1 món để thanh toán!'); return;
        }

        // Render Tóm Tắt Tiền Hàng
        document.getElementById('summary_tongTienHang').innerText = new Intl.NumberFormat('vi-VN').format(checkoutTotal) + 'đ';
        updateFinalSummary(); // Gọi tính toán để hiện thị tổng cuối

        // Khởi tạo giờ hẹn lấy
        let now = new Date(); now.setMinutes(now.getMinutes() + 15);
        let minTime = String(now.getHours()).padStart(2, '0') + ':' + String(now.getMinutes()).padStart(2, '0');
        document.getElementById('gioHenLay').min = minTime;
        document.getElementById('gioHenLay').value = minTime;

        document.getElementById('step-cart').style.display = 'none';
        document.getElementById('step-checkout').style.display = 'block';
        document.body.style.paddingBottom = '0';
    }

    function backToCart() {
        document.getElementById('step-checkout').style.display = 'none';
        document.getElementById('step-cart').style.display = 'block';
        document.body.style.paddingBottom = '90px';
    }

    // --- VOUCHER LOGIC (SHOPEE STYLE) ---
    function applyVoucher() {
        let codeInput = document.getElementById('inputVoucherCode').value.trim().toUpperCase();
        if (codeInput === '') { alert("Vui lòng nhập mã giảm giá!"); return; }

        let found = availableVouchers.find(v => v.code === codeInput);
        if (found) {
            let now = new Date().getTime();
            if (now < found.start) { alert("Mã giảm giá này chưa đến ngày sử dụng!"); return; }
            if (now > (found.end + 86399000)) { alert("Mã giảm giá này đã hết hạn!"); return; }

            if (checkoutTotal >= found.min) {
                currentVoucher = found;
                document.getElementById('activeVoucherInfo').style.display = 'block';
                document.getElementById('voucherLabel').innerText = found.code;
                document.getElementById('inputVoucherCode').value = '';
                document.getElementById('input_maKM').value = found.id; // Gắn vào form
                updateFinalSummary();
            } else {
                alert('Đơn hàng chưa đạt mức tối thiểu ' + new Intl.NumberFormat('vi-VN').format(found.min) + 'đ để dùng mã này!');
            }
        } else {
            alert("Mã không hợp lệ hoặc đã hết lượt dùng!");
        }
    }

    function removeVoucher() {
        currentVoucher = null;
        document.getElementById('input_maKM').value = '';
        document.getElementById('activeVoucherInfo').style.display = 'none';
        updateFinalSummary();
    }

    function updateFinalSummary() {
        let discount = 0;
        if (currentVoucher) {
            discount = currentVoucher.loai === 'Phần Trăm' ? (checkoutTotal * currentVoucher.giaTri) / 100 : currentVoucher.giaTri;
            if(discount > checkoutTotal) discount = checkoutTotal;
        }
        finalPayAmount = checkoutTotal - discount;

        document.getElementById('summary_tienGiamGia').innerText = "-" + new Intl.NumberFormat('vi-VN').format(discount) + 'đ';
        document.getElementById('summary_tongPhaiTra').innerText = new Intl.NumberFormat('vi-VN').format(finalPayAmount) + 'đ';
    }

    // 3. EDIT MODAL (GIỮ NGUYÊN)
    const editModal = new bootstrap.Modal(document.getElementById('o2oEditModal'));
    function openEditModal(maCTGH, maSP, tenSP, maBTHienTai, mucDaDuong, toppingsJsonStr) {
        document.getElementById('editCartId').value = maCTGH;
        document.getElementById('editProductName').innerText = tenSP;

        let variants = allVariants.filter(v => v.maSP === maSP);
        let sizeHtml = '';
        variants.forEach(v => {
            let checked = (v.maBT === maBTHienTai) ? "checked" : "";
            sizeHtml += `<input type='radio' class='btn-check' name='editSize' id='e_s_\${v.maBT}' value='\${v.maBT}' \${checked}>
                             <label class='btn btn-outline-warning text-dark fw-bold rounded-3 px-3 py-2' for='e_s_\${v.maBT}'>Size \${v.size} - \${v.price}đ</label>`;
        });
        document.getElementById('editSizeContainer').innerHTML = sizeHtml;

        if(mucDaDuong.includes(",")) {
            let parts = mucDaDuong.split(",");
            document.getElementById('editDa').value = parts.replace(" Đá", "").trim() + "%";
            document.getElementById('editDuong').value = parts.replace(" Đường", "").trim() + "%";
        }

        document.querySelectorAll('input[id^="edit_tp_qty_"]').forEach(inp => inp.value = 0);
        if(toppingsJsonStr && toppingsJsonStr !== '[]') {
            try {
                let tps = JSON.parse(toppingsJsonStr.replace(/&quot;/g, '"'));
                tps.forEach(t => {
                    let inp = document.getElementById('edit_tp_qty_' + t.id);
                    if(inp) inp.value = t.qty;
                });
            } catch(e){}
        }
        editModal.show();
    }

    function changeEditTpQty(id, amount) {
        let input = document.getElementById('edit_tp_qty_' + id);
        let val = parseInt(input.value) + amount;
        if(val >= 0) input.value = val;
    }

    function saveEditedItem() {
        let maCTGH = document.getElementById('editCartId').value;
        let maBT = document.querySelector('input[name="editSize"]:checked').value;
        let daDuong = document.getElementById('editDa').value + " Đá, " + document.getElementById('editDuong').value + " Đường";

        let toppings = [];
        document.querySelectorAll('input[id^="edit_tp_qty_"]').forEach(inp => {
            let qty = parseInt(inp.value);
            if (qty > 0) {
                toppings.push({ id: inp.getAttribute('data-id'), name: inp.getAttribute('data-name'), price: parseInt(inp.getAttribute('data-price')), qty: qty });
            }
        });

        fetch(apiCartUrl, {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: "update_full_item", maCTGH: maCTGH, maBT: maBT, mucDaDuong: daDuong, toppingsJson: JSON.stringify(toppings) })
        }).then(() => window.location.reload());
    }

    // 4. THANH TOÁN QR SEPAY (LẤY TIỀN ĐÃ TRỪ VOUCHER)
    let checkPaymentInterval = null; let isPaymentActive = false;
    const qrModal = new bootstrap.Modal(document.getElementById('qrO2OModal'));

    function showQRModal() {
        let minTime = document.getElementById('gioHenLay').min;
        if(document.getElementById('gioHenLay').value < minTime) {
            alert('Vui lòng chọn giờ hẹn lấy nước sau ' + minTime + ' để quán kịp chuẩn bị!'); return;
        }

        // FIX: Sử dụng finalPayAmount (đã áp mã giảm giá) thay vì total gốc
        if (finalPayAmount <= 0) {
            alert("Đơn hàng 0đ, hệ thống sẽ tự động chốt đơn mà không cần quét QR!");
            forceSubmit();
            return;
        }

        let transactionCode = "TEA" + new Date().getFullYear().toString().slice(-2) + String(new Date().getMonth() + 1).padStart(2, '0') + String(new Date().getDate()).padStart(2, '0') + Math.floor(1000 + Math.random() * 9000);

        document.getElementById('qrAmount').innerText = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(finalPayAmount);
        document.getElementById('qrCodeDisplay').innerText = transactionCode;
        document.getElementById('qrImage').src = `https://img.vietqr.io/image/TPB-0346406405-compact2.png?amount=\${finalPayAmount}&addInfo=\${transactionCode}`;

        isPaymentActive = true; qrModal.show();
        checkPaymentInterval = setInterval(function() {
            if (!isPaymentActive) { clearInterval(checkPaymentInterval); return; }
            fetch(appContext + '/api/check-payment?code=' + transactionCode).then(res => res.json()).then(data => {
                if (data.status === 'success') forceSubmit();
            });
        }, 3000);
    }

    function cancelPayment() { isPaymentActive = false; clearInterval(checkPaymentInterval); qrModal.hide(); }
    function forceSubmit() { isPaymentActive = false; clearInterval(checkPaymentInterval); qrModal.hide(); document.getElementById('checkoutO2OForm').submit(); }
</script>
</body>
</html>