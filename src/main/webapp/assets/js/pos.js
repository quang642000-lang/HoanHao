/* =================================================================
HỆ THỐNG JAVASCRIPT CHO TRANG POS (BÁN HÀNG)
================================================================= */
const formatCurrency = (number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(number);
let cart = JSON.parse(sessionStorage.getItem('tea_pos_cart')) || [];
let currentVoucher = null;
let currentProductVariants = [];
let customerPoints = 0;
let isUsingPoints = false;
let customPointsToUse = 0;

// Các biến quản lý Thanh toán QR
let checkPaymentInterval = null;
let countdownInterval = null;
let qrTimeout = null;
let isPaymentActive = false;
let editingCartId = null;
let optionModal = null;
let qrModal = null;

// ================= HÀM HỖ TRỢ BẢO MẬT XSS =================
function escapeHTML(str) {
    if(!str) return '';
    return str.replace(/[&<>'"]/g, function(tag) {
        const chars = { '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' };
        return chars[tag] || tag;
    });
}

// ================= FIX LỖI KẸT GIỎ HÀNG TRÊN MOBILE =================
function closeMobileCart() {
    let offcanvasElement = document.getElementById('mobileCartOffcanvas');
    if (!offcanvasElement) return;
    try {
        let bsOffcanvas = bootstrap.Offcanvas.getInstance(offcanvasElement);
        if (bsOffcanvas) {
            bsOffcanvas.hide();
        } else {
            forceCloseOffcanvas(offcanvasElement);
        }
    } catch (err) {
        forceCloseOffcanvas(offcanvasElement);
    }
}

function forceCloseOffcanvas(el) {
    el.classList.remove('show');
    document.querySelectorAll('.offcanvas-backdrop').forEach(b => b.remove());
    document.body.style.overflow = '';
    document.body.style.paddingRight = '';
}

// ================= KHỞI TẠO DOM =================
document.addEventListener("DOMContentLoaded", function() {
    optionModal = new bootstrap.Modal(document.getElementById('optionModal'));
    qrModal = new bootstrap.Modal(document.getElementById('qrModal'));
    let offcanvasElement = document.getElementById('mobileCartOffcanvas');
    if (offcanvasElement) {
        let closeBtn = offcanvasElement.querySelector('.btn-close');
        if (closeBtn) {
            closeBtn.removeAttribute('data-bs-dismiss');
            closeBtn.addEventListener('click', closeMobileCart);
        }
    }

    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('offcanvas-backdrop')) {
            closeMobileCart();
        }
    });

    let receiptElement = document.getElementById('receiptModal');
    if(receiptElement) {
        sessionStorage.removeItem('tea_pos_cart');
        cart = [];
        let myModal = new bootstrap.Modal(receiptElement);
        myModal.show();
        setTimeout(() => { printReceipt(); }, 500);
        // Gọi API xóa thông tin bill tạm
        fetch(appBasePath + '/ban-hang?action=clear-bill').catch(e => console.log(e));
    } else {
        renderCart();
    }
});

// ================= HÀM IN HÓA ĐƠN =================
function printReceipt() {
    const receiptContent = document.getElementById('printable-receipt-content').innerHTML;
    const iframe = document.createElement('iframe');
    iframe.style.position = 'absolute';
    iframe.style.top = '-9999px';
    iframe.style.left = '-9999px';
    document.body.appendChild(iframe);

    const doc = iframe.contentWindow.document;
    const style = `<style>@page { margin: 0; } body { font-family: 'Courier New', Courier, monospace; margin: 0; padding: 5mm; width: 70mm; color: #000; } table { width: 100%; border-collapse: collapse; } hr { border-top: 1px dashed #000; opacity: 1; margin: 8px 0; background: none; }</style>`;

    doc.open();
    doc.write('<html><head>' + style + '</head><body>' + receiptContent + '</body></html>');
    doc.close();

    iframe.onload = function() {
        iframe.contentWindow.focus();
        iframe.contentWindow.print();
        setTimeout(() => { document.body.removeChild(iframe); }, 2000);
    };
}

// ================= MODAL CHỌN MÓN =================
function openOptionsModal(maSP, tenSP) {
    editingCartId = null;
    document.getElementById('btn-confirm-modal').innerHTML = '<i class="bi bi-cart-plus me-2"></i> THÊM VÀO ĐƠN';
    let decodedTenSP = tenSP.replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&#034;/g, '"').replace(/&#039;/g, "'");
    document.getElementById('modalProductName').innerText = decodedTenSP;
    currentProductVariants = window.allVariants.filter(v => v.maSP === maSP);

    if (currentProductVariants.length === 0) {
        showToast("Sản phẩm này chưa được thiết lập Size để bán!", "danger");
        return;
    }

    let sizeHtml = '';
    currentProductVariants.forEach(function(v, index) {
        let checked = index === 0 ? "checked" : "";
        sizeHtml += `<input type='radio' class='btn-check' name='modalSizeRadio' id='size_${v.maBT}' value='${v.maBT}' ${checked}>
        <label class='btn btn-outline-primary fw-bold rounded-3 px-3 py-2' for='size_${v.maBT}'>Size ${escapeHTML(v.size)} <br> <small class='text-dark'>${formatCurrency(v.price)}</small></label>`;
    });

    document.getElementById('sizeContainer').innerHTML = sizeHtml;
    document.querySelectorAll('input[id^="tp_qty_"]').forEach(function(inp) { inp.value = 0; });
    document.getElementById('modalDa').value = '100%';
    document.getElementById('modalDuong').value = '100%';
    optionModal.show();
}

// ================= CHỈNH SỬA MÓN =================
function editCartItem(cartId) {
    let item = cart.find(i => i.cartId === cartId);
    if (!item) return;
    editingCartId = cartId;
    document.getElementById('modalProductName').innerText = item.tenGoc;
    document.getElementById('btn-confirm-modal').innerHTML = '<i class="bi bi-check2-circle me-2"></i> CẬP NHẬT MÓN';
    currentProductVariants = window.allVariants.filter(v => v.maSP === item.maSP);

    let sizeHtml = '';
    currentProductVariants.forEach(function(v) {
        let checked = (v.maBT === item.maBT) ? "checked" : "";
        sizeHtml += `<input type='radio' class='btn-check' name='modalSizeRadio' id='size_${v.maBT}' value='${v.maBT}' ${checked}>
        <label class='btn btn-outline-primary fw-bold rounded-3 px-3 py-2' for='size_${v.maBT}'>Size ${escapeHTML(v.size)} <br> <small class='text-dark'>${formatCurrency(v.price)}</small></label>`;
    });

    document.getElementById('sizeContainer').innerHTML = sizeHtml;
    document.getElementById('modalDa').value = item.da;
    document.getElementById('modalDuong').value = item.duong;
    document.querySelectorAll('input[id^="tp_qty_"]').forEach(function(inp) {
        inp.value = 0;
    });

    if (item.toppings) {
        item.toppings.forEach(function(tp) {
            let tpInput = document.getElementById('tp_qty_' + tp.id);
            if (tpInput) tpInput.value = tp.qty;
        });
    }
    optionModal.show();
}

// ================= XÁC NHẬN THÊM MÓN VÀO GIỎ =================
function confirmAddToCart() {
    let selectedSizeRadio = document.querySelector('input[name="modalSizeRadio"]:checked');
    if (!selectedSizeRadio) {
        showToast("Vui lòng chọn Size món nước!", "warning");
        return;
    }

    let maBT = selectedSizeRadio.value;
    let selectedVariant = currentProductVariants.find(v => v.maBT === maBT);
    let tenGoc = document.getElementById('modalProductName').innerText;
    let ten = tenGoc + " (Size " + selectedVariant.size + ")";
    let gia = selectedVariant.price;
    let da = document.getElementById('modalDa').value;
    let duong = document.getElementById('modalDuong').value;
    let toppings = [];
    let extraToppingPrice = 0;

    document.querySelectorAll('input[id^="tp_qty_"]').forEach(function(inp) {
        let qty = parseInt(inp.value);
        if (qty > 0) {
            let id = inp.getAttribute('data-id');
            let name = inp.getAttribute('data-name');
            let price = parseInt(inp.getAttribute('data-price'));
            toppings.push({ id: id, name: name, price: price, qty: qty });
            extraToppingPrice += (price * qty);
        }
    });

    let cartId = maBT + "_" + da + "_" + duong;
    if (toppings.length > 0) {
        cartId += "_" + toppings.map(function(t) { return t.id + "-" + t.qty; }).join('_');
    }

    let qtyToSet = 1;
    if (editingCartId) {
        let oldItemIndex = cart.findIndex(i => i.cartId === editingCartId);
        if (oldItemIndex > -1) {
            qtyToSet = cart[oldItemIndex].soLuong;
            cart.splice(oldItemIndex, 1);
        }
        editingCartId = null;
    }

    let existingItem = cart.find(item => item.cartId === cartId);
    if (existingItem) {
        existingItem.soLuong += qtyToSet;
    } else {
        cart.push({
            cartId: cartId,
            maSP: selectedVariant.maSP,
            tenGoc: tenGoc,
            maBT: maBT,
            ten: ten,
            giaGoc: gia,
            size: selectedVariant.size,
            giaChot: gia + extraToppingPrice,
            soLuong: qtyToSet,
            da: da,
            duong: duong,
            toppings: toppings
        });
    }
    optionModal.hide();
    checkVoucherValid();
    renderCart();
}

// ================= RENDER GIỎ HÀNG =================
function renderCart() {
    sessionStorage.setItem('tea_pos_cart', JSON.stringify(cart));
    const container = document.getElementById('cart-items-container');
    container.innerHTML = '';
    let badgeObjEmpty = document.getElementById('mobileCartBadge');

    if (cart.length === 0) {
        container.innerHTML = `<div class='text-center text-muted mt-5' id='empty-cart-msg'><i class='bi bi-cart-x text-secondary opacity-25' style='font-size: 4rem;'></i><p class='mt-3 fw-medium'>Chưa có món nào được chọn</p></div>`;
        document.getElementById('btn-checkout').disabled = true;
        removeVoucher(true, true);
        document.getElementById('display_tongTienHang').innerText = "0 ₫";
        document.getElementById('display_tienGiamGia').innerText = "- 0 ₫";
        document.getElementById('display_tongPhaiTra').innerText = "0 ₫";
        document.getElementById('input_tongTienHang').value = 0;
        document.getElementById('input_tienGiamGia').value = 0;
        document.getElementById('input_tongPhaiTra').value = 0;
        document.getElementById('row_giamDiem').style.setProperty('display', 'none', 'important');
        isUsingPoints = false;
        if(document.getElementById('toggleDiem')) {
            document.getElementById('toggleDiem').checked = false;
        }
        handlePaymentMethodChange();
        if (badgeObjEmpty) badgeObjEmpty.innerText = "0";
        return;
    }

    document.getElementById('btn-checkout').disabled = false;
    let tongTienHang = 0, tienGiamGia = 0, tienGiamDiem = 0, diemThucTeSuDung = 0;

    cart.forEach(function(item, index) {
        tongTienHang += item.giaChot * item.soLuong;
        let toppingTotalOneLy = 0;
        let tpStr = "";
        if (item.toppings && item.toppings.length > 0) {
            tpStr = `<div class="mt-2 w-100">`;
            item.toppings.forEach(t => {
                let tpTotal = t.price * t.qty;
                toppingTotalOneLy += tpTotal;
                tpStr += `
                <div class="d-flex justify-content-between small text-muted mb-1 ps-2" style="border-left: 2px solid #CBD5E1;">
                    <span>+ ${t.qty} x ${escapeHTML(t.name)}</span>
                    <span>${formatCurrency(tpTotal)}</span>
                </div>`;
            });
            tpStr += `</div>`;
        }

        let basePrice = item.giaGoc || (item.giaChot - toppingTotalOneLy);
        let sttHtml = `<span class="badge rounded-circle d-flex align-items-center justify-content-center me-2 shadow-sm text-white" style="width: 26px; height: 26px; font-size: 0.85rem; flex-shrink: 0; background-color: var(--brand); margin-top: 2px;">${index + 1}</span>`;
        let itemHtml = `
        <div class='p-3 border-bottom bg-white'>
            <div class='d-flex justify-content-between align-items-start'>
                <div class='d-flex align-items-start flex-grow-1 pe-2'>
                    ${sttHtml}
                    <div class="w-100">
                        <h6 class='mb-1 fw-bold text-dark'>${escapeHTML(item.ten)}</h6>
                        <div class='small text-muted mb-2 fw-medium'>Đá: ${escapeHTML(item.da)} &bull; Đường: ${escapeHTML(item.duong)}</div>
                        <div class="d-flex justify-content-between small fw-semibold text-dark mb-1">
                            <span>Giá nước x1</span><span>${formatCurrency(basePrice)}</span>
                        </div>
                        ${tpStr}
                    </div>
                </div>
            </div>
            <div class='d-flex justify-content-between align-items-center mt-3 pt-2 border-top border-light'>
                <div>
                    <a href='javascript:void(0)' class='text-primary small text-decoration-none me-3 fw-bold' onclick="editCartItem('${item.cartId}')"><i class='bi bi-pencil-square'></i> Sửa</a>
                    <a href='javascript:void(0)' class='text-danger small text-decoration-none fw-bold' onclick="updateQty('${item.cartId}', -999)"><i class='bi bi-trash'></i> Xóa</a>
                </div>
                <div class="d-flex align-items-center">
                    <div class='btn-group btn-group-sm shadow-sm me-3'>
                        <button type='button' class='btn btn-light border fw-bold px-2' onclick="updateQty('${item.cartId}', -1)"><i class='bi bi-dash-lg'></i></button>
                        <span class='btn btn-white border fw-bold px-3 text-primary' style='pointer-events: none; background: #fff;'>${item.soLuong}</span>
                        <button type='button' class='btn btn-light border fw-bold px-2' onclick="updateQty('${item.cartId}', 1)"><i class='bi bi-plus-lg'></i></button>
                    </div>
                    <div class='text-end'><h6 class='mb-0 fw-bold text-danger' style="font-size: 1.1rem;">${formatCurrency(item.giaChot * item.soLuong)}</h6></div>
                </div>
            </div>
        </div>`;
        container.insertAdjacentHTML('beforeend', itemHtml);
    });

    // Tính toán Voucher
    if (currentVoucher) {
        tienGiamGia = currentVoucher.loai === 'Phần Trăm' ? (tongTienHang * currentVoucher.giaTri) / 100 : currentVoucher.giaTri;
        if(tienGiamGia > tongTienHang) tienGiamGia = tongTienHang;
        document.getElementById('input_maKM').value = currentVoucher.id;
    }
    let tienSauVoucher = tongTienHang - tienGiamGia;

    // Tính toán Điểm Tích Lũy
    if (isUsingPoints && customerPoints > 0) {
        let maxPointsCanUse = Math.floor(tienSauVoucher / 1000);
        diemThucTeSuDung = (customPointsToUse > maxPointsCanUse) ? maxPointsCanUse : customPointsToUse;
        tienGiamDiem = diemThucTeSuDung * 1000;
        document.getElementById('row_giamDiem').style.setProperty('display', 'flex', 'important');
        document.getElementById('display_giamDiem').innerText = "- " + formatCurrency(tienGiamDiem);
    } else {
        document.getElementById('row_giamDiem').style.setProperty('display', 'none', 'important');
    }

    let tongPhaiTra = tienSauVoucher - tienGiamDiem;

    document.getElementById('display_tongTienHang').innerText = formatCurrency(tongTienHang);
    document.getElementById('display_tienGiamGia').innerText = "- " + formatCurrency(tienGiamGia);
    document.getElementById('display_tongPhaiTra').innerText = formatCurrency(tongPhaiTra);

    document.getElementById('input_tongTienHang').value = tongTienHang;
    document.getElementById('input_tienGiamGia').value = tienGiamGia;
    document.getElementById('input_diemSuDung').value = diemThucTeSuDung;
    document.getElementById('input_tongPhaiTra').value = tongPhaiTra;
    handlePaymentMethodChange();

    let totalItems = cart.reduce((sum, item) => sum + item.soLuong, 0);
    if(badgeObjEmpty) {
        badgeObjEmpty.innerText = totalItems;
        badgeObjEmpty.classList.add('animate__animated', 'animate__rubberBand');
        setTimeout(() => badgeObjEmpty.classList.remove('animate__animated', 'animate__rubberBand'), 500);
    }
}

// ================= CÁC TÁC VỤ GIỎ HÀNG KHÁC =================
function clearCart() {
    if(cart.length === 0) return;
    showConfirmAction("Xóa Giỏ Hàng", "Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng hiện tại?", function() {
        cart = [];
        sessionStorage.removeItem('tea_pos_cart');
        document.getElementById('tienKhachDua').value = '';
        document.getElementById('sdtKhachHang').value = '';
        document.getElementById('tenKhachHang').value = '';
        document.getElementById('customerInfoPanel').style.display = 'none';
        customerPoints = 0;
        isUsingPoints = false;
        customPointsToUse = 0;
        renderCart();
        if (window.innerWidth < 992) closeMobileCart();
    });
}

function updateQty(cartId, change) {
    let idx = cart.findIndex(i => i.cartId === cartId);
    if (idx > -1) {
        cart[idx].soLuong += change;
        if (cart[idx].soLuong <= 0) cart.splice(idx, 1);
    }
    checkVoucherValid();
    isUsingPoints ? calculateCustomPoints() : renderCart();
}

function changeModalTpQty(id, amount) {
    let input = document.getElementById('tp_qty_' + id);
    let val = parseInt(input.value) + amount;
    if(val >= 0) input.value = val;
}

// ================= VOUCHER (MÃ KHUYẾN MÃI) =================
function checkAndApplyVoucher() {
    let codeInput = document.getElementById('inputVoucherCode').value.trim().toUpperCase();
    if (codeInput === '') {
        showToast("Vui lòng nhập mã giảm giá!", "danger");
        return;
    }
    let found = window.availableVouchers.find(v => v.code === codeInput);
    if (found) {
        let now = new Date().getTime();
        if (now < found.start) {
            showToast("Mã giảm giá này chưa đến ngày sử dụng!", "warning");
            return;
        }
        if (now > (found.end + 86399000)) {
            showToast("Mã giảm giá này đã hết hạn!", "danger");
            return;
        }
        let tongTienHang = cart.reduce((sum, item) => sum + (item.giaChot * item.soLuong), 0);
        if (tongTienHang >= found.min) {
            currentVoucher = found;
            document.getElementById('activeVoucherInfo').style.display = 'flex';
            document.getElementById('voucherLabel').innerText = escapeHTML(found.code);
            document.getElementById('inputVoucherCode').value = '';
            isUsingPoints ? calculateCustomPoints() : renderCart();
        } else {
            showToast('Đơn hàng chưa đạt mức tối thiểu ' + formatCurrency(found.min) + ' để áp dụng mã!', "danger");
        }
    } else {
        showToast("Mã không hợp lệ hoặc đã hết lượt dùng!", "danger");
    }
}

function removeVoucher(skipRender = false, silent = false) {
    currentVoucher = null;
    document.getElementById('input_maKM').value = '';
    document.getElementById('activeVoucherInfo').style.display = 'none';
    if(!skipRender) {
        isUsingPoints ? calculateCustomPoints() : renderCart();
    }
}

function checkVoucherValid() {
    if(currentVoucher) {
        let tongTest = cart.reduce((sum, item) => sum + (item.giaChot * item.soLuong), 0);
        if(tongTest < currentVoucher.min) removeVoucher(true, true);
    }
}

// ================= THANH TOÁN =================
function handlePaymentMethodChange() {
    let ptttSelect = document.getElementById('select_pttt');
    let ptttName = ptttSelect.options[ptttSelect.selectedIndex].text.toLowerCase();
    let tienKhachDuaInput = document.getElementById('tienKhachDua');
    let phaiTra = parseInt(document.getElementById('input_tongPhaiTra').value) || 0;
    if (ptttName.includes("tiền mặt") || ptttName.includes("cash")) {
        tienKhachDuaInput.readOnly = false;
        tienKhachDuaInput.classList.remove('bg-light');
        if(tienKhachDuaInput.value == phaiTra) tienKhachDuaInput.value = '';
    } else {
        tienKhachDuaInput.readOnly = true;
        tienKhachDuaInput.classList.add('bg-light');
        tienKhachDuaInput.value = phaiTra;
    }
    calculateChange();
}

function calculateChange() {
    let khachDua = parseInt(document.getElementById('tienKhachDua').value) || 0;
    let phaiTra = parseInt(document.getElementById('input_tongPhaiTra').value) || 0;
    let ptttSelect = document.getElementById('select_pttt');
    let ptttName = ptttSelect.options[ptttSelect.selectedIndex].text.toLowerCase();
    let container = document.getElementById('tienThuaContainer');

    if ((ptttName.includes("tiền mặt") || ptttName.includes("cash")) && khachDua >= phaiTra && phaiTra > 0) {
        container.style.display = 'block';
        document.getElementById('tienThuaLabel').innerText = formatCurrency(khachDua - phaiTra);
    } else {
        container.style.display = 'none';
    }
}

function validateCheckout(event) {
    event.preventDefault();
    let phaiTra = parseInt(document.getElementById('input_tongPhaiTra').value) || 0;
    let ptttSelect = document.getElementById('select_pttt');
    let ptttName = ptttSelect.options[ptttSelect.selectedIndex].text.toLowerCase();
    if (ptttName.includes("tiền mặt") || ptttName.includes("cash")) {
        let khachDua = parseInt(document.getElementById('tienKhachDua').value) || 0;
        if (khachDua < phaiTra) {
            showToast("Số tiền khách đưa không đủ để thanh toán hóa đơn!", "danger");
            return false;
        }
    } else {
        document.getElementById('tienKhachDua').value = phaiTra;
    }

    const h = document.getElementById('hidden-cart-inputs');
    h.innerHTML = '';
    cart.forEach(function(item, idx) {
        let inputs = `<input type='hidden' name='itemIndex[]' value='${idx}'>
        <input type='hidden' name='tenMon_${idx}' value='${escapeHTML(item.ten)}'>
        <input type='hidden' name='maBT_${idx}' value='${item.maBT}'>
        <input type='hidden' name='soLuong_${idx}' value='${item.soLuong}'>
        <input type='hidden' name='giaChot_${idx}' value='${item.giaChot}'>
        <input type='hidden' name='da_${idx}' value='${escapeHTML(item.da)}'>
        <input type='hidden' name='duong_${idx}' value='${escapeHTML(item.duong)}'>`;

        item.toppings.forEach(function(tp) {
            inputs += `<input type='hidden' name='toppings_${idx}[]' value='${tp.id}|${tp.qty}|${tp.price}|${escapeHTML(tp.name)}'>`;
        });
        h.insertAdjacentHTML('beforeend', inputs);
    });

    if (ptttName.includes("tiền mặt") || ptttName.includes("cash")) {
        let khachDua = parseInt(document.getElementById('tienKhachDua').value) || 0;
        showConfirmAction("Xác Nhận Thanh Toán", `Thu đủ ${formatCurrency(khachDua)} tiền mặt?`, () => document.getElementById('checkout-form').submit());
    } else {
        if (phaiTra <= 0) {
            document.getElementById('checkout-form').submit();
            return false;
        }
        document.getElementById('qrAmount').innerText = formatCurrency(phaiTra);
        let transactionCode = "TEA" + new Date().getFullYear().toString().slice(-2) + String(new Date().getMonth() + 1).padStart(2, '0') + String(new Date().getDate()).padStart(2, '0') + Math.floor(1000 + Math.random() * 9000);
        document.getElementById('qrCodeDisplay').innerText = transactionCode;
        document.getElementById('qrImage').src = `https://img.vietqr.io/image/TPB-0346406405-compact2.png?amount=${phaiTra}&addInfo=${transactionCode}`;

        document.getElementById('qrSuccessOverlay').style.setProperty('display', 'none', 'important');
        let expiredOverlay = document.getElementById('qrExpiredOverlay');
        if(expiredOverlay) expiredOverlay.style.setProperty('display', 'none', 'important');

        let loadingStatus = document.getElementById('qrLoadingStatus');
        loadingStatus.style.setProperty('display', 'flex', 'important');
        loadingStatus.innerHTML = '<div class="spinner-border spinner-border-sm me-2" role="status"></div><span class="text-primary">Hệ thống đang chờ tiền vào...</span>';

        isPaymentActive = true;
        if (checkPaymentInterval) clearInterval(checkPaymentInterval);
        if (countdownInterval) clearInterval(countdownInterval);
        if (qrTimeout) clearTimeout(qrTimeout);
        qrModal.show();

        let timeLeft = 60;
        let countdownEl = document.getElementById('qrCountdownText');
        if(countdownEl) countdownEl.innerText = timeLeft;
        countdownInterval = setInterval(function() {
            timeLeft--;
            if (timeLeft >= 0 && countdownEl) countdownEl.innerText = timeLeft;
            if (timeLeft <= 0) {
                clearInterval(countdownInterval);
                if(expiredOverlay) expiredOverlay.style.setProperty('display', 'flex', 'important');
                loadingStatus.innerHTML = '<div class="spinner-grow spinner-grow-sm text-warning me-2" role="status"></div><span class="text-warning fw-bold">Mã đã ẩn, nhưng hệ thống vẫn chờ khách chuyển...</span>';
            }
        }, 1000);

        checkPaymentInterval = setInterval(function() {
            if (!isPaymentActive) {
                clearInterval(checkPaymentInterval);
                return;
            }
            fetch(appBasePath + '/api/check-payment?code=' + transactionCode)
                .then(response => response.json())
                .then(data => {
                    if (!isPaymentActive) return;
                    if (data.status === 'success') {
                        isPaymentActive = false;
                        clearInterval(checkPaymentInterval);
                        if (countdownInterval) clearInterval(countdownInterval);
                        if(expiredOverlay) expiredOverlay.style.setProperty('display', 'none', 'important');
                        document.getElementById('qrLoadingStatus').style.setProperty('display', 'none', 'important');
                        document.getElementById('qrSuccessOverlay').style.setProperty('display', 'flex', 'important');
                        setTimeout(() => {
                            qrModal.hide();
                            document.getElementById('checkout-form').submit();
                        }, 1500);
                    }
                }).catch(e => console.log("Lỗi mạng check QR:", e));
        }, 3000);
    }
    return false;
}

function cancelQRPayment() {
    isPaymentActive = false;
    if (checkPaymentInterval) clearInterval(checkPaymentInterval);
    if (countdownInterval) clearInterval(countdownInterval);
    if (qrTimeout) clearTimeout(qrTimeout);
    qrModal.hide();
}

function forceSubmitCheckout() {
    isPaymentActive = false;
    if (checkPaymentInterval) clearInterval(checkPaymentInterval);
    if (countdownInterval) clearInterval(countdownInterval);
    if (qrTimeout) clearTimeout(qrTimeout);
    qrModal.hide();
    document.getElementById('checkout-form').submit();
}

// ================= THÔNG TIN KHÁCH HÀNG & ĐIỂM TÍCH LŨY =================
function checkCustomerPhone() {
    let phone = document.getElementById('sdtKhachHang').value;
    document.getElementById('toggleDiem').checked = false;
    isUsingPoints = false;
    customerPoints = 0;
    customPointsToUse = 0;

    if (phone.length >= 10) {
        fetch(appBasePath + '/ban-hang?action=check-phone&phone=' + phone)
            .then(res => res.json())
            .then(data => {
                if (data.found) {
                    document.getElementById('tenKhachHang').value = data.tenKH;
                    document.getElementById('tenKhachHang').readOnly = true;
                    document.getElementById('lblTenKH').innerText = data.tenKH;
                    document.getElementById('lblDiem').innerText = data.diem;
                    customerPoints = parseInt(data.diem);
                    document.getElementById('customerInfoPanel').style.display = 'block';
                    document.getElementById('newCustomerPanel').style.display = 'none';
                } else {
                    document.getElementById('tenKhachHang').value = '';
                    document.getElementById('tenKhachHang').readOnly = false;
                    document.getElementById('customerInfoPanel').style.display = 'none';
                    document.getElementById('newCustomerPanel').style.display = 'block';
                }
                renderCart();
            });
    } else {
        document.getElementById('tenKhachHang').readOnly = false;
        document.getElementById('customerInfoPanel').style.display = 'none';
        document.getElementById('newCustomerPanel').style.display = 'none';
        renderCart();
    }
}

function applyPoints() {
    isUsingPoints = document.getElementById('toggleDiem').checked;
    if(isUsingPoints) {
        document.getElementById('nhapDiemContainer').style.display = 'flex';
        useMaxPoints();
    } else {
        document.getElementById('nhapDiemContainer').style.display = 'none';
        customPointsToUse = 0;
        document.getElementById('input_nhapDiemTay').value = 0;
    }
    renderCart();
}

function getMaxAllowedPoints() {
    let tongHang = cart.reduce((sum, item) => sum + (item.giaChot * item.soLuong), 0);
    let giamVoucher = 0;
    if (currentVoucher && tongHang >= currentVoucher.min) {
        giamVoucher = currentVoucher.loai === 'Phần Trăm' ? (tongHang * currentVoucher.giaTri) / 100 : currentVoucher.giaTri;
    }
    let maxPointsForBill = Math.floor((tongHang - giamVoucher) / 1000);
    return (customerPoints > maxPointsForBill) ? maxPointsForBill : customerPoints;
}

function calculateCustomPoints() {
    let inputVal = parseInt(document.getElementById('input_nhapDiemTay').value) || 0;
    let maxAllowed = getMaxAllowedPoints();
    if (inputVal > maxAllowed) inputVal = maxAllowed;
    document.getElementById('input_nhapDiemTay').value = inputVal;
    customPointsToUse = inputVal;
    renderCart();
}

function useMaxPoints() {
    let maxPts = getMaxAllowedPoints();
    document.getElementById('input_nhapDiemTay').value = maxPts;
    customPointsToUse = maxPts;
    renderCart();
}