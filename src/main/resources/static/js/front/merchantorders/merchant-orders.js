// 篩選訂單
function filterOrders() {
    const status = document.getElementById('status-filter').value;
    window.location.href = `/store/orders/manage?status=${status}`;
}

// 顯示拒絕訂單彈窗
function showRejectModal(orderId) {
    const modal = document.getElementById('reject-order-modal');
    const form = document.getElementById('reject-order-form');
    form.action = `/store/orders/reject/${orderId}`;
    modal.style.display = 'block';
}

// 關閉拒絕訂單彈窗
function closeRejectModal() {
    const modal = document.getElementById('reject-order-modal');
    modal.style.display = 'none';

    // 重置表單
    const form = document.getElementById('reject-order-form');
    form.reset();
    document.getElementById('custom-reason-container').style.display = 'none';
}

// 監聽拒絕原因選項變化
document.addEventListener('DOMContentLoaded', function() {
    const reasonRadios = document.querySelectorAll('input[name="cancelReason"]');
    const customReasonContainer = document.getElementById('custom-reason-container');

    reasonRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.value === '其他') {
                customReasonContainer.style.display = 'block';
                document.getElementById('customReason').required = true;
            } else {
                customReasonContainer.style.display = 'none';
                document.getElementById('customReason').required = false;
            }
        });
    });
});