<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác Thực OTP</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body class="bg-light d-flex align-items-center vh-100">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-sm border-0 rounded-3">
                <div class="card-header bg-primary text-white text-center py-3">
                    <h4 class="mb-0 fw-bold"><i class="bi bi-shield-check"></i> XÁC THỰC OTP</h4>
                </div>
                <div class="card-body p-4">
                    <div class="alert alert-success small text-center mb-4">
                        Mã OTP gồm 6 chữ số đã được gửi đến email:<br>
                        <strong>${sessionScope.reset_email}</strong>
                    </div>
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger shadow-sm"><i class="bi bi-exclamation-triangle-fill"></i> ${requestScope.error}</div>
                    </c:if>
                    <div class="text-center mb-3">
                        <span id="countdownText" class="fw-bold text-danger fs-5">Thời gian còn lại: 05:00</span>
                    </div>
                    <form action="${pageContext.request.contextPath}/auth" method="post" id="verifyForm">
                        <input type="hidden" name="action" value="verify-otp">
                        <div class="mb-4">
                            <label class="form-label fw-bold">Nhập Mã OTP</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-123"></i></span>
                                <input type="text" class="form-control text-center fw-bold fs-4 tracking-widest" name="otpInput" id="otpInput" placeholder=" _ _ _ _ _ _ " required pattern="\d{6}" maxlength="6">
                            </div>
                        </div>
                        <button type="submit" id="btnVerify" class="btn btn-primary w-100 fw-bold py-2">
                            Xác Nhận & Tiếp Tục <i class="bi bi-arrow-right-circle"></i>
                        </button>
                    </form>
                    <form action="${pageContext.request.contextPath}/auth" method="post" id="resendForm" style="display: none;" class="mt-3">
                        <input type="hidden" name="action" value="send-otp">
                        <input type="hidden" name="email" value="${sessionScope.reset_email}">
                        <button type="submit" class="btn btn-outline-danger w-100 fw-bold py-2 shadow-sm">
                            <i class="bi bi-arrow-clockwise"></i> Mã hết hạn! Bấm để gửi lại OTP
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const expiryTimeStr = "${not empty sessionScope.otp_expiry ? sessionScope.otp_expiry : 0}";
    const expiryTime = parseInt(expiryTimeStr) || 0;
    const timerDisplay = document.getElementById('countdownText');
    const btnVerify = document.getElementById('btnVerify');
    const inputOtp = document.getElementById('otpInput');
    const resendForm = document.getElementById('resendForm');

    if (expiryTime > 0) {
        const countdownInterval = setInterval(function() {
            const now = new Date().getTime();
            const distance = expiryTime - now;
            if (distance <= 0) {
                clearInterval(countdownInterval);
                timerDisplay.innerHTML = "MÃ OTP ĐÃ HẾT HẠN!";
                timerDisplay.classList.replace('text-danger', 'text-muted');
                inputOtp.disabled = true;
                btnVerify.disabled = true;
                btnVerify.classList.replace('btn-primary', 'btn-secondary');
                resendForm.style.display = 'block';
            } else {
                const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((distance % (1000 * 60)) / 1000);
                timerDisplay.innerHTML = "Thời gian còn lại: " + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
            }
        }, 1000);
    } else {
        timerDisplay.innerHTML = "Không tìm thấy thời gian hiệu lực!";
    }
</script>
</body>
</html>