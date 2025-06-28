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


    // ================= WebSocket 即時通知邏輯 =================
    const body = document.querySelector('body');
    const storeId = body.dataset.storeId; // 從 <body> 的 data-store-id 屬性獲取商家ID

    if (storeId) {
        connectWebSocket(storeId);
    }

    function connectWebSocket(storeId) {
        const protocol = window.location.protocol === 'https' ? 'wss://' : 'ws://';
        const host = window.location.host;
        const wsUrl = `${protocol}${host}/ws/orders/${storeId}`;

        const websocket = new WebSocket(wsUrl);

        websocket.onopen = function(event) {
            console.log('WebSocket 連線成功！');
        };

        // 核心：接收到伺服器推播的訊息
        websocket.onmessage = function(event) {
            console.log('收到新訂單通知:', event.data);
            try {
                const newOrder = JSON.parse(event.data);
                addNewOrderCard(newOrder); // 呼叫函式來動態新增訂單卡片
            } catch (e) {
                console.error('解析新訂單資料失敗:', e);
            }
        };

        websocket.onclose = function(event) {
            console.log('WebSocket 連線已關閉。');
            // 可在此處加入 5 秒後自動重連的邏輯
            setTimeout(() => connectWebSocket(storeId), 5000);
        };

        websocket.onerror = function(event) {
            console.error('WebSocket 發生錯誤:', event);
        };
    }

    /**
     * 動態建立並在頁面頂部插入新的訂單卡片
     * @param {object} order - 從 WebSocket 收到的新訂單物件
     */
    function addNewOrderCard(order) {
        const ordersList = document.querySelector('.orders-list');
        if (!ordersList) return;

        // 建立新的訂單卡片 div
        const newCard = document.createElement('div');
        newCard.className = 'order-card pending'; // 新訂單預設為待處理狀態

        // 使用模板字符串填充 HTML 結構 (此處為簡化版，請根據您的 VO/DTO 調整)
        // 注意：日期格式化和訂單詳情可能需要額外處理
        newCard.innerHTML = `
            <div class="order-header">
                <div class="order-info">
                    <h3 class="order-id">訂單 #${order.ordId}</h3>
                    <span class="order-time">${new Date(order.ordDate).toLocaleString()}</span>
                </div>
                <div class="order-status">
                    <span class="status-badge pending">待處理</span>
                </div>
            </div>
            <div class="order-details">
                <!-- 根據您的需求填充客戶資訊、訂單項目等 -->
                <p><strong>客戶：</strong>${order.member.memName}</p>
                <p><strong>訂單總額：NT$${order.actualPayment}</strong></p>
            </div>
            <div class="order-actions">
                <!-- 根據您的需求填充操作按鈕 -->
                <form action="/store/orders/accept/${order.ordId}" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-success">接受訂單</button>
                </form>
                <button type="button" class="btn btn-danger" onclick="showRejectModal(${order.ordId})">拒絕訂單</button>
            </div>
        `;

        // 將新卡片插入到列表的最前面
        ordersList.prepend(newCard);

        // 可選：新增音效提示或視覺效果
        newCard.style.backgroundColor = '#fff3cd'; // 臨時高亮
        setTimeout(() => { newCard.style.backgroundColor = ''; }, 2000);

        // 可選：播放提示音
        // const audio = new Audio('/sounds/notification.mp3');
        // audio.play();
    }
});