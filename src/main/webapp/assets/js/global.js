/* =================================================================
HỆ THỐNG JAVASCRIPT DÙNG CHUNG - DỰ ÁN TEA POS
================================================================= */
let confirmActionCallback = null;
let jsConfirmModalObj = null;

// ================= SỰ KIỆN KHI TRANG LOAD XONG =================
document.addEventListener("DOMContentLoaded", function() {
    // 1. Khởi tạo Modal Xác nhận chung
    let confirmModalEl = document.getElementById('jsConfirmModal');
    if (confirmModalEl) {
        jsConfirmModalObj = new bootstrap.Modal(confirmModalEl);
        let btnYes = document.getElementById('jsConfirmYesBtn');
        if (btnYes) {
            btnYes.addEventListener('click', function() {
                if (typeof confirmActionCallback === 'function') {
                    confirmActionCallback();
                }
                confirmActionCallback = null; // Reset callback
                jsConfirmModalObj.hide();
            });
        }
    }

    // 2. Xử lý Active Menu và Trạng thái Sidebar
    let sidebarMenu = document.getElementById("main-sidebar-menu");
    if (sidebarMenu) {
        let currentPath = window.location.pathname;
        let menuItems = document.querySelectorAll("#main-sidebar-menu .menu-item");
        menuItems.forEach(function(link) {
            link.classList.remove('active');
        });

        let foundActive = false;
        menuItems.forEach(function(link) {
            let href = link.getAttribute('href');
            if (href && href.includes('/ban-hang')) return; // Bỏ qua link POS
            if (href && currentPath.includes(href)) {
                link.classList.add('active');
                foundActive = true;
            }
        });

        // Nếu không có menu nào active, mặc định chọn Dashboard
        if (!foundActive && (currentPath.endsWith('/admin') || currentPath.endsWith('/admin.jsp'))) {
            let dashboardLink = document.querySelector('#main-sidebar-menu a[href$="/admin"]');
            if(dashboardLink) dashboardLink.classList.add('active');
        }

        // Khôi phục trạng thái Sidebar (Collapsed) trên Desktop
        if (window.innerWidth >= 992 && localStorage.getItem('sidebarState') === 'collapsed') {
            let sidebar = document.getElementById('sidebar');
            if (sidebar) sidebar.classList.add('collapsed');
            let mainContent = document.querySelector('.main-content');
            if (mainContent) mainContent.classList.add('expanded');
        }
    }
});

// ================= CÁC HÀM GỌI MODAL XÁC NHẬN =================
function showConfirmAction(title, text, callback) {
    let titleEl = document.getElementById('jsConfirmTitle');
    let textEl = document.getElementById('jsConfirmText');
    if (titleEl) titleEl.innerText = title;
    if (textEl) textEl.innerText = text;
    confirmActionCallback = callback;
    if (jsConfirmModalObj) {
        jsConfirmModalObj.show();
    } else {
        console.warn("Lỗi: jsConfirmModal chưa được khởi tạo trên trang này.");
    }
}

function showConfirmLink(title, text, url) {
    showConfirmAction(title, text, function() {
        window.location.href = url;
    });
}

function showConfirmForm(event, formElement, title, text) {
    event.preventDefault();
    showConfirmAction(title, text, function() {
        formElement.submit();
    });
}

// ================= HÀM XỬ LÝ SIDEBAR =================
function toggleSidebar() {
    let sidebar = document.getElementById('sidebar');
    let mainContent = document.querySelector('.main-content');
    let overlay = document.getElementById('sidebarOverlay');
    if (window.innerWidth >= 992) {
        if (sidebar) sidebar.classList.toggle('collapsed');
        if (mainContent) mainContent.classList.toggle('expanded');
        // Lưu trạng thái
        if (sidebar) {
            localStorage.setItem('sidebarState', sidebar.classList.contains('collapsed') ? 'collapsed' : 'expanded');
        }
    } else {
        if (sidebar) sidebar.classList.toggle('show');
        if (overlay) overlay.classList.toggle('show');
    }
}

// ================= HÀM HIỂN THỊ THÔNG BÁO (TOAST) =================
function showToast(message, type = 'success') {
    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '1055';
        document.body.appendChild(toastContainer);
    }

    let bgClass = 'bg-success';
    let iconClass = 'bi-check-circle-fill';

    if (type === 'danger' || type === 'error') {
        bgClass = 'bg-danger';
        iconClass = 'bi-x-circle-fill';
    } else if (type === 'warning') {
        bgClass = 'bg-warning text-dark';
        iconClass = 'bi-exclamation-triangle-fill';
    } else if (type === 'info') {
        bgClass = 'bg-info text-dark';
        iconClass = 'bi-info-circle-fill';
    }

    let toastId = 'toast_' + Date.now();
    let toastHtml = `
    <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="d-flex">
            <div class="toast-body fw-bold d-flex align-items-center" style="font-size: 0.95rem;">
                <i class="bi ${iconClass} me-2 fs-5"></i><span>${message}</span>
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    </div>
    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    let toastEl = document.getElementById(toastId);
    let bsToast = new bootstrap.Toast(toastEl, { delay: 4000 });
    bsToast.show();

    // Dọn dẹp DOM
    toastEl.addEventListener('hidden.bs.toast', function () {
        toastEl.remove();
    });
}