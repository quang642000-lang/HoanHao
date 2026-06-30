<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thực Đơn Mua Sắm - TEA POS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root { --brand-primary: #D97706; }
        body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #F8FAFC; padding-top: 70px; }
        .navbar-o2o { background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); }
        .hero-banner {
            background: linear-gradient(135deg, #F59E0B, #B45309);
            color: white; padding: 3rem 1.5rem; border-radius: 20px;
            margin-bottom: 2rem; box-shadow: 0 10px 30px rgba(217, 119, 6, 0.2);
        }
        .category-scroll::-webkit-scrollbar { display: none; }
        .btn-cat { border-radius: 99px; padding: 0.5rem 1.5rem; font-weight: 600; color: #475569; border: 1px solid #E2E8F0; background: white; white-space: nowrap; transition: 0.2s; text-decoration: none;}
        .btn-cat.active { background: var(--brand-primary); color: white; border-color: var(--brand-primary); box-shadow: 0 4px 10px rgba(217, 119, 6, 0.3); }
        .product-card { border: 1px solid #E2E8F0; border-radius: 16px; overflow: hidden; transition: 0.3s; background: white; cursor: pointer; }
        .product-card:hover { transform: translateY(-5px); box-shadow: 0 15px 25px rgba(0,0,0,0.05); border-color: var(--brand-primary); }
        .product-img { width: 100%; height: 180px; object-fit: cover; transition: 0.3s; }
        .product-card:hover .product-img { transform: scale(1.05); }
    </style>
</head>
<body>
<!-- Ẩn đường dẫn context và MaKH để dùng cho việc gọi Ajax API -->
<input type="hidden" id="appContextPath" value="${pageContext.request.contextPath}">
<input type="hidden" id="currentKhId" value="${not empty sessionScope.khachHangDangNhap ? sessionScope.khachHangDangNhap.maKH : ''}">

<nav class="navbar navbar-expand-lg navbar-light shadow-sm fixed-top navbar-o2o border-bottom">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/portal/trang-chu" style="color: var(--brand-primary);">
            <i class="bi bi-cup-straw fs-4 me-1"></i> TEA POS O2O
        </a>
        <div class="d-flex align-items-center gap-2">
            <c:choose>
                <c:when test="${not empty sessionScope.khachHangDangNhap}">
                    <a href="${pageContext.request.contextPath}/portal/ho-so" class="btn btn-light fw-bold rounded-pill border shadow-sm text-dark px-3">
                        <i class="bi bi-person-circle text-primary"></i> ${sessionScope.khachHangDangNhap.tenKH}
                    </a>
                </c:when>
                <c:otherwise>
                    <button class="btn btn-outline-dark fw-bold rounded-pill px-3 shadow-sm" data-bs-toggle="modal" data-bs-target="#loginModal">
                        <i class="bi bi-box-arrow-in-right"></i> Hội Viên
                    </button>
                </c:otherwise>
            </c:choose>
            <a href="${pageContext.request.contextPath}/portal/trang-chu?action=cart" class="btn fw-bold rounded-pill shadow-sm px-3 text-white" style="background-color: var(--brand-primary);">
                <i class="bi bi-bag-check-fill"></i> Giỏ Hàng
            </a>
        </div>
    </div>
</nav>

<div class="container mb-5">
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success shadow-sm rounded-4 fw-bold mt-3 border-0 bg-success text-white">
            <i class="bi bi-check-circle-fill me-2"></i> ${sessionScope.message}
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <div class="hero-banner mt-3 text-center">
        <h1 class="fw-bold display-6 mb-3">Thưởng Thức Trà Sữa Tuyệt Hảo</h1>
        <p class="fs-5 mb-0 fw-medium opacity-75">Click & Collect - Đặt hàng trực tuyến, không chờ đợi!</p>
    </div>

    <!-- Thanh Cuộn Danh Mục -->
    <div class="d-flex category-scroll gap-2 overflow-auto mb-4 pb-2">
        <a href="${pageContext.request.contextPath}/portal/trang-chu" class="btn-cat ${requestScope.selectedDanhMuc == 'all' ? 'active' : ''}">Tất Cả Món</a>
        <c:forEach var="dm" items="${requestScope.danhSachDanhMuc}">
            <a href="${pageContext.request.contextPath}/portal/trang-chu?danhMuc=${dm.maDanhMuc}" class="btn-cat ${requestScope.selectedDanhMuc == dm.maDanhMuc ? 'active' : ''}">${dm.tenDanhMuc}</a>
        </c:forEach>
    </div>

    <!-- Lưới Sản Phẩm -->
    <div class="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3 g-md-4">
        <c:forEach var="sp" items="${requestScope.danhSachSanPham}">
            <c:if test="${sp.trangThai == 1}">
                <div class="col">
                    <div class="product-card h-100 d-flex flex-column shadow-sm" onclick="openO2OModal('${sp.maSP}', '${fn:escapeXml(sp.tenSanPham)}')">
                        <div class="overflow-hidden rounded-top-4">
                            <img src="${pageContext.request.contextPath}/image/${not empty sp.hinhAnh ? sp.hinhAnh : 'default.png'}" class="product-img" onerror="this.src='https://placehold.co/300x300?text=No+Image'">
                        </div>
                        <div class="card-body text-center p-3 d-flex flex-column justify-content-between flex-grow-1">
                            <h6 class="fw-bold text-dark mb-3">${sp.tenSanPham}</h6>
                            <button class="btn btn-outline-warning fw-bold rounded-pill w-100 mt-auto text-dark">
                                <i class="bi bi-plus-lg"></i> Chọn Món
                            </button>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
        <c:if test="${empty requestScope.danhSachSanPham}">
            <div class="col-12 text-center py-5">
                <i class="bi bi-inbox fs-1 text-muted opacity-50"></i>
                <p class="mt-2 text-muted fw-bold">Chưa có món ăn nào trong danh mục này.</p>
            </div>
        </c:if>
    </div>
</div>

<!-- MODAL ĐẶT MÓN O2O (CHỌN SIZE ĐÁ ĐƯỜNG) -->
<div class="modal fade" id="o2oModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <div class="modal-header bg-warning py-3">
                <h5 class="modal-title fw-bold text-dark" id="o2oProductName">Tên Món</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div id="sizeContainer" class="d-flex flex-wrap gap-2 mb-4"></div>
                <div class="row mb-3">
                    <div class="col-6">
                        <label class="fw-bold small text-muted">Đá</label>
                        <select class="form-select fw-bold" id="o2oDa">
                            <option value="100%">100% Đá</option>
                            <option value="50%">50% Đá</option>
                            <option value="0%">0% Đá</option>
                        </select>
                    </div>
                    <div class="col-6">
                        <label class="fw-bold small text-muted">Đường</label>
                        <select class="form-select fw-bold" id="o2oDuong">
                            <option value="100%">100% Đường</option>
                            <option value="50%">50% Đường</option>
                            <option value="0%">0% Đường</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer bg-light p-3">
                <button type="button" class="btn btn-warning w-100 fw-bold rounded-pill py-2 text-dark" onclick="addO2OToCart()">Xác Nhận Đưa Vào Giỏ</button>
            </div>
        </div>
    </div>
</div>

<!-- MODAL HỘI VIÊN ĐA LUỒNG (HYBRID ONBOARDING: POS TO WEB) -->
<div class="modal fade" id="loginModal" tabindex="-1" data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content shadow-lg border-0 rounded-4">
            <div class="modal-header bg-light border-0 py-3">
                <h5 class="modal-title fw-bold text-dark"><i class="bi bi-stars text-primary me-2"></i>TEA POS Member</h5>
                <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal" onclick="resetAuthUI()"></button>
            </div>
            <div class="modal-body p-4" id="authBody">

                <!-- BƯỚC 1: ĐIỀN SỐ ĐIỆN THOẠI ĐỂ HỆ THỐNG PHÂN LUỒNG -->
                <div id="step-phone">
                    <label class="form-label fw-bold small text-muted text-uppercase">Số Điện Thoại</label>
                    <input type="tel" class="form-control form-control-lg fw-bold text-primary mb-3" id="authPhone" placeholder="Nhập SĐT..." pattern="\d{10,11}">
                    <button class="btn btn-primary w-100 fw-bold rounded-pill py-3 shadow-sm" onclick="checkPortalPhone()" id="btnCheckPhone">Tiếp Tục <i class="bi bi-arrow-right ms-1"></i></button>
                </div>

                <!-- BƯỚC 2A: LOGIN BÌNH THƯỜNG (DÀNH CHO KHÁCH CŨ ĐÃ CÓ MẬT KHẨU) -->
                <div id="step-login" style="display: none;">
                    <p class="small text-muted fw-bold mb-3">Tài khoản: <span id="lblLoginPhone" class="text-primary fs-6"></span> <a href="javascript:resetAuthUI()" class="text-danger ms-2 text-decoration-none">Đổi</a></p>
                    <label class="form-label fw-bold small text-muted text-uppercase">Mật Khẩu Web</label>
                    <input type="password" class="form-control form-control-lg fw-bold mb-3 border-primary" id="authPass" placeholder="Nhập mật khẩu...">
                    <button class="btn btn-success w-100 fw-bold rounded-pill py-3 shadow-sm" onclick="portalLogin()" id="btnLogin">Đăng Nhập <i class="bi bi-box-arrow-in-right ms-1"></i></button>
                </div>

                <!-- BƯỚC 2B: XÁC MINH OTP (DÀNH CHO KHÁCH ĐƯỢC TẠO THẺ Ở MÁY POS) -->
                <div id="step-otp" style="display: none;">
                    <div class="alert alert-warning border-0 small mb-3">
                        <i class="bi bi-info-circle-fill"></i> Thẻ của bạn được mở tại quầy. Hệ thống đã gửi mã OTP kích hoạt Web đến Email:<br>
                        <strong class="text-danger fs-6" id="lblOtpEmail"></strong>
                    </div>
                    <input type="text" class="form-control form-control-lg fw-bold text-center mb-2 tracking-widest text-primary" id="authOtp" placeholder="OTP 6 Số" maxlength="6">
                    <input type="password" class="form-control form-control-lg fw-bold mb-3" id="authNewPass" placeholder="Tạo mật khẩu Web...">
                    <button class="btn btn-warning w-100 fw-bold rounded-pill py-3 shadow-sm text-dark" onclick="verifyAndSetPass()" id="btnOtp">Kích Hoạt Tài Khoản</button>
                    <div class="text-center mt-3"><a href="javascript:resetAuthUI()" class="text-muted small text-decoration-none"><i class="bi bi-arrow-left"></i> Quay lại</a></div>
                </div>

                <!-- BƯỚC 2C: ĐĂNG KÝ (DÀNH CHO KHÁCH MỚI HOÀN TOÀN) -->
                <div id="step-register" style="display: none;">
                    <p class="small text-muted fw-bold mb-3">SĐT <span id="lblRegPhone" class="text-primary"></span> chưa đăng ký. Hãy mở thẻ mới:</p>
                    <input type="text" class="form-control mb-2 fw-medium" id="regName" placeholder="Họ và Tên bạn">
                    <input type="email" class="form-control mb-2 fw-medium" id="regEmail" placeholder="Địa chỉ Email (Nhận Voucher)">
                    <input type="password" class="form-control mb-3 fw-medium" id="regPass" placeholder="Tạo mật khẩu Web">
                    <button class="btn btn-dark w-100 fw-bold rounded-pill py-3 shadow-sm" onclick="portalRegister()" id="btnReg">Mở Thẻ Thành Viên</button>
                    <div class="text-center mt-3"><a href="javascript:resetAuthUI()" class="text-muted small text-decoration-none"><i class="bi bi-arrow-left"></i> Đổi số khác</a></div>
                </div>

            </div>
        </div>
    </div>
</div>

<!-- DỮ LIỆU ẨN CHO TẦNG JAVASCRIPT ĐỌC TRONG QUÁ TRÌNH TẠO HÓA ĐƠN O2O -->
<div id="hidden-data" style="display:none;">
    <c:forEach var="bt" items="${requestScope.danhSachBienThe}">
        <div class="variant-data" data-masp="${bt.sanPham.maSP}" data-mabt="${bt.maBienThe}" data-size="${fn:escapeXml(bt.kichCo)}" data-price="${bt.giaBan}"></div>
    </c:forEach>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- SCRIPT XỬ LÝ TOÀN BỘ NGHIỆP VỤ O2O & XÁC THỰC -->
<script>
    const appContext = document.getElementById('appContextPath').value;

    // ----------------------------------------------------
    // LOGIC CHỌN MÓN VÀ GỬI VÀO GIỎ HÀNG THÔNG MINH O2O
    // ----------------------------------------------------
    let allVariants = [];
    document.querySelectorAll('.variant-data').forEach(item => {
        allVariants.push({
            maSP: item.dataset.masp,
            maBT: item.dataset.mabt,
            size: item.dataset.size,
            price: parseInt(item.dataset.price)
        });
    });

    const o2oModal = new bootstrap.Modal(document.getElementById('o2oModal'));

    function openO2OModal(maSP, tenSP) {
        let maKH = document.getElementById('currentKhId').value;
        // Nếu chưa đăng nhập, tự động kích hoạt Modal Hội viên
        if(!maKH) {
            alert('Vui lòng đăng nhập thẻ Hội Viên để thêm món!');
            new bootstrap.Modal(document.getElementById('loginModal')).show();
            return;
        }

        document.getElementById('o2oProductName').innerText = tenSP;
        let variants = allVariants.filter(v => v.maSP === maSP);
        if(variants.length === 0) {
            alert('Món này tạm thời chưa được cấu hình Kích Cỡ, không thể bán!');
            return;
        }

        // Vẽ danh sách Size động cho sản phẩm
        let sizeHtml = '';
        variants.forEach((v, index) => {
            let checked = index === 0 ? "checked" : "";
            // Dùng dấu gạch chéo ngược \ trước các EL JSP để JavaScript hiểu đây là biến của JS
            sizeHtml += `<input type='radio' class='btn-check' name='o2oSize' id='s_\${v.maBT}' value='\${v.maBT}' \${checked}>
                             <label class='btn btn-outline-warning text-dark fw-bold rounded-3 px-3 py-2' for='s_\${v.maBT}'>Size \${v.size} - \${v.price}đ</label>`;
        });
        document.getElementById('sizeContainer').innerHTML = sizeHtml;
        o2oModal.show();
    }

    function addO2OToCart() {
        let maKH = document.getElementById('currentKhId').value;
        let maBT = document.querySelector('input[name="o2oSize"]:checked').value;
        let daDuong = document.getElementById('o2oDa').value + " Đá, " + document.getElementById('o2oDuong').value + " Đường";

        // Xây dựng JSON đẩy về API (Topping O2O sẽ được phát triển tiếp ở module sau)
        let payload = {
            maKH: maKH,
            maBT: maBT,
            soLuong: 1,
            mucDaDuong: daDuong,
            toppingsJson: "[]"
        };

        fetch(appContext + '/api/portal/cart', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
            .then(res => res.json())
            .then(data => {
                if(data.status === 'success') {
                    alert('Đã đưa món vào Giỏ Hàng của bạn!');
                    o2oModal.hide();
                } else {
                    alert(data.message);
                }
            });
    }

    // ----------------------------------------------------
    // LOGIC AUTHENTICATION (XÁC THỰC HYBRID ONBOARDING)
    // ----------------------------------------------------
    const authApiUrl = appContext + '/api/portal/auth';

    function resetAuthUI() {
        ['step-phone', 'step-login', 'step-otp', 'step-register'].forEach(id => {
            document.getElementById(id).style.display = 'none';
        });
        document.getElementById('step-phone').style.display = 'block';
        document.getElementById('authPass').value = '';
        document.getElementById('authOtp').value = '';
        document.getElementById('authNewPass').value = '';
    }

    // Bước 1: Gửi SĐT về Backend để Backend quyết định luồng đi
    function checkPortalPhone() {
        let phone = document.getElementById('authPhone').value;
        if(phone.length < 10) { alert('Số điện thoại không hợp lệ!'); return; }

        let btn = document.getElementById('btnCheckPhone');
        btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang xử lý...';
        btn.disabled = true;

        fetch(authApiUrl, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'action=check-phone&phone=' + phone
        }).then(res => res.json()).then(data => {
            btn.innerHTML = 'Tiếp Tục <i class="bi bi-arrow-right ms-1"></i>';
            btn.disabled = false;

            if(data.status === 'has_pass') {
                // Khách đã có Password -> Chuyển sang form Login thường
                document.getElementById('step-phone').style.display = 'none';
                document.getElementById('step-login').style.display = 'block';
                document.getElementById('lblLoginPhone').innerText = phone;
            } else if(data.status === 'no_pass') {
                // Khách mở thẻ ở POS nhưng chưa có Pass -> Chuyển sang OTP Kích hoạt
                document.getElementById('step-phone').style.display = 'none';
                document.getElementById('step-otp').style.display = 'block';
                document.getElementById('lblOtpEmail').innerText = data.email;
            } else if(data.status === 'not_found') {
                // Khách mới toanh -> Chuyển sang form Đăng Ký
                document.getElementById('step-phone').style.display = 'none';
                document.getElementById('step-register').style.display = 'block';
                document.getElementById('lblRegPhone').innerText = phone;
            } else {
                alert(data.message);
            }
        });
    }

    // Chạy Luồng 1: Đăng nhập
    function portalLogin() {
        let phone = document.getElementById('authPhone').value;
        let pass = document.getElementById('authPass').value;
        if(!pass) { alert('Vui lòng nhập mật khẩu!'); return; }

        fetch(authApiUrl, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `action=login&phone=\${phone}&password=\${pass}`
        }).then(res => res.json()).then(data => {
            if(data.status === 'success') window.location.reload();
            else alert(data.message);
        });
    }

    // Chạy Luồng 2: Xác thực OTP để thiết lập Pass lần đầu
    function verifyAndSetPass() {
        let otp = document.getElementById('authOtp').value;
        let pass = document.getElementById('authNewPass').value;
        if(otp.length !== 6 || !pass) { alert('Vui lòng điền đủ mã OTP 6 số và Mật khẩu bạn muốn tạo!'); return; }

        fetch(authApiUrl, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `action=verify-set-pass&otp=\${otp}&password=\${pass}`
        }).then(res => res.json()).then(data => {
            if(data.status === 'success') window.location.reload();
            else alert(data.message);
        });
    }

    // Chạy Luồng 3: Đăng ký tạo thẻ hoàn toàn mới
    function portalRegister() {
        let phone = document.getElementById('authPhone').value;
        let name = document.getElementById('regName').value;
        let email = document.getElementById('regEmail').value;
        let pass = document.getElementById('regPass').value;
        if(!name || !email || !pass) { alert('Vui lòng điền đầy đủ thông tin để khởi tạo thẻ!'); return; }

        fetch(authApiUrl, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `action=register&phone=\${phone}&name=\${name}&email=\${email}&password=\${pass}`
        }).then(res => res.json()).then(data => {
            if(data.status === 'success') window.location.reload();
            else alert(data.message);
        });
    }
</script>
</body>
</html>